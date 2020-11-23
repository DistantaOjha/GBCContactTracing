package com.prototype.gbcontacttracing.ui.age

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.activity_underage.*

class UnderageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_underage)

        backButton.setOnClickListener {
            val intent = Intent(this, AgeActivity::class.java)
            startActivity(intent)
        }

        hyperlink.movementMethod = LinkMovementMethod.getInstance()
    }
}