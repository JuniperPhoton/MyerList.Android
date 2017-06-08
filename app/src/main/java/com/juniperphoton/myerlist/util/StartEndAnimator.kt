package com.juniperphoton.myerlist.util


import android.animation.Animator

abstract class StartEndAnimator : Animator.AnimatorListener {
    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }
}