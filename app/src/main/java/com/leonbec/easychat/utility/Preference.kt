package com.leonbec.easychat.utility

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

/**
 * Created by leonbec on 2018/1/2.
 */
const val PREFERENCE_FILENAME = "preference"
const val IS_LOGGED_IN = "isLoggedIn"
const val AUTH_TOKEN = "authToken"
const val USER_EMAIL = "userEmail"

class Preference(context: Context) {
    private val preference: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILENAME, 0)

    var isLoggedIn
        get() = preference.getBoolean(IS_LOGGED_IN, false)
        set(value) = preference.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken
        get() = preference.getString(AUTH_TOKEN, "")
        set(value) = preference.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail
        get() = preference.getString(USER_EMAIL, "")
        set(value) = preference.edit().putString(USER_EMAIL, value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}