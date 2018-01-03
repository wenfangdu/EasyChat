package com.leonbec.easychat.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.leonbec.easychat.R
import com.leonbec.easychat.model.Channel
import com.leonbec.easychat.model.Message
import com.leonbec.easychat.service.AuthService
import com.leonbec.easychat.service.MessageService
import com.leonbec.easychat.service.UserDataService
import com.leonbec.easychat.util.BROADCAST_USER_DATA_CHANGE
import com.leonbec.easychat.util.SOCKET_URL
import com.leonbec.easychat.util.hideKeyboard
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private fun setupAdapter() {
        channelAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                MessageService.channels)
        channelLV.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(userDataChangeReceiver,
                        IntentFilter(BROADCAST_USER_DATA_CHANGE))
        socket.connect()
        socket.on("channelCreated", channelCreatedListener)
        socket.on("messageCreated", messageCreatedListener)
        setupAdapter()

        channelLV.setOnItemClickListener { adapterView, view, i, l ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.preference.isLoggedIn)
            AuthService.findUserByEmail(this) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.preference.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "Logout"

                MessageService.getChannels { success ->
                    if (success) {
                        if (MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        } else mainChannelName.text = "No channel selected"
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        //download msgs for channel
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavHeaderClick(view: View) {
        if (App.preference.isLoggedIn) {
            UserDataService.logout()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun addChannelBtnClick(view: View) {
        if (App.preference.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                    .setPositiveButton("Add") { dialogInterface, i ->
                        val channelName = dialogView.addChannelNameET.text.toString()
                        val channelDesc = dialogView.addChannelDescET.text.toString()

                        socket.emit("newChannel", channelName, channelDesc)
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }
                    .show()
        }
    }

    private val channelCreatedListener = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDesc = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDesc, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    private val messageCreatedListener = Emitter.Listener { args ->
        runOnUiThread {
            val msgBody = args[0] as String
            val channelId = args[2] as String
            val userName = args[3] as String
            val userAvatar = args[4] as String
            val userAvatarColor = args[5] as String
            val id = args[6] as String
            val timeStamp = args[7] as String

            val newMsg = Message(msgBody, channelId, userName, userAvatar, userAvatarColor, id, timeStamp)
            MessageService.messages.add(newMsg)
        }
    }

    fun sendMsgBtnClick(view: View) {
        if (App.preference.isLoggedIn && msgMT.text.isNotEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage", msgMT.text.toString(),
                    userId, channelId, UserDataService.name, UserDataService.avatarName,
                    UserDataService.avatarColor)
            msgMT.text.clear()
            view.hideKeyboard()
        }
    }
}
