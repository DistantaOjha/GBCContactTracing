package com.prototype.gbcontacttracing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_age.*
import kotlinx.android.synthetic.main.activity_underage.*
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        submitButton.setOnClickListener{
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}