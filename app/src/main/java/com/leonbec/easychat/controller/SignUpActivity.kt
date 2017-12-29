package com.leonbec.easychat.controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.leonbec.easychat.R
import com.leonbec.easychat.service.AuthService
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private var avatar = "profiledefault"
    private var bgColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val num = random.nextInt(28)

        avatar = if (color == 0) "light$num" else "dark$num"
        val resId = resources.getIdentifier(avatar, "drawable", packageName)
        signUpAvatarIV.setImageResource(resId)
    }

    fun generateBgColor(view: View) {
        val random = Random()
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        signUpAvatarIV.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        bgColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUser(view: View) {
        AuthService.registerUser(this, "ff@t.com", "1234422") {}
    }
}
