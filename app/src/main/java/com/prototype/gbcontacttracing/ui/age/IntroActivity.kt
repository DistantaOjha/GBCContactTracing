package com.prototype.gbcontacttracing.ui.age

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.prototype.gbcontacttracing.MainActivity
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.ui.emailVerification.LoginActivity
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        startAppButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}