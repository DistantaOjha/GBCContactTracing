package com.prototype.gbcontacttracing.bluetoothManager

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.prototype.gbcontacttracing.databaseManager.DataBaseManager
import java.util.*
import kotlin.math.pow

/**
 * This class controls BLE processing which includes scanning and advertising
 */
class BleManager {

    companion object {

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

        // if the difference between end time of the data and current time is less than this time then delete that record from the database
        private const val maxTimeDiff: Long = 120000

        // avg. distance to be considered the exposure
        private const val MIN_EXPOSURE_DISTANCE = 6 //in feet

        private val bleDataUUID =
            ParcelUuid(UUID.fromString("00001234-0000-1000-8000-00805F9B34FB"))

        private val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        //for each device found
        private val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                ScanResult.DATA_COMPLETE

                val token = result.device.name

                //check if device has a name
                if (token != null) {

                    //convert distance to feets
                    val distanceInFeet = 10.0.pow((-69 - (result.rssi)) / (10.0 * 2)) * 3.28084

                    val time = System.currentTimeMillis()

                    //if device has not been seen before. Put it into first seen map
                    if (!initTimeMap.containsKey(token)) {
                        initTimeMap[token] = time
                        distanceMap[token] = mutableListOf(distanceInFeet)
                    } else {
                        //if already seen then update the last seen time with current time
                        lastTimeMap[token] = time
                        (distanceMap[token] as MutableList<Double>?)?.add(distanceInFeet)
                    }

                    //if the device has been seen before at least twice
                    if (initTimeMap.containsKey(token) && lastTimeMap.containsKey(token)) {
                        //calculate exposure time as (listSeenTime - firstSeenTime)
                        val exposureTime =
                            initTimeMap[token]?.let { lastTimeMap[token]?.minus(it) }
                        //calculate average distance from the list of distances for each seen instance
                        val averageDistance = distanceMap[token]?.average()

                        if (exposureTime != null && averageDistance != null) {
                            //if it meets the condition
                            if (exposureTime > MIN_EXPOSURE_TIME && averageDistance < MIN_EXPOSURE_DISTANCE) {
                                initTimeMap[token]?.let {
                                    lastTimeMap[token]?.let { it1 ->
                                        //insert into the database
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

                //delete the data if there exist anything older than specified time
                db.deleteOldData(System.currentTimeMillis(), maxTimeDiff)
            }

        }

        private val scanFilter = ScanFilter.Builder()
            .setServiceUuid(bleDataUUID)
            .build()

        private val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
                Log.i("BLE", "LE Advertise success.")
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e("BLE", "Advertising onStartFailure: $errorCode")
                super.onStartFailure(errorCode)
            }
        }

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

        fun stopBleScan() {
            bleScanner.stopScan(scanCallback)
            isScanning = false
        }

        fun stopBleAdvertise() {
            bleAdvertiser.stopAdvertising(advertisingCallback)
        }

        fun getBleAdapter(): BluetoothAdapter {
            return bluetoothAdapter
        }


        fun startAdvertising() {

            val sendTOKEN =
                baseContext.getSharedPreferences("GBContactTracing", AppCompatActivity.MODE_PRIVATE)
                    .getString("user_id", "UNNAMED")

            if (sendTOKEN.equals("UNNAMED")) {
                Log.e("Error Advertising Data", "Bluetooth adapter not named")
            } else {
                //sending token as a name of the service. This compromise for android was made necessary because of communication constraints with iOS device
                    // we can still send as a service data. Need more research on the iOS side to make it more conventional
                bluetoothAdapter.name = sendTOKEN
                val settings = AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setConnectable(false)
                    .build()

                val data = AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(false)
                    .addServiceUuid(bleDataUUID)
                    .build()

                bleAdvertiser.startAdvertising(settings, data, advertisingCallback)
            }


        }

        fun initContext(baseContext: Context?) {
            if (baseContext != null) {
                this.baseContext = baseContext
            }
        }

    }


}