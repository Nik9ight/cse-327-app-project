package com.example.myllmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
<<<<<<< Updated upstream
import androidx.lifecycle.lifecycleScope
import com.example.myllmapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
=======
<<<<<<< Updated upstream
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
=======
import androidx.lifecycle.lifecycleScope
import com.example.myllmapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
>>>>>>> Stashed changes
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes

class MainActivity : AppCompatActivity() {
    private lateinit var llmw: LLMW // LLMW class instance
    private var isModelLoaded = false
    private lateinit var modelPath: String
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

<<<<<<< Updated upstream
        // Use view binding to access layout views
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
=======
<<<<<<< Updated upstream
        signInButton = findViewById(R.id.sign_in_button)
        inputText = findViewById(R.id.input_text)
        invokeM1Button = findViewById(R.id.invoke_m1_button)
        invokeM2Button = findViewById(R.id.invoke_m2_button)
        resultView = findViewById(R.id.result_view)
>>>>>>> Stashed changes

        // Initialize LLMW instance
        llmw = LLMW()

        // Configure Google Sign-In
        configureGoogleSignIn()

        try {
            // Load model path from assets and prepare for inference
            lifecycleScope.launch {
                modelPath = preparePath()
                llmw.load(modelPath)
                isModelLoaded = true
            }

            // Google Sign-In button click listener
            binding.signInButton.setOnClickListener {
                signIn()
            }

            // Setup action listener for the edit text to listen for "Done" button
            binding.inputText.setOnEditorActionListener { textView, actionID, _ ->
                if (actionID == EditorInfo.IME_ACTION_DONE || actionID == EditorInfo.IME_ACTION_NEXT) {
                    val question = binding.inputText.text.toString()
                    textView.text = "" // Clear the input field
                    if (isModelLoaded) {
                        llmw.send(question, object : LLMW.MessageHandler {
                            override fun h(msg: String) {
                                // Append response to the TextView on the UI thread
                                runOnUiThread {
                                    val allText = binding.resultView.text.toString() + msg
                                    binding.resultView.text = allText
                                }
                            }
                        })
                    } else {
                        Toast.makeText(this, "Model is not loaded yet.", Toast.LENGTH_SHORT).show()
                    }
                }
                false
            }

            // M1 button listener to handle model inference
            binding.invokeM1Button.setOnClickListener {
                if (!isModelLoaded) {
                    Toast.makeText(this, "Model is not loaded yet.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val input = binding.inputText.text.toString()
                if (input.isNotBlank()) {
                    // Run model inference on background thread
                    llmw.send(input, object : LLMW.MessageHandler {
                        override fun h(msg: String) {
                            // Display model response in resultView
                            runOnUiThread {
                                val allText = binding.resultView.text.toString() + msg
                                binding.resultView.text = allText
                            }
                        }
                    })
                } else {
                    Toast.makeText(this, "Please enter some text.", Toast.LENGTH_SHORT).show()
                }
            }

            // M2 button listener (Placeholder)
            binding.invokeM2Button.setOnClickListener {
                if (!isModelLoaded) {
                    Toast.makeText(this, "Model is not loaded yet.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val input = binding.inputText.text.toString()
                if (input.isNotBlank()) {
                    // Placeholder behavior for M2 model
                    val result = "[M2] Processed input: $input"
                    runOnUiThread {
                        binding.resultView.text = result
                    }
                } else {
                    Toast.makeText(this, "Please enter some text.", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (ex: Exception) {
            // Handle any errors during model load
            Toast.makeText(this, "Load error: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }

<<<<<<< Updated upstream
    // Method to prepare the model file from the app's resources and copy to cache directory
    private fun preparePath(): String {
        try {
            // Open model file from raw resources
            val inputStream: InputStream = resources.openRawResource(R.raw.tinyllama)

            // Create a temporary file in the cache directory
            val tempFile = File.createTempFile("myfile", ".gguf", cacheDir)
            val outputStream: OutputStream = FileOutputStream(tempFile)

            // Copy the model file data from inputStream to the temp file
=======
=======
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        llmw = LLMW()

        configureGoogleSignIn()

        // 🚨 Move model load to coroutine on IO thread
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("MainActivity", "Preparing model file...")
                modelPath = preparePath()

                Log.d("MainActivity", "Loading model on IO thread...")
                llmw.load(modelPath)

                // Mark model loaded on main thread
                withContext(Dispatchers.Main) {
                    isModelLoaded = true
                    Toast.makeText(this@MainActivity, "M1 model loaded!", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Model loaded and ready.")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading model: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Load error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Google Sign-In button
        binding.signInButton.setOnClickListener {
            signIn()
        }

        // Text input listener
        binding.inputText.setOnEditorActionListener { textView, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_DONE || actionID == EditorInfo.IME_ACTION_NEXT) {
                val question = binding.inputText.text.toString()
                textView.text = "" // Clear the field
                handleM1Inference(binding, question)
            }
            false
        }

        // M1 button listener
        binding.invokeM1Button.setOnClickListener {
            val input = binding.inputText.text.toString()
            handleM1Inference(binding, input)
        }

        // M2 button listener placeholder //n
        binding.invokeM2Button.setOnClickListener {
            // Start M2 activity directly
            val intent = Intent(this, M2::class.java)
            startActivity(intent)
        } //n

    }

    private fun handleM1Inference(binding: ActivityMainBinding, input: String) {
        if (!isModelLoaded) {
            Toast.makeText(this, "Model is not loaded yet.", Toast.LENGTH_SHORT).show()
            return
        }
        if (input.isNotBlank()) {
            llmw.send(input, object : LLMW.MessageHandler {
                override fun h(msg: String) {
                    runOnUiThread {
                        val allText = binding.resultView.text.toString() + msg
                        binding.resultView.text = allText
                    }
                }
            })
        } else {
            Toast.makeText(this, "Please enter some text.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preparePath(): String {
        try {
            val inputStream: InputStream = resources.openRawResource(R.raw.tinyllama)
            val tempFile = File.createTempFile("myfile", ".gguf", cacheDir)
            val outputStream: OutputStream = FileOutputStream(tempFile)

>>>>>>> Stashed changes
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

<<<<<<< Updated upstream
            // Return the path to the temp file
            return tempFile.absolutePath

=======
            return tempFile.absolutePath
>>>>>>> Stashed changes
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Error preparing model path: ${e.message}")
        }
    }

<<<<<<< Updated upstream
    // Google Sign-In setup
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    // Google Sign-In logic
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

<<<<<<< Updated upstream
    // Handle sign-in result
=======
<<<<<<< Updated upstream
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
=======
>>>>>>> Stashed changes
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Toast.makeText(this, "Signed in as ${account.displayName}", Toast.LENGTH_SHORT).show()
                } catch (e: ApiException) {
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
        }
}
