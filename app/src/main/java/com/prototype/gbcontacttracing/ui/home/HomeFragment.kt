package com.prototype.gbcontacttracing.ui.home
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.bluetoothManager.BleManager


class HomeFragment : Fragment() {

    private lateinit var inflatedView: View
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            with(result.device) {
                Log.d(
                    "Scan Callback",
                    "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address"
                )
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflatedView = inflater.inflate(R.layout.fragment_home, container, false)

        val button = inflatedView.findViewById(R.id.scan_ble) as Button
        button.setOnClickListener {
            BleManager.startBleScan(scanSettings, scanCallback)
            Log.d("Hanuman", "banuman pressed button")
        }

        return inflatedView
    }




}