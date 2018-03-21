package com.example.mkmnim.socialize.Controllers

import android.app.Fragment
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.API.PageCreator
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * Created by nimish on 20/3/18.
 */
class ChatFragment:android.support.v4.app.Fragment()
{
    var myView:View?=null
    lateinit var connectedDevices:ArrayList<String>
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        myView=inflater?.inflate(R.layout.fragment_chat,container,false)
        connectedDevices=WifiService.getConnectedDevices(context)
        myView!!.chatListView.adapter=ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,connectedDevices)
        return myView!!
    }

}