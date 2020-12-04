package com.prototype.gbcontacttracing.ui.emailVerification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private val verificationServerEndPoint = "http://p4pproto.sites.gettysburg.edu/GBContactTracing/verify.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        loginVerifyButton.setOnClickListener {
            if (validEmailAddress(emailBox)) {

                val email = emailBox.text.toString().trim()
                val code = getRandomString()
                val requestBody =
                    "{\"id\":\"196bcd9c-23c4-11eb-adc1-0242ac120002\",\"code\":\"$code\",\"email\":\"$email\"}"
                Log.i("requestBody", requestBody)

                Fuel.post(verificationServerEndPoint)
                    .jsonBody(requestBody)
                    .also { println(it) }
                    .response { result ->
                        val (byte, error) = result
                        if (byte != null) {
                            Log.i("Result is", String(byte))
                        } else {
                            Log.i("Result is", "Nothing")
                        }

                        if (error != null) {
                            Log.e("Error http request", "email verification")
                        }

                    }

                val intent = Intent(this, VerifyActivity::class.java)
                intent.putExtra("code", code)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun validEmailAddress(emailBox: EditText?): Boolean {
        val email = emailBox?.text.toString()
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }
}