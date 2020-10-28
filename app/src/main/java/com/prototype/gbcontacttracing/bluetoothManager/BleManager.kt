package com.prototype.gbcontacttracing.bluetoothManager

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.util.*


class BleManager {

    companion object {

        private lateinit var bluetoothManager: BluetoothManager
        private lateinit var bluetoothAdapter: BluetoothAdapter
        private lateinit var bleScanner: BluetoothLeScanner
        private lateinit var bleAdvertiser: BluetoothLeAdvertiser


        var isScanning = false

        fun setBluetooth(activity: Activity) {
            bluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            bleScanner = bluetoothAdapter.bluetoothLeScanner
            bleAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        }

        fun startBleScan(scanSettings: ScanSettings, scanCallback: ScanCallback) {
            bleScanner.startScan(null, scanSettings, scanCallback)
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


            //TODO : add token to advertise here
            val sixteenBit = ParcelUuid(UUID.fromString("00001234-0000-1000-8000-00805F9B34FB"))
            Log.i("id is:", sixteenBit.toString())
            val sendData = "hoang".toByteArray(Charsets.UTF_8)

            Log.i("Given data is: bytes", sendData.toString())
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(true)
                .addServiceData(sixteenBit, sendData)
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