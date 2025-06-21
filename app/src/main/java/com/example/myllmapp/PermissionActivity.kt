//package com.example.myllmapp

package com.example.myllmapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 101
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val enableBtn = findViewById<Button>(R.id.enable_permissions)
        enableBtn.setOnClickListener {
            if (areAllPermissionsGranted()) {
                Toast.makeText(this, "All permissions already granted", Toast.LENGTH_SHORT).show()
                // All permissions are already granted, proceed with your logic
            } else {
                requestAllPermissions()
            }
        }
    }

    private fun requestAllPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val perms = HashMap<String, Int>()
            // Fill with actual results from user
            if (grantResults.isNotEmpty()) {
                for (i in permissions.indices) {
                    perms[permissions[i]] = grantResults[i]
                }
                // Check if all permissions are granted
                if (areAllPermissionsGranted(perms)) {
                    Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Some permissions were denied. Functionality may be limited.", Toast.LENGTH_LONG).show()
                    // Optionally, you can guide the user to settings or explain why permissions are needed.
                }
            }
        }
    }

    private fun areAllPermissionsGranted(perms: Map<String, Int>? = null): Boolean {
        for (permission in permissions) {
            val granted = perms?.get(permission) ?: ContextCompat.checkSelfPermission(this, permission)
            if (granted != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }
}
