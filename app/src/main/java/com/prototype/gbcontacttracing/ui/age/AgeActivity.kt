package com.prototype.gbcontacttracing.ui.age

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.activity_age.*

class AgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)

        underageButton.setOnClickListener {
            val intent = Intent(this, UnderageActivity::class.java)
            startActivity(intent)
        }

        legalAgeButton.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
    }
}