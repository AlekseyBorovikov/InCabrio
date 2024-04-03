package com.digitalsln.stanserhorn.ui.trip_log

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalsln.stanserhorn.base.BaseFragment
import com.digitalsln.stanserhorn.databinding.FragmentTripLogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripLogFragment: BaseFragment<FragmentTripLogBinding, TripLogState, TripLogViewModel>() {

    companion object {
        const val OPEN_DIALOG_KEY = "key.open-dialog"
    }

    private val dialog by lazy { TripLogDialog() }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            binding.recycle.scrollToPosition(positionStart)
        }
    }

    private val adapter by lazy {
        TripLogAdapter { dialog.showUpdateDialog(activity?.supportFragmentManager ?: return@TripLogAdapter, it) }.apply {
            registerAdapterDataObserver(observer)
        }
    }

    override fun initViewModel() = viewModels<TripLogViewModel>().value

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTripLogBinding.inflate(inflater, container, false)

    override fun initViews() {
        binding.recycle.layoutManager = LinearLayoutManager(context)
        binding.recycle.adapter = adapter

        binding.addTripButton.setOnClickListener {
            dialog.showAddDialog(activity?.supportFragmentManager ?: return@setOnClickListener)
        }

        if (arguments?.getBoolean(OPEN_DIALOG_KEY) == true) dialog.showAddDialog(activity?.supportFragmentManager ?: return)
    }

    override fun handleState(state: TripLogState) {
        binding.cabineLabel.text = "Kabine ${state.cabineNumber}"
        binding.ascentCount.text = state.ascentNumber.toString()
        binding.descentCount.text = state.descentNumber.toString()

        if (state.tripLogList.isEmpty()) {
            binding.recycle.isVisible = false
            binding.emptyListLabel.isVisible = true
        } else {
            binding.emptyListLabel.isVisible = false
            binding.recycle.isVisible = true
            adapter.submitList(state.tripLogList)
        }
    }
}