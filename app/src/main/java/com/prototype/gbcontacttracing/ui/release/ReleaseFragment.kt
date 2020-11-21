package com.prototype.gbcontacttracing.ui.release

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.prototype.gbcontacttracing.R
import kotlinx.android.synthetic.main.fragment_release.*

class ReleaseFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_release, container, false)
    }
}