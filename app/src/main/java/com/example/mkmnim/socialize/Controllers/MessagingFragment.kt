package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.mkmnim.socialize.Adapters.MessageAdapter
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.API.PageCreator
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.fragment_messaging.view.*

/**
 * Created by nimish on 20/3/18.
 */
class MessagingFragment:android.support.v4.app.Fragment()
{
    var myView:View?=null
    var messages= mutableListOf<Message>()
    lateinit var myMessageAdapter:MessageAdapter
    var SendButtonOnClickListener=object:View.OnClickListener
    {
        override fun onClick(v: View?)
        {
            messages.add(Message(myView!!.messageEditText.text.toString(),"You"))
            myView!!.messageEditText.text.clear()
            myMessageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        myView=inflater?.inflate(R.layout.fragment_messaging,container,false)
        messages.add(Message("hello bhai","You"))
        messages.add(Message("ok bhai","Nimi"))
        messages.add(Message("bhia bhai hello bhai","You"))
        messages.add(Message("zinda bhai hello bhai","You"))
        messages.add(Message("awwlele hello bhai","You"))
        messages.add(Message("kaisa hai hello bhai","Palku"))
        myMessageAdapter=MessageAdapter(context,messages)
        myView!!.messagingListView.adapter=myMessageAdapter
        myView!!.sendButton.setOnClickListener(SendButtonOnClickListener)
        return myView!!

    }


    fun replaceFragment(someFragment: android.support.v4.app.Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, someFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}