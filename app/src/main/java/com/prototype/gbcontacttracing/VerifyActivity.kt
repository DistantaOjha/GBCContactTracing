package com.prototype.gbcontacttracing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_age.*
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        submitButton.setOnClickListener{
            if(verification(codeBox.text.toString())){
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Invalid Code, Try Again!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun verification(input:String) : Boolean {
        if(input == intent.getStringExtra("Code").toString()){
            return true
        }
        return false
    }
}