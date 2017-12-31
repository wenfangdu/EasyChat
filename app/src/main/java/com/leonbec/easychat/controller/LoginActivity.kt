package com.leonbec.easychat.controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.leonbec.easychat.R
import com.leonbec.easychat.service.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginBtnClick(view: View) {
        enableSpinner(true)
        val email = loginEmailET.text.toString()
        val password = loginPasswordET.text.toString()

        if (email.isNotBlank() && password.isNotBlank())
            AuthService.login(this, email, password) { success ->
                if (success) AuthService.findUserByEmail(this) { success ->
                    if (success) {
                        enableSpinner(false)
                        finish()
                    } else errorToast()
                } else errorToast()
            }
        else Toast.makeText(
                this,
                "Please fill in both email and password",
                Toast.LENGTH_LONG
        ).show()
    }

    fun loginSignUpBtnClick(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun enableSpinner(enable: Boolean) {
        loginSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE

        loginLoginBtn.isEnabled = !enable
        loginSignUpBtn.isEnabled = !enable
    }

    private fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again.",
                Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }
}
