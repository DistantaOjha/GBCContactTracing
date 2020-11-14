package com.prototype.gbcontacttracing.bluetoothManager

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.prototype.gbcontacttracing.databaseManager.DataBaseManager
import java.util.*
import kotlin.math.pow


class BleManager {

    companion object {

        private const val SEND_TOKEN = "haider"

        private lateinit var baseContext: Context
        private lateinit var bluetoothManager: BluetoothManager
        private lateinit var bluetoothAdapter: BluetoothAdapter
        private lateinit var bleScanner: BluetoothLeScanner
        private lateinit var bleAdvertiser: BluetoothLeAdvertiser
        private lateinit var db: DataBaseManager
        private var isScanning = false


        // Keeps track of when the device was first seen
        private val initTimeMap = mutableMapOf<String, Long>()

        //keeps track of when the device was last seen //keeps update for every new seen
        private val lastTimeMap = mutableMapOf<String, Long>()

        //keeps track of distances obtained from the subsequent scans
        private val distanceMap = mutableMapOf<String, List<Double>>()

        // KEEP MIN_EXPOSURE_TIME < DISAPPEAR TIME
        // difference between last seen time and first seen map to get into the database
        private const val MIN_EXPOSURE_TIME = 15000 //in milliseconds

        //difference between current time and last seen time for the device to be not in the periphery
        private const val DISAPPEAR_TIME = 20000 //in milliseconds

        // avg. distance to be considered the exposure
        private const val MIN_EXPOSURE_DISTANCE = 6 //in feet

        private val bleDataUUID =
            ParcelUuid(UUID.fromString("00001234-0000-1000-8000-00805F9B34FB"))

        private val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        private val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                ScanResult.DATA_COMPLETE

                val serviceData =
                    result.scanRecord?.getServiceData(bleDataUUID)
                if (serviceData != null) {

                    val token = String(serviceData)
                    val distanceInFeets = 10.0.pow((-69 - (result.rssi)) / (10.0 * 2)) * 3.28084

                    val time = System.currentTimeMillis()
                    if (!initTimeMap.containsKey(token)) {
                        initTimeMap[token] = time
                        distanceMap[token] = mutableListOf(distanceInFeets)
                    } else {
                        lastTimeMap[token] = time
                        (distanceMap[token] as MutableList<Double>?)?.add(distanceInFeets)
                    }


                    if (initTimeMap.containsKey(token) && lastTimeMap.containsKey(token)) {
                        val exposureTime =
                            initTimeMap[token]?.let { lastTimeMap[token]?.minus(it) }

                        val averageDistance = distanceMap[token]?.average()

                        if (exposureTime != null && averageDistance != null) {
                            if (exposureTime > MIN_EXPOSURE_TIME && averageDistance < MIN_EXPOSURE_DISTANCE) {
                                initTimeMap[token]?.let {
                                    lastTimeMap[token]?.let { it1 ->
                                        db.insertData(
                                            token,
                                            it,
                                            it1, averageDistance
                                        )
                                    }
                                }
                            }
                        }
                    }

                    //Clear the ones that have disappeared
                    val removedTokens = mutableListOf<String>()
                    for ((canToken, lastSeenTime) in lastTimeMap) {//candidate token
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastSeenTime > DISAPPEAR_TIME) {
                            Log.i("LOST DEVICE", canToken)
                            initTimeMap.remove(canToken)
                            distanceMap.remove(canToken)
                            removedTokens.add(canToken)
                        }
                    }

                    for (removedToken in removedTokens) {
                        //remove from the lastTimeMap
                        lastTimeMap.remove(removedToken)
                    }

                }
            }
        }

        private val scanFilter = ScanFilter.Builder()
            .setServiceUuid(bleDataUUID)
            .build()

        fun setBluetooth(activity: Activity) {
            bluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            this.bleScanner = bluetoothAdapter.bluetoothLeScanner
            bleAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        }

        fun startBleScan() {
            db = DataBaseManager(baseContext)
            db.setupTable()
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

            val sendData = SEND_TOKEN.toByteArray(Charsets.UTF_8)

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
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

        fun initContext(baseContext: Context?) {
            if (baseContext != null) {
                this.baseContext = baseContext
            }
        }

    }


}