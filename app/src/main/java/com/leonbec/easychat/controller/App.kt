package com.leonbec.easychat.controller

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.leonbec.easychat.util.ActivityLifecycleAdapter
import com.leonbec.easychat.util.Preference

/**
 * Created by leonbec on 2018/1/2.
 */
class App : Application() {
    companion object {
        lateinit var preference: Preference
    }

    override fun onCreate() {
        super.onCreate()
        preference = Preference(applicationContext)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleAdapter() {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        })
    }
}