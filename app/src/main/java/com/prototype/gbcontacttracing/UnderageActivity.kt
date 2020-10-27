package com.prototype.gbcontacttracing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_underage.*

class UnderageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_underage)

        backButton.setOnClickListener{
            val intent = Intent(this, AgeActivity::class.java)
            startActivity(intent)
        }

        hyperlink.movementMethod = LinkMovementMethod.getInstance()
    }
}