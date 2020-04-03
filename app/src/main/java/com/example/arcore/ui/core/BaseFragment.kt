package com.example.arcore.ui.core

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.arcore.ui.main.MainNavigationActivity

abstract class BaseFragment constructor(@LayoutRes id: Int) : Fragment(id) {

    protected lateinit var navigation: MainNavigationActivity

    protected abstract fun initView(savedInstanceState: Bundle?)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity !is MainNavigationActivity) {
            throw IllegalStateException("Activity must implement MainNavigation")
        }
        navigation = activity as MainNavigationActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView(savedInstanceState)
    }
}