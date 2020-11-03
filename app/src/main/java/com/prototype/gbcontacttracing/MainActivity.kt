package com.prototype.gbcontacttracing
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.prototype.gbcontacttracing.ui.home.HomeFragment
import com.prototype.gbcontacttracing.ui.infoPage.InfoFragment
import com.prototype.gbcontacttracing.ui.release.ReleaseFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val releaseFragment = ReleaseFragment()
        val infoFragment = InfoFragment()

        makeCurrentFragment(homeFragment)

        nav_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home -> makeCurrentFragment(homeFragment)
                R.id.navigation_release -> makeCurrentFragment(releaseFragment)
                R.id.navigation_info -> makeCurrentFragment(infoFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}