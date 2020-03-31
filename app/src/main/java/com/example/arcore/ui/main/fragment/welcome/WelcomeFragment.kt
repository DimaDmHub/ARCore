package com.example.arcore.ui.main.fragment.welcome

import android.os.Bundle
import android.widget.Toast
import com.example.arcore.R
import com.example.arcore.ui.core.BaseFragment
import com.example.arcore.util.extension.addAnimationEndListener
import kotlinx.android.synthetic.main.welcome_fragment.*

class WelcomeFragment : BaseFragment(R.layout.welcome_fragment) {

    override fun initView(savedInstanceState: Bundle?) {
        initAnimationView()
        initContinueButton()
    }

    private fun initAnimationView() {
        lavWelcomeFragmentAnimationView?.setMinAndMaxProgress(0.0f, MAX_ANIMATION_VIEW_PROGRESS)
        lavWelcomeFragmentAnimationView?.addAnimatorUpdateListener {
            with(it.animatedValue as Float) {
                mbWelcomeFragmentContinue.alpha = (1f / MAX_ANIMATION_VIEW_PROGRESS) * this
            }
        }
        lavWelcomeFragmentAnimationView?.addAnimationEndListener {
            mbWelcomeFragmentContinue.isEnabled = true
        }
    }

    private fun initContinueButton() {
        mbWelcomeFragmentContinue?.setOnClickListener {
            Toast.makeText(requireContext(), "Test", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        private const val MAX_ANIMATION_VIEW_PROGRESS = 0.8f
    }
}