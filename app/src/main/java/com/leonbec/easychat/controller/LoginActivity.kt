package com.leonbec.easychat.controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.leonbec.easychat.R
import com.leonbec.easychat.service.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginBtnClick(view: View) {
        val email = loginEmailET.text.toString()
        val password = loginPasswordET.text.toString()

        AuthService.login(this, email, password) { success ->
            if (success) AuthService.findUserByEmail(this) { success ->
                if (success) finish()
            }
        }
    }

    fun loginSignUpBtnClick(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}
