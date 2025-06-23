//package com.example.myllmapp

package com.example.myllmapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {
    private lateinit var signInButton: Button
    private lateinit var inputText: EditText
    private lateinit var invokeM1Button: Button
    private lateinit var invokeM2Button: Button
    private lateinit var resultView: TextView

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Uses updated template layout

        signInButton = findViewById(R.id.sign_in_button)
        inputText = findViewById(R.id.input_text)
        invokeM1Button = findViewById(R.id.invoke_m1_button)
        invokeM2Button = findViewById(R.id.invoke_m2_button)
        resultView = findViewById(R.id.result_view)

        requestPermissions()
        configureGoogleSignIn()

        signInButton.setOnClickListener {
            signIn()
        }

        invokeM1Button.setOnClickListener {
            val input = inputText.text.toString()
            val result = runM1Model(input)
            resultView.text = result
        }

        invokeM2Button.setOnClickListener {
            val input = inputText.text.toString()
            val result = runM2Model(input)
            resultView.text = result
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, "Signed in as ${account.displayName}", Toast.LENGTH_LONG).show()
        } catch (e: ApiException) {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun runM1Model(input: String): String {
        return "[M1] Processed input: $input\n(Integrate M1 model here)"
    }

    private fun runM2Model(input: String): String {
        return "[M2] Processed input: $input\n(Integrate M2 model here)"
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val denied = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (denied.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, denied.toTypedArray(), 101)
        }
    }

}
//final git test