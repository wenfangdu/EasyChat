package com.leonbec.easychat.controller

import android.app.Application
import com.leonbec.easychat.utility.Preference

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
    }
}