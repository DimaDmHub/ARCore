package com.example.arcore.util.extension

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView

fun LottieAnimationView.addAnimationEndListener(onEnd: () -> Unit) {
    this.addAnimatorListener(object : Animator.AnimatorListener {

        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            onEnd.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {}
    })
}