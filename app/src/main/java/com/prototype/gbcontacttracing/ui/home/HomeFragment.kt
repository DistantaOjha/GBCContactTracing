package com.prototype.gbcontacttracing.ui.home
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.prototype.gbcontacttracing.R
import com.prototype.gbcontacttracing.bluetoothManager.BleManager
import java.util.*
import kotlin.math.pow


class HomeFragment : Fragment() {

    private lateinit var inflatedView: View
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .build()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            ScanResult.DATA_COMPLETE

            with(result.device) {
                Log.d(
                    "Scan Callback",
                    "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address, strength: ${result.rssi}"
                )

                val serviceData =
                    result.scanRecord?.getServiceData(ParcelUuid(UUID.fromString("00001234-0000-1000-8000-00805F9B34FB")))

                if (serviceData != null) {
                    val string = String(serviceData)
                    Log.d("We received data:", string)
                    val distanceInFeets = 10.0.pow((-69 - (result.rssi)) / (10.0 * 2)) * 3.28084
                    Log.d("At distance:", distanceInFeets.toString())
                }
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
            BleManager.startAdvertising()
            Log.d("Hanuman", "banuman pressed button")
        }

        return inflatedView
    }




}