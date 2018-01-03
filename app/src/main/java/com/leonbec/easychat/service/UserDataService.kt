package com.leonbec.easychat.service

import android.graphics.Color
import com.leonbec.easychat.controller.App
import java.util.*

/**
 * Created by leonbec on 2017/12/30.
 */
object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        MessageService.channels.clear()
        App.preference.authToken = ""
        App.preference.userEmail = ""
        App.preference.isLoggedIn = false
    }

    fun returnAvatarColor(components: String): Int {
        val strippedColor = components
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r, g, b)
    }
}