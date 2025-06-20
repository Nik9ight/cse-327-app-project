package com.example.myllmapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val loginBtn = findViewById<Button>(R.id.login_button)
        val registerBtn = findViewById<Button>(R.id.register_button)

        loginBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, PermissionActivity::class.java)) // Placeholder for future RegisterActivity
        }
    }
}