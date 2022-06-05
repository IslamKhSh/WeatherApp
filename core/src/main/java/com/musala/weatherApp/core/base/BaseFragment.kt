package com.musala.weatherApp.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.musala.weatherApp.core.base.states.BaseViewState

/**
 * base fragment that work with viewModel and states
 *
 * @param DB the view binding of fragment layout
 * @param VS the viewState of the fragment which extends [BaseViewState]
 * @param VM the viewModel type that extends [BaseViewModel]
 */
abstract class BaseFragment<DB : ViewDataBinding, VS : BaseViewState, VM : BaseViewModel<VS, *>> :
    Fragment(), BaseView {

    protected lateinit var binding: DB

    /**
     * every fragment must return the class type of its viewModel
     */
    protected abstract fun getViewModelClass(): Class<VM>

    protected open val viewModel: VM by lazy {
        ViewModelProvider(if (isSharedViewModel()) requireActivity() else this)[getViewModelClass()]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDataBinding(inflater, container)
        initBindingLifeCycleOwner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewState()
        init()
    }

    private fun initDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            layoutRes,
            container,
            false
        )
    }

    override fun initBindingLifeCycleOwner() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun observeViewState() {
        viewModel.viewState.observe(viewLifecycleOwner, ::renderViewState)
    }

    /**
     * Use this to render state and draw the ui
     */
    abstract fun renderViewState(state: VS)

    /**
     * override it to return true if the fragment viewModel is shared with other fragments.
     * @return Boolean true if viewModel is shared, false otherwise
     */
    open fun isSharedViewModel() = false
}
