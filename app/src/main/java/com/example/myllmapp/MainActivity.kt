package com.example.myllmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myllmapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var llmw: LLMW // LLMW class instance
    private var isModelLoaded = false
    private lateinit var modelPath: String
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use view binding to access layout views
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    // Method to prepare the model file from the app's resources and copy to cache directory
    private fun preparePath(): String {
        try {
            // Open model file from raw resources
            val inputStream: InputStream = resources.openRawResource(R.raw.tinyllama)

            // Create a temporary file in the cache directory
            val tempFile = File.createTempFile("myfile", ".gguf", cacheDir)
            val outputStream: OutputStream = FileOutputStream(tempFile)

            // Copy the model file data from inputStream to the temp file
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            // Return the path to the temp file
            return tempFile.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Error preparing model path: ${e.message}")
        }
    }

    // Google Sign-In setup
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

    // Handle sign-in result
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
        }
}
