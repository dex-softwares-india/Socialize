package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.mkmnim.socialize.Adapters.MessageAdapter
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.DATABASE_HANDLER
import com.example.mkmnim.socialize.Utilities.MESSAGING_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_messaging.view.*
import org.json.JSONObject
import java.io.PrintWriter
import java.util.*


class MessagingFragment:android.support.v4.app.Fragment()
{
    var myView:View?=null
    var messages= mutableListOf<Message>()
    lateinit var myMessageAdapter:MessageAdapter
    var receiverIP:String?=null

//    var outFromServer:PrintWriter?=null //this should be many corresponding to each port

    private var clientSendButtonOnClickListener=object: View.OnClickListener
    {

        override fun onClick(v: View?)
        {
            Log.i("mytag","Client Send btn")
            var myMessage=Message(myView!!.messageEditText.text.toString().trim(), receiverIP.toString(), WifiService.getIpAddress192type(context).toString())
            messages.add(myMessage)
            myView?.messagingListView?.setSelection(myMessageAdapter.count-1)
            DATABASE_HANDLER?.addMessage(myMessage)
            val messageText=myView!!.messageEditText.text.toString()
            Thread(Runnable {
                val jsonObject=JSONObject()
                jsonObject.put("messageContent",messageText)
                jsonObject.put("from",WifiService.getIpAddress192type(context).toString())
                jsonObject.put("to",receiverIP.toString())
                outFromClient?.println(jsonObject.toString())
                outFromClient?.flush()
            }).start()
            myView!!.messageEditText.text.clear()
           myMessageAdapter.notifyDataSetChanged()
//            hideKeyboardFromMessageInputScreen()
        }
    }
    private var hostSendButtonOnClickListener=object: View.OnClickListener
    {

        override fun onClick(v: View?)
        {
            Log.i("mytag","Host Send btn")
            var myMEssage=Message(myView!!.messageEditText.text.toString().trim(), receiverIP.toString(), WifiService.getIpAddress192type(context).toString())
            messages.add(myMEssage)
            myView?.messagingListView?.setSelection(myMessageAdapter.count-1)
            DATABASE_HANDLER?.addMessage(myMEssage)
            val messageText=myView!!.messageEditText.text.toString()
            Thread(Runnable {
                val jsonObject=JSONObject()
                jsonObject.put("messageContent",messageText)
                jsonObject.put("from",WifiService.getIpAddress192type(context).toString())
                jsonObject.put("to",receiverIP.toString())
                var outFromServer:PrintWriter?=null
                outFromServer=outFromServerHashMap.get(receiverIP)
                outFromServer?.println(jsonObject.toString())
                outFromServer?.flush()

            }).start()
            myView!!.messageEditText.text.clear()
            myMessageAdapter.notifyDataSetChanged()
//            hideKeyboardFromMessageInputScreen()
        }
    }


    var myMessages:List<JSONObject>?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        myView = inflater?.inflate(R.layout.fragment_messaging, container, false)
        setAdaptersAndOnClickListeners()

        Log.i("mytag", this.arguments["position"].toString())
        Log.i("mytag", this.arguments["devices"].toString())
        receiverIP = (this.arguments["devices"] as List<String>).get(this.arguments["position"] as Int) as String
        Log.i("mytag", receiverIP.toString())

        loadMessages()


        if (WifiService.isWifiOn(context))
        {
            if (MESSAGING_FRAGMENT_INITIALIZED_ONCE==false)
            {
                MESSAGING_FRAGMENT_INITIALIZED_ONCE=true
            }

        }

        return myView!!

    }


    fun setAdaptersAndOnClickListeners()
    {
        myMessageAdapter = MessageAdapter(context, messages)
        myView!!.messagingListView.adapter = myMessageAdapter
        if (WifiService.isWifiOn(context))
        {
            myView!!.sendButton.setOnClickListener(clientSendButtonOnClickListener)
        }
        else if (WifiService.isHotspotOn(context))
        {
            myView!!.sendButton.setOnClickListener(hostSendButtonOnClickListener)
        }

    }



    fun loadMessages()
    {
        if (DATABASE_HANDLER?.allMessages?.size != 0)
        {
            for (i in DATABASE_HANDLER?.allMessages!!)
            {
                var myIp: String? = WifiService.getIpAddress192type(context)
                if ((i.receiver == receiverIP && i.from == myIp.toString()) || (i.receiver == myIp && i.from == receiverIP))
                {
                    Log.i("mytag", "i is $i and receeiver is $receiverIP")
                    messages.add(i)
                }
            }
        }
    }
    fun replaceFragment(someFragment: android.support.v4.app.Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, someFragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun hideKeyboardFromMessageInputScreen()
    {

        var im=activity.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(
                myView!!.messageEditText.getWindowToken(), 0);
    }

    fun isFragmentUIActive(): Boolean
    {
        return isAdded && !isDetached && !isRemoving
    }
    companion object
    {
        var outFromServerHashMap=HashMap<String,PrintWriter>()
        var outFromClient:PrintWriter?=null
    }
}