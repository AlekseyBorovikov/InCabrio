package com.digitalsln.stanserhorn.ui.trip_log.add_trip_log_dialog

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry
import com.digitalsln.stanserhorn.databinding.DialogAddTripLogBinding
import com.digitalsln.stanserhorn.repositoies.TripLogRepository
import com.digitalsln.stanserhorn.tools.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TripLogDialog: DialogFragment() {

    @Inject
    lateinit var tripLogRepository: TripLogRepository

    private var _binding: DialogAddTripLogBinding? = null
    private val binding get() = _binding!!
    private var isDialogShown = false

    private var tripDialogType: TripDialogType = TripDialogType.AddTripType

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddTripLogBinding.inflate(layoutInflater, null, false)
        val calendar = GregorianCalendar.getInstance(Locale.GERMANY)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)

        isCancelable = false
        val dialog = AlertDialog.Builder(requireActivity())
            .apply {
                when(tripDialogType) {
                    is TripDialogType.UpdateTripType -> {
                        val tripLogEntry = (tripDialogType as TripDialogType.UpdateTripType).tripLogEntry
                        setTitle(context?.getString(R.string.triplog_add_trip_dialog_title, tripLogEntry.date))
                        setEditData(tripLogEntry)
                    }
                    TripDialogType.AddTripType -> {
                        setTitle(context?.getString(R.string.triplog_add_trip_dialog_title, dateFormat.format(calendar.time)))
                        setNewData()
                    }
                }
            }
            .setPositiveButton(R.string.triplog_add_trip_dialog_ok) { dialog, _ ->

                val deviceUID = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
                val tripOfDay = binding.tripDialogTripOfDay.text.toString().toIntOrNull() ?: 1
                val ascent = binding.tripDialogAscentSpinner.selectedItemPosition
                val passengersAsString = binding.tripDialogPassengers.text.toString()
                val passengers = if (passengersAsString.isNotEmpty()) passengersAsString.toInt() else 0
                val remarks = binding.tripDialogRemarks.text.toString()

                val dateCalendar = GregorianCalendar(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    binding.hourPicker.value,
                    binding.minutePicker.value,
                )

                val timeFormat = SimpleDateFormat("HH:mm:00", Locale.GERMANY)
                val date = dateFormat.format(dateCalendar.time)
                val time = timeFormat.format(dateCalendar.time)

                createOrUpdateTripLogEntry(deviceUID, tripOfDay, date, time, passengers, ascent == 1, remarks)
            }
            .setNegativeButton(R.string.triplog_add_trip_dialog_cancel) { dialog, _ -> dismiss() }
            .setView(binding.root)
            .create()

        return dialog
    }

    override fun onResume() {
        super.onResume()


        val displayMetrics = context?.resources?.displayMetrics ?: return
        val displayWidth = displayMetrics.widthPixels
        val width = (displayWidth * 0.9).toInt()

        dialog?.window?.setLayout(width, LayoutParams.WRAP_CONTENT)
    }

    fun showAddDialog(fragmentManager: FragmentManager) {
        if (isDialogShown) return
        tripDialogType = TripDialogType.AddTripType
        show(fragmentManager, "addTripDialog")
    }

    fun showUpdateDialog(fragmentManager: FragmentManager, tripLogEntry: TripLogEntry) {
        if (isDialogShown) return
        tripDialogType = TripDialogType.UpdateTripType(tripLogEntry)
        show(fragmentManager, "updateTripDialog")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        isDialogShown = true
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        super.show(manager, tag)
    }

    override fun onDismiss(dialog: DialogInterface) {
        isDialogShown = false
        super.onDismiss(dialog)
    }

    override fun onStart() {
        super.onStart()

        if (tripDialogType == TripDialogType.AddTripType) lifecycleScope.launch {
            val tripOfDay = tripLogRepository.getTripOfDayFromDb() + 1
            binding.tripDialogTripOfDay.setText(tripOfDay.toString())

            val ascent = tripLogRepository.getLastDirection()
            binding.tripDialogAscentSpinner.setSelection(if (ascent) 0 else 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createOrUpdateTripLogEntry(
        deviceUID: String,
        tripOfDay: Int,
        date: String,
        time: String,
        passengers: Int,
        ascent: Boolean,
        remarks: String,
    ) {
        lifecycleScope.launch {
            if (tripDialogType == TripDialogType.AddTripType) {
                Logger.d("In AddTripDialogFragment.onCreateDialog: Adding trip $deviceUID.")
                tripLogRepository.createTripLog(
                    deviceUID = deviceUID,
                    tripOfDay = tripOfDay,
                    date = date,
                    time = time,
                    passengers = passengers,
                    ascent = ascent,
                    remarks = remarks,
                )
            } else {
                Logger.d("In AddTripDialogFragment.onCreateDialog: Updating trip $deviceUID.")
                val oldTripLog = (tripDialogType as TripDialogType.UpdateTripType).tripLogEntry
                tripLogRepository.updateTripLog(
                    deviceUID = deviceUID,
                    tripOfDay = tripOfDay,
                    date = date,
                    time = time,
                    passengers = passengers,
                    ascent = ascent,
                    remarks = remarks,
                    internalId = oldTripLog.internalId,
                    globeId = oldTripLog.globeId,
                )
            }
        }
    }

    private fun setNewData() {
        val calendar = GregorianCalendar.getInstance(Locale.GERMANY)
        binding.hourPicker.apply {
            maxValue = 23
            minValue = 0
            value = calendar.get(Calendar.HOUR_OF_DAY)
        }
        binding.minutePicker.apply {
            maxValue = 59
            minValue = 0
            value = calendar.get(Calendar.MINUTE)
        }

        binding.tripDialogTripOfDay.setText("1")

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.triplog_add_trip_dialog_ascent_spinner_choices,
            R.layout.add_trip_spinner_layout
        )
        adapter.setDropDownViewResource(R.layout.add_trip_spinner_layout)
        binding.tripDialogAscentSpinner.adapter = adapter
        binding.tripDialogPassengers.requestFocus()
    }

    private fun setEditData(tripLog: TripLogEntry) {

        val dateTime = SimpleDateFormat("HH:mm", Locale.GERMANY).parse(tripLog.time)
        val calendar = Calendar.getInstance().apply { time = dateTime }

        binding.hourPicker.apply {
            maxValue = 23
            minValue = 0
            value = calendar.get(Calendar.HOUR_OF_DAY)
        }
        binding.minutePicker.apply {
            maxValue = 59
            minValue = 0
            value = calendar.get(Calendar.MINUTE)
        }

        binding.tripDialogTripOfDay.setText(tripLog.tripOfDay.toString())

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.triplog_add_trip_dialog_ascent_spinner_choices,
            R.layout.add_trip_spinner_layout
        )
        adapter.setDropDownViewResource(R.layout.add_trip_spinner_layout)

        binding.tripDialogAscentSpinner.adapter = adapter
        binding.tripDialogAscentSpinner.setSelection(if (tripLog.ascent) 1 else 0)

        binding.tripDialogPassengers.setText(tripLog.numberPassengers.toString())
        binding.tripDialogPassengers.requestFocus()

        binding.tripDialogRemarks.setText(tripLog.remarks)
    }

    private sealed class TripDialogType {
        data object AddTripType: TripDialogType()
        data class UpdateTripType(val tripLogEntry: TripLogEntry): TripDialogType()
    }

}

//class AddTripLogDialogFragment: DialogFragment() {
//
//    private var _binding: DialogAddTripLogBinding? = null
//    private val binding: DialogAddTripLogBinding get() = _binding!!
//
//    private val adapter by lazy {
//        AddTripLogDialogAdapter(this).apply {
//            addFragment(DateTimePickFragment())
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        isCancelable = false
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = DialogAddTripLogBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val calendar: Calendar = GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"))
//        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
//        val currentDate = dateFormat.format(calendar.time)
//
//        binding.run {
//            dateValue.text = currentDate
//
//            viewPager.adapter = adapter
//            actionButton.setOnClickListener {
//                if (binding.viewPager.currentItem != (adapter.itemCount - 1)) binding.viewPager.currentItem += 1
//                else dismiss()
//            }
//            backButton.setOnClickListener {
//                if (binding.viewPager.currentItem != 0) binding.viewPager.currentItem -= 1
//                else dismiss()
//            }
//            closeButton.setOnClickListener { dismiss() }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//}