package com.prototype.gbcontacttracing.bluetoothManager
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.util.*
import kotlin.math.pow


class BleManager {

    companion object {

        private lateinit var bluetoothManager: BluetoothManager
        private lateinit var bluetoothAdapter: BluetoothAdapter
        private lateinit var bleScanner: BluetoothLeScanner
        private lateinit var bleAdvertiser: BluetoothLeAdvertiser

        private val initTimeMap = mutableMapOf<String, Long>()
        private val lastTimeMap = mutableMapOf<String, Long>()
        private val distanceMap = mutableMapOf<String, List<Double>>()

        private val bleDataUUID = ParcelUuid(UUID.fromString("00001234-0000-1000-8000-00805F9B34FB"))

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
                        result.scanRecord?.getServiceData(bleDataUUID)

                    if (serviceData != null) {
                        val token = String(serviceData)
                        val distanceInFeets = 10.0.pow((-69 - (result.rssi)) / (10.0 * 2)) * 3.28084

                        Log.d("We received data", token)
                        Log.d("At distance:", distanceInFeets.toString())

                        val time = System.currentTimeMillis()
                        if(!initTimeMap.containsKey(token)){
                            initTimeMap[token] = time
                            distanceMap[token] = mutableListOf(distanceInFeets)
                        }
                        else{
                            lastTimeMap[token] = time
                            (distanceMap[token] as MutableList<Double>?)?.add(distanceInFeets)
                        }

                        Log.d("Latest init time map is ", initTimeMap.toString())
                        Log.d("Latest last seen time map is ", lastTimeMap.toString())
                        Log.d("Latest distance map is ", distanceMap.toString())
                    }
                }
            }
        }

        private val scanFilter = ScanFilter.Builder()
            .setServiceUuid(bleDataUUID)
            .build()

        var isScanning = false

        fun setBluetooth(activity: Activity) {
            bluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            bleScanner = bluetoothAdapter.bluetoothLeScanner
            bleAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        }

        fun startBleScan() {
            bleScanner.startScan(mutableListOf<ScanFilter>(scanFilter), scanSettings, scanCallback)
            isScanning = true
        }

        fun stopBleScan(scanCallback: ScanCallback) {
            bleScanner.stopScan(scanCallback)
            isScanning = false
        }

        fun getBleScanner(): BluetoothLeScanner {
            return bleScanner
        }

        fun getBleAdapter(): BluetoothAdapter {
            return bluetoothAdapter
        }

        fun startAdvertising() {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(false)
                .build()


            val sendData = "paluman".toByteArray(Charsets.UTF_8)

            val i = Log.i("Given data is: bytes", sendData.toString())
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(bleDataUUID)
                .addServiceData(bleDataUUID, sendData)
                .build()

            val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                    Log.i("BLE", "LE Advertise success.")
                }

                override fun onStartFailure(errorCode: Int) {
                    Log.e("BLE", "Advertising onStartFailure: $errorCode")
                    super.onStartFailure(errorCode)
                }
            }
            bleAdvertiser.startAdvertising(settings, data, advertisingCallback)
        }

    }


}