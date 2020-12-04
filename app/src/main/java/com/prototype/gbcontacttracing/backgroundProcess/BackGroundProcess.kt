package com.prototype.gbcontacttracing.backgroundProcess

import android.app.*
import android.content.Intent
import android.os.*
import android.widget.Toast
import com.prototype.gbcontacttracing.MainActivity
import com.prototype.gbcontacttracing.bluetoothManager.BleManager

/**
 * This class included initiation for background services which has SQLite operations and BLE operations
 */
class BackGroundProcess : Service() {


    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    //static
    companion object {
        @JvmField
        var isAppInForeground: Boolean = false
    }

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            //background work here
            try {
                BleManager.initContext(baseContext)
                BleManager.startBleScan()
                BleManager.startAdvertising()

            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", Process.BLUETOOTH_UID).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        isAppInForeground = true
        createNotificationChannel()
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = Notification.Builder(this, "GBContactTracing")
            .setContentTitle("Contact Tracing")
            .setContentText("You are using GBC contact tracing application")
            //.setSmallIcon(R.drawable.)
            .setContentIntent(pendingIntent)
            //.setTicker(getText(R.string.ticker_text))
            .build()

        startForeground(1, notification)

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        BleManager.stopBleScan()
        BleManager.stopBleAdvertise()
        Toast.makeText(this.baseContext, "Contact Tracing Service Ended", Toast.LENGTH_SHORT).show()
        isAppInForeground = false
    }

    //show notification that there's service running in the background
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "GBContactTracing",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }


}