package com.prototype.gbcontacttracing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {

    private val code = getRandomString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginVerifyButton.setOnClickListener{
            if(validEmailAddress(emailBox)) {
                val email = emailBox.text.toString()
                sendPostRequest(this.code, email)
                val intent = Intent(this, VerifyActivity::class.java)
                intent.putExtra("Code", code)
                startActivity(intent)
            }
        }
    }

    private fun validEmailAddress(emailBox: EditText?): Boolean {
        val email = emailBox?.text.toString()
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendPostRequest(password:String, email:String) {

        var reqParam = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode("196bcd9c-23c4-11eb-adc1-0242ac120002", "UTF-8")
        reqParam += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        reqParam += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
        val mURL = URL("http://p4pproto.sites.gettysburg.edu/GBContactTracing/verify.php")

        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                println("Response : $response")
            }
        }
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }
}