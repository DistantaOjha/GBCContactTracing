package com.prototype.gbcontacttracing.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.bluetoothManager.BleManager
import com.prototype.gbcontacttracing.databaseManager.DataBaseManager

class ReleaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_release, container, false)
        val submitButton = inflatedView.findViewById(R.id.releaseButton) as Button

        val db = context?.let { DataBaseManager(it) }
        db?.setupTable()

        submitButton.setOnClickListener {

            val data = db?.readAllData()
            val user =
                context?.getSharedPreferences("GBContactTracing", AppCompatActivity.MODE_PRIVATE)
                    ?.getString("user_id", "UNNAMED")

            val requestBody =
                "{\"id\":\"196bcd9c-23c4-11eb-adc1-0242ac120002\",\"data\":\"$data\",\"user\":\"$user\"}"

            Log.i("requestBody", requestBody)

            Fuel.post("http://p4pproto.sites.gettysburg.edu/GBContactTracing/release.php")
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

        }

        return inflatedView
    }
}