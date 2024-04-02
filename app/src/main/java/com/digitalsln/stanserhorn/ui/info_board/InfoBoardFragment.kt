package com.digitalsln.stanserhorn.ui.info_board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalsln.stanserhorn.base.BaseFragment
import com.digitalsln.stanserhorn.databinding.FragmentInfoBoardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoBoardFragment: BaseFragment<FragmentInfoBoardBinding, InfoBoardState, InfoBoardViewModel>() {

    private val adapter by lazy { InfoBoardAdapter() }

    override fun initViewModel() = viewModels<InfoBoardViewModel>().value

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentInfoBoardBinding.inflate(inflater, container, false)

    override fun initViews() {
        binding.recycle.layoutManager = LinearLayoutManager(context)
        binding.recycle.adapter = adapter
    }

    override fun handleState(state: InfoBoardState) {
        if (state.infoBoardList.isEmpty()) {
            binding.recycle.isVisible = false
            binding.emptyListLabel.isVisible = true
        } else {
            binding.emptyListLabel.isVisible = false
            binding.recycle.isVisible = true
            adapter.submitList(state.infoBoardList)
        }
    }

}