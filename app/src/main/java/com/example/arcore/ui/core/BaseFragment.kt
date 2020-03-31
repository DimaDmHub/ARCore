package com.example.arcore.ui.core

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment

abstract class BaseFragment constructor(@LayoutRes id: Int) : Fragment(id) {

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    abstract fun initView(savedInstanceState: Bundle?)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView(savedInstanceState)
    }
}