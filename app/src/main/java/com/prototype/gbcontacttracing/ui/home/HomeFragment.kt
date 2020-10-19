package com.prototype.gbcontacttracing.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {


    private fun startBleScan(){

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        
        
        //scan_bli.setOnClickListener( startBleScan())


        return inflater.inflate(R.layout.fragment_home, container, false)
    }




}