package com.prototype.gbcontacttracing.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        inflatedView = inflater.inflate(R.layout.fragment_home, container, false)

        val button = inflatedView.findViewById(R.id.scan_ble) as Button
        button.setOnClickListener {
            Log.d(null, "Initiated background task")

            val serviceIntent = Intent(context, BackGroundProcess::class.java)
            serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")

            context?.startForegroundService(serviceIntent)
        }

        return inflatedView
    }


}