package com.example.mkmnim.socialize.Adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.message_receiver.view.*
import kotlinx.android.synthetic.main.message_sender.view.*

/**
 * Created by nimish on 22/3/18.
 */
class MessageAdapter(context:Context,messageList:List<Message>):BaseAdapter()
{
    var context = context
    var messageList = messageList

    override fun getCount(): Int
    {
        return messageList.size
    }

    override fun getItem(position: Int): Any
    {
        return messageList[position]
    }

    override fun getItemId(position: Int): Long
    {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var view: View
        if (messageList[position].receiver == WifiService.getIpAddress192type()?.toString())
        {
            view = LayoutInflater.from(context).inflate(R.layout.message_sender, null)
            view.UsernameSender.text = messageList[position].receiver
            view.messageTextSender.text = messageList[position].message
            view.messageTextSender.gravity=Gravity.RIGHT
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.message_receiver, null)
            view.UsernameReceiver.text = messageList[position].receiver
            view.messageTextReceiver.text = messageList[position].message
        }
        return view
    }
}