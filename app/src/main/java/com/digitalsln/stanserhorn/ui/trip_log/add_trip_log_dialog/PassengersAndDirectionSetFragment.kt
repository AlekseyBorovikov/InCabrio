package com.digitalsln.stanserhorn.ui.trip_log.add_trip_log_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalsln.stanserhorn.databinding.FragmentPassengersDirectionsBinding

class PassengersAndDirectionSetFragment: Fragment() {

    private var _binding: FragmentPassengersDirectionsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPassengersDirectionsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}