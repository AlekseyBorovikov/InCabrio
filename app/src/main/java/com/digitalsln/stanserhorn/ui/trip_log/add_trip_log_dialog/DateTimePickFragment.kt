package com.digitalsln.stanserhorn.ui.trip_log.add_trip_log_dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalsln.stanserhorn.databinding.FragmentDateTimeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.min

class DateTimePickFragment(
    private var hour: Int = 0,
    private var minutes: Int = 0,
): Fragment() {

    private var _binding: FragmentDateTimeBinding? = null
    private val binding get() = _binding

    init {
        if(hour == 0 && minutes == 0) {
            val calendar: Calendar = GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"))

            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minutes = calendar.get(Calendar.MINUTE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDateTimeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            hourPicker.minValue = 0
            hourPicker.maxValue = 23
            hourPicker.value = hour
            minutePicker.minValue = 0
            minutePicker.maxValue = 59
            minutePicker.value = minutes
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getHour() = hour
    fun getMinute() = minutes
}