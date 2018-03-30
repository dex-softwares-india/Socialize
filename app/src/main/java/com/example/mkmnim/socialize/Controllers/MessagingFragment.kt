package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.mkmnim.socialize.Adapters.MessageAdapter
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_messaging.view.*
import java.net.Socket
import org.json.JSONObject
import java.io.PrintWriter


/**
 * Created by nimish on 20/3/18.
 */
class MessagingFragment:android.support.v4.app.Fragment()
{
    var myView:View?=null
    var messages= mutableListOf<Message>()
    lateinit var myMessageAdapter:MessageAdapter
    var receiverIP:String?=null
    var out:PrintWriter?=null
    var SendButtonOnClickListener=object:View.OnClickListener
    {
        override fun onClick(v: View?)
        {
            messages.add(Message(myView!!.messageEditText.text.toString(),"You"))
            var messageText=myView!!.messageEditText.text.toString()
            Thread(Runnable {
                var jsonObject=JSONObject()
                jsonObject.put("messageContent",messageText)
                jsonObject.put("from",WifiService.getIpAddress192type().toString())
                jsonObject.put("to",receiverIP.toString())
                out?.println(jsonObject.toString()+"\n")
                out?.flush()
            }).start()
            myView!!.messageEditText.text.clear()
           myMessageAdapter.notifyDataSetChanged()
//            hideKeyboardFromMessageInputScreen()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        myView=inflater?.inflate(R.layout.fragment_messaging,container,false)

        messages.add(Message("awwlele hello bhai","You"))
        messages.add(Message("kaisa hai hello bhai","Palku"))


        setAdaptersAndOnClickListeners()

        Log.i("mytag",this.arguments["position"].toString())
        Log.i("mytag",this.arguments["devices"].toString())
        receiverIP=(this.arguments["devices"] as List<String>).get(this.arguments["position"] as Int) as String
        Log.i("mytag",receiverIP.toString())
        ConnectToSocket(5001)


        return myView!!

    }


    fun ConnectToSocket(port:Int)
    {


            Thread(Runnable {
                try
                {
                    val socket = Socket("192.168.43.76", port)  //host ip for testing using 192.168.43.76
                    out = PrintWriter(socket.getOutputStream())

                }
                catch (ex:Exception)
                {
                    Log.i("mytag",ex.message.toString())
                }

            }).start()


        }








    fun setAdaptersAndOnClickListeners()
    {
        myMessageAdapter=MessageAdapter(context,messages)
        myView!!.messagingListView.adapter=myMessageAdapter
        myView!!.sendButton.setOnClickListener(SendButtonOnClickListener)

    }

    fun replaceFragment(someFragment: android.support.v4.app.Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, someFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun hideKeyboardFromMessageInputScreen()
    {

        var im=activity.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(
                myView!!.messageEditText.getWindowToken(), 0);
    }
}