package com.prototype.gbcontacttracing

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.prototype.gbcontacttracing.bluetoothManager.BleManager
import com.prototype.gbcontacttracing.ui.HomeFragment
import com.prototype.gbcontacttracing.ui.InfoFragment
import com.prototype.gbcontacttracing.ui.ReleaseFragment
import com.prototype.gbcontacttracing.ui.age.AgeActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2


class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onResume() {
        super.onResume()
        requestLocationPermission()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                //If user denies the permission for bluetooth
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            print("permission allowed")
            return
        }
        runOnUiThread {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Locations Permission Required")
            dialogBuilder.setMessage("Location Permission is required in order for this app to run and use Bluetooth Low Energy Services. The app will also BLE services in background.")
            dialogBuilder.setPositiveButton(
                "Allow Location"
            ) { _: DialogInterface, _: Int ->
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

            val alert = dialogBuilder.create()
            alert.show()

        }

    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BleManager.setBluetooth(this)
        bluetoothAdapter = BleManager.getBleAdapter()

        val sharedPref = getSharedPreferences("GBContactTracing", MODE_PRIVATE)
        val userId: String? = sharedPref.getString("user_id", "Null")

        if (userId.equals("Null")) {
            val intent = Intent(this, AgeActivity::class.java)
            startActivity(intent)
        } else {
            val homeFragment = HomeFragment()
            val releaseFragment = ReleaseFragment()
            val infoFragment = InfoFragment()

            makeCurrentFragment(homeFragment)

            nav_view.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> makeCurrentFragment(homeFragment)
                    R.id.navigation_release -> makeCurrentFragment(releaseFragment)
                    R.id.navigation_info -> makeCurrentFragment(infoFragment)
                }
                true
            }
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }

}