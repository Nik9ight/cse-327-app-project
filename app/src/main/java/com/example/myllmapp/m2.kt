//pull
package com.example.myllmapp

import android.os.Bundle
import android.widget.*
import android.net.Uri
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import ai.onnxruntime.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.sqrt

class M2 : AppCompatActivity() {

    private val textEmbeddings = mutableMapOf<String, FloatArray>()
    private var ortEnv: OrtEnvironment? = null
    private var ortSessionVision: OrtSession? = null
    private var ortSessionText: OrtSession? = null
    private var selectedImageUri: Uri? = null
    private lateinit var tokenizer: SimpleTokenizer

    private val labels = listOf(
        "a dog", "a cat", "a mountain", "a street", "a beach", "people", "food",
        "a car", "a soccer ball", "a forest", "a sunset", "a river", "a horse", "a tree", "a building",
        "a bridge", "a boat", "a bird", "a phone", "a laptop", "a cup ", "a bicycle", "a bus", "a train", "a clock",
        "a lamp", "a book", "a chair", "a table", "a shoe", "a jacket", "a hat", "a guitar", "a piano", "a camera", "a television",
        "a pizza", "a sandwich", "a cake", "a flower", "a butterfly", "a fish", "a cat playing", "a dog running", "a child smiling",
        "a woman reading", "a man walking", "a car driving", "a mountain range", "a snowy landscape", "a sunny beach", "a rainy street",
        "a nighttime city", "a fireworks display", "a birthday party", "a wedding ceremony", "a soccer game", "a basketball court",
        "a swimming pool", "a playground", "a concert stage", "a museum interior", "a library shelf", "a supermarket aisle",
        "a kitchen counter", "a garden", "a zoo enclosure", "a desert scene", "a waterfall", "a hot air balloon", "a mountain bike",
        "a skateboard", "a helicopter", "a plane flying", "a train station", "a subway platform", "a park bench", "a street sign",
        "a traffic light", "a mailbox", "a mailbox with letters", "a dog sleeping", "a cat chasing a mouse", "a child playing with toys",
        "a family dinner", "a man fishing", "a woman cooking", "a chef in a kitchen", "a fire truck", "an ambulance", "a police car",
        "a school bus", "a playground slide", "a treehouse", "a roller coaster", "a ferris wheel", "a carnival", "a parade",
        "a snowman", "a pumpkin patch"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m2)

        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val runModelButton = findViewById<Button>(R.id.runModelButton)
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val optionSpinner = findViewById<Spinner>(R.id.optionSelector)
        val customTextInput = findViewById<EditText>(R.id.customTextInput)

        val options = listOf("Check image label", "Test similarity with custom text")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        optionSpinner.adapter = adapter

        optionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                customTextInput.visibility = if (position == 1) View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Load model and embeddings in background
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                ortEnv = OrtEnvironment.getEnvironment()
                val sessionOptions = OrtSession.SessionOptions()

                val visionModelPath = copyAssetToFile("clip_model.ort")
                val textModelPath = copyAssetToFile("clip_text.ort")

                ortSessionVision = ortEnv!!.createSession(visionModelPath, sessionOptions)
                ortSessionText = ortEnv!!.createSession(textModelPath, sessionOptions)

                tokenizer = SimpleTokenizer(this@M2)

                // Precompute label embeddings
                for (label in labels) {
                    val tensor = preprocessText(label)
                    val output = ortSessionText!!.run(mapOf("input.1" to tensor))
                    val embedding = (output[0].value as Array<FloatArray>)[0]
                    textEmbeddings[label] = embedding
                }

                runOnUiThread {
                    Toast.makeText(this@M2, "M2 model loaded!", Toast.LENGTH_SHORT).show()
                }
                Log.d("CLIP", "Models and embeddings ready")

            } catch (e: Exception) {
                Log.e("CLIP", "Failed to load: ${e.message}")
                runOnUiThread {
                    resultTextView.text = "Model load error: ${e.message}"
                }
            }
        }

        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                imagePreview.setImageURI(it)
                resultTextView.text = "Image selected. Press 'Run Model'."
            }
        }

        selectImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        runModelButton.setOnClickListener {
            val selectedOption = optionSpinner.selectedItem.toString()
            val uri = selectedImageUri
            if (uri == null) {
                resultTextView.text = "Please select an image first."
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val imageTensor = preprocessImage(uri, this@M2)
                    val imageOutput = ortSessionVision!!.run(mapOf("input.1" to imageTensor))
                    val imageEmbedding = imageOutput[0].value as Array<FloatArray>

                    val textResult = if (selectedOption == "Check image label") {
                        var bestLabel = ""
                        var bestScore = -1f
                        for ((label, embedding) in textEmbeddings) {
                            val score = cosineSimilarity(imageEmbedding[0], embedding)
                            if (score > bestScore) {
                                bestScore = score
                                bestLabel = label
                            }
                        }
                        "This is a picture of: $bestLabel ".format(bestScore)
                    } else {
                        val customText = customTextInput.text.toString().trim()
                        if (customText.isEmpty()) {
                            "Please enter a label to match."
                        } else {
                            val textTensor = preprocessText(customText)
                            val textOutput = ortSessionText!!.run(mapOf("input.1" to textTensor))
                            val textEmbedding = (textOutput[0].value as Array<FloatArray>)[0]
                            val score = cosineSimilarity(imageEmbedding[0], textEmbedding)
                            "Similarity with \"$customText\": %.3f".format(score)
                        }
                    }

                    runOnUiThread {
                        resultTextView.text = textResult
                    }

                } catch (e: Exception) {
                    Log.e("CLIP", "Inference failed: ${e.message}")
                    runOnUiThread {
                        resultTextView.text = "Inference failed: ${e.message}"
                    }
                }
            }
        }
    }

    private fun copyAssetToFile(fileName: String): String {
        val file = File(filesDir, fileName)
        if (!file.exists()) {
            assets.open(fileName).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        }
        return file.absolutePath
    }

    private fun preprocessImage(uri: Uri, context: Context): OnnxTensor {
        val bitmap = if (Build.VERSION.SDK_INT >= 29) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }.copy(Bitmap.Config.ARGB_8888, true)

        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val floatValues = FloatBuffer.allocate(3 * 224 * 224)

        val mean = floatArrayOf(0.481f, 0.457f, 0.408f)
        val std = floatArrayOf(0.268f, 0.261f, 0.275f)

        for (c in 0..2) {
            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val pixel = resized.getPixel(x, y)
                    val value = when (c) {
                        0 -> ((pixel shr 16) and 0xFF) / 255.0f
                        1 -> ((pixel shr 8) and 0xFF) / 255.0f
                        else -> (pixel and 0xFF) / 255.0f
                    }
                    floatValues.put((value - mean[c]) / std[c])
                }
            }
        }
        floatValues.rewind()
        return OnnxTensor.createTensor(ortEnv, floatValues, longArrayOf(1, 3, 224, 224))
    }

    private fun preprocessText(text: String): OnnxTensor {
        val tokenIdsList = tokenizer.tokenize(text.lowercase())
        val tokenIds = if (tokenIdsList.size > 77) tokenIdsList.subList(0, 77).toIntArray()
        else IntArray(77) { i -> tokenIdsList.getOrNull(i) ?: 0 }
        return tokensToTensor(tokenIds)
    }

    private fun tokensToTensor(tokens: IntArray): OnnxTensor {
        val buffer = IntBuffer.wrap(tokens)
        return OnnxTensor.createTensor(ortEnv, buffer, longArrayOf(1, 77))
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        return dot / (sqrt(normA) * sqrt(normB))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            ortSessionVision?.close()
            ortSessionText?.close()
            ortEnv?.close()
            Log.d("CLIP", "M2 resources released")
        } catch (e: Exception) {
            Log.e("CLIP", "Error closing sessions: ${e.message}")
        }
    }
}
