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
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.API.PageCreator
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * Created by nimish on 20/3/18.
 */
class ChatFragment:android.support.v4.app.Fragment()
{
    var myView:View?=null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        myView=inflater?.inflate(R.layout.fragment_chat,container,false)
        return myView!!

    }

}