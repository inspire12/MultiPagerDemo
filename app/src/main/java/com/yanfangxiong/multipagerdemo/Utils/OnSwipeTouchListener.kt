package com.yanfangxiong.multipagerdemo.Utils

import android.content.Context
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.view.*
import com.yanfangxiong.multipagerdemo.Utils.OnSwipeTouchListener.Companion.SWIPE_THRESHOLD
import com.yanfangxiong.multipagerdemo.Utils.OnSwipeTouchListener.Companion.SWIPE_VELOCITY_THRESHOLD
/**
 * 출처 : https://gist.github.com/nesquena/ed58f34791da00da9751
 */
open class OnSwipeTouchListener(c: Context) : OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }

    override  fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeDown()
                        } else {
                            onSwipeUp()
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }


    }
    companion object {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onSwipeUp() {}

    open fun onSwipeDown() {}
}