package com.prototype.gbcontacttracing.ui.emailVerification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_underage.*

class LoginActivity : AppCompatActivity() {

    private val code = getRandomString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        loginVerifyButton.setOnClickListener{
            if(validEmailAddress(emailBox)) {

                val email = emailBox.text.toString().trim()

                val intent = Intent(this, VerifyActivity::class.java)
                intent.putExtra("code", getRandomString())
                intent.putExtra("email", email)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validEmailAddress(emailBox: EditText?): Boolean {
        val email = emailBox?.text.toString()
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }
}