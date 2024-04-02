package com.digitalsln.stanserhorn.ui.reservation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalsln.stanserhorn.base.BaseFragment
import com.digitalsln.stanserhorn.databinding.FragmentReservationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservationFragment: BaseFragment<FragmentReservationsBinding, ReservationState, ReservationViewModel>() {

    private val adapter by lazy { ReservationAdapter() }

    override fun initViewModel() = viewModels<ReservationViewModel>().value

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentReservationsBinding.inflate(inflater, container, false)

    override fun initViews() {
        binding.recycle.layoutManager = LinearLayoutManager(context)
        binding.recycle.adapter = adapter
    }

    override fun handleState(state: ReservationState) {
        if (state.reservationList.isEmpty()) {
            binding.recycle.isVisible = false
            binding.emptyListLabel.isVisible = true
        } else {
            binding.emptyListLabel.isVisible = false
            binding.recycle.isVisible = true
            adapter.submitList(state.reservationList)
        }
    }

}