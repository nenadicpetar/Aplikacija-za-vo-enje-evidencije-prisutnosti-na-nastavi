package com.example.feritep

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaos.view.PinView

class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val pinView = findViewById<PinView>(R.id.pin_view)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            if(pinView.text.toString().equals("1111")) {
                val intent = Intent(this, NewPasswordActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Neispravan PIN!", Toast.LENGTH_LONG).show()
                pinView.text?.clear()
            }
        }
    }
}