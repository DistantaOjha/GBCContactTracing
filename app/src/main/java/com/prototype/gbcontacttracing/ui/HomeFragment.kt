package com.prototype.gbcontacttracing.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.backgroundProcess.BackGroundProcess


class HomeFragment : Fragment() {

    private lateinit var inflatedView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val isServiceRunning = BackGroundProcess.isAppInForeground
        inflatedView = inflater.inflate(R.layout.fragment_home, container, false)

        val serviceIntent = Intent(context, BackGroundProcess::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")

        val button = inflatedView.findViewById(R.id.scan_ble) as Button
        if (!isServiceRunning) {

            button.setBackgroundColor(Color.GREEN)
            button.setText(R.string.startService)

            button.setOnClickListener {
                Log.d(null, "Initiated background task")
                Toast.makeText(context, "Contact Tracing Service Started", Toast.LENGTH_SHORT)
                    .show()
                context?.startForegroundService(serviceIntent)
                BackGroundProcess.isAppInForeground = true
                parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
            }
        } else {
            button.setBackgroundColor(Color.RED)
            button.setText(R.string.stopService)
            button.setOnClickListener {
                context?.stopService(serviceIntent)
                BackGroundProcess.isAppInForeground = false
                parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
            }

        }
        return inflatedView
    }


}