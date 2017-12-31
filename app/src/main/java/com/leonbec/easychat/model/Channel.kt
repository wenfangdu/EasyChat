package com.leonbec.easychat.model

/**
 * Created by leonbec on 2018/1/1.
 */
class Channel(val name:String,val desc:String,val id:String) {
    override fun toString(): String {
        return "#$name"
    }
}