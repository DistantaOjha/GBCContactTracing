package com.prototype.gbcontacttracing.ui.age

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.ui.emailVerification.LoginActivity
import kotlinx.android.synthetic.main.activity_age.*

class AgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age)

        underageButton.setOnClickListener{
            val intent = Intent(this, UnderageActivity::class.java)
            startActivity(intent)
        }

        legalAgeButton.setOnClickListener{
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
    }
}