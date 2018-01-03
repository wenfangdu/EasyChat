package com.leonbec.easychat.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.leonbec.easychat.controller.App
import com.leonbec.easychat.utility.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.invoke.MethodHandle

/**
 * Created by leonbec on 2017/12/30.
 */
object AuthService {

    fun register(email: String,
                 password: String,
                 complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(
                Method.POST,
                URL_REGISTER,
                Response.Listener { response ->
                    println(response)
                    complete(true)
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not register user")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.preference.requestQueue.add(registerRequest)
    }

    fun login(email: String,
              password: String,
              complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(
                Method.POST,
                URL_LOGIN,
                null,
                Response.Listener { response ->
                    //                    println(response)
                    try {
                        App.preference.userEmail = response.getString("user")
                        App.preference.authToken = response.getString("token")
                        App.preference.isLoggedIn = true
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", "EXCEPTION: ${e.message}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not login user")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset-utf=8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.preference.requestQueue.add(loginRequest)
    }

    fun addUser(name: String,
                email: String,
                avatarName: String,
                avatarColor: String,
                complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val addUserRequest = object : JsonObjectRequest(
                Method.POST,
                URL_ADD_USER,
                null,
                Response.Listener { response ->
                    try {
                        UserDataService.id = response.getString("_id")
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", "EXCEPTION ${e.message}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not add user")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.preference.authToken}")
                return headers
            }
        }

        App.preference.requestQueue.add(addUserRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest = object : JsonObjectRequest(
                Method.GET,
                "$URL_GET_USER${App.preference.userEmail}",
                null,
                Response.Listener { response ->
                    try {
                        UserDataService.id = response.getString("_id")
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")

                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                        LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(userDataChange)
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", e.message)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not find user")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.preference.authToken}")
                return headers
            }
        }
        App.preference.requestQueue.add(findUserRequest)
    }
}
