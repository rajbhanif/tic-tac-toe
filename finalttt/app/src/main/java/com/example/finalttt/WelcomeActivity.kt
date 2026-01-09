package com.example.finalttt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val pseudoEditText: EditText = findViewById(R.id.pseudoEditText)
        val commencerButton: Button = findViewById(R.id.commencerButton)

        commencerButton.setOnClickListener {
            val pseudo = pseudoEditText.text.toString().trim()
            if (pseudo.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("pseudo", pseudo) // Pass pseudo to MainActivity
                startActivity(intent)
                finish()
            } else {
                // Show a toast message if pseudo is empty
                Toast.makeText(this, "Veuillez entrer un pseudo afin de continuer", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
