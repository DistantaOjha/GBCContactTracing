package com.prototype.gbcontacttracing.ui.emailVerification

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prototype.gbcontacttracing.MainActivity
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.activity_verify.*


class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        backToLoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        submitButton.setOnClickListener{

            val intentLoginActivity = intent
            val email = intentLoginActivity.extras!!.getString("email")

            val originalCode  = intentLoginActivity.extras!!.getString("code")
            val userEnteredCode = codeBox.text.toString().trim()


            val sharedPref = getSharedPreferences("GBContactTracing", MODE_PRIVATE);
            val editor = sharedPref.edit()
            editor.putString("user_id", email)
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}