package com.digitalsln.stanserhorn.ui.daily_menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalsln.stanserhorn.base.BaseFragment
import com.digitalsln.stanserhorn.databinding.FragmentDailyMenuBinding
import com.digitalsln.stanserhorn.ui.info_board.InfoBoardAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyMenuFragment: BaseFragment<FragmentDailyMenuBinding, DailyMenuState, DailyMenuViewModel>() {

    private val adapter by lazy { DailyMenuAdapter() }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDailyMenuBinding.inflate(inflater, container, false)

    override fun initViews() {
        binding.recycle.layoutManager = LinearLayoutManager(context)
        binding.recycle.adapter = adapter
    }

    override fun handleState(state: DailyMenuState) {
        if (state.dailyMenuList.isEmpty()) {
            binding.recycle.isVisible = false
            binding.emptyListLabel.isVisible = true
        } else {
            binding.emptyListLabel.isVisible = false
            binding.recycle.isVisible = true
            adapter.submitList(state.dailyMenuList)
        }
    }

    override fun initViewModel() = viewModels<DailyMenuViewModel>().value

}