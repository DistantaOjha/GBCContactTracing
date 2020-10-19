package com.prototype.gbcontacttracing.bluetoothManager
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanSettings
import android.content.Context

class BleManager {

    companion object {

        private lateinit var bluetoothManager: BluetoothManager
        private lateinit var bluetoothAdapter: BluetoothAdapter
        private lateinit var bleScanner: BluetoothLeScanner
        var isScanning = false

        fun setBluetooth(activity: Activity) {
            bluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            bleScanner = bluetoothAdapter.bluetoothLeScanner
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
    }


}