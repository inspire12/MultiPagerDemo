package com.yanfangxiong.multipagerdemo.Utils

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import com.yanfangxiong.multipagerdemo.BuildConfig

object CustomLog{
    val TAG = "Log"
    fun d(msg:String){
        if(BuildConfig.DEBUG){
            Log.d(TAG, msg)
        }
    }
    fun cd(tag: String, msg:String){
        if(BuildConfig.DEBUG){
            Log.d(tag, msg)
        }
    }
}