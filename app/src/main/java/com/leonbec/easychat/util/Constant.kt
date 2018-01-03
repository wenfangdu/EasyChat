package com.leonbec.easychat.util

/**
 * Created by leonbec on 2017/12/30.
 */
const val BASE_URL = "https://leon-easy-chat.herokuapp.com/v1"
const val SOCKET_URL = "https://leon-easy-chat.herokuapp.com"
const val URL_REGISTER = "$BASE_URL/account/register"
const val URL_LOGIN = "$BASE_URL/account/login"
const val URL_ADD_USER = "$BASE_URL/user/add"
const val URL_GET_USER = "$BASE_URL/user/byEmail/"
const val URL_GET_CHANNEL = "$BASE_URL/channel"

//Broadcast constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"