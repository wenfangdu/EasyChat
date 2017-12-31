package com.leonbec.easychat.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.leonbec.easychat.utility.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.invoke.MethodHandle

/**
 * Created by leonbec on 2017/12/30.
 */
object AuthService {
    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun register(
            context: Context,
            email: String,
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
                    Log.d("ERROR", "Could not register user: $error")
                    complete(false)
                }
        ) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun login(context: Context,
              email: String,
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
                        userEmail = response.getString("user")
                        authToken = response.getString("token")
                        isLoggedIn = true
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("JSON", "EXCEPTION: ${e.message}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not login user: $error")
                    complete(false)
                }
        ) {
            override fun getBodyContentType(): String {
                return "application/json; charset-utf=8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun addUser(context: Context,
                name: String,
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
                    Log.d("ERROR", "Could not add user $error")
                    complete(false)
                }
        ) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authToken")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(addUserRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest = object : JsonObjectRequest(
                Method.GET,
                "$URL_GET_USER$userEmail",
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
                    Log.d("ERROR", "Could not find user: $error")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $authToken")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(findUserRequest)
    }
}
