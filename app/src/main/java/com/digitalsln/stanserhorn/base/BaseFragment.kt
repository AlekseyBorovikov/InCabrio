package com.digitalsln.stanserhorn.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.digitalsln.stanserhorn.ui.daily_menu.DailyMenuViewModel
import kotlinx.coroutines.launch

abstract class BaseFragment<B: ViewBinding, S: ViewState, M: BaseViewModel<S>>: Fragment() {

    private var _binding: B? = null
    val binding: B get() = _binding!!

    private val viewModel by lazy { initViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.uiState.collect { state -> handleState(state) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun initViewModel(): M

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun handleState(state: S)

    abstract fun initViews()
}