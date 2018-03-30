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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket


class MessagingFragment:android.support.v4.app.Fragment()
{
    var myView:View?=null
    var messages= mutableListOf<Message>()
    lateinit var myMessageAdapter:MessageAdapter
    var receiverIP:String?=null
    var outFromClient:PrintWriter?=null
    var outFromServer:PrintWriter?=null
    private var clientSendButtonOnClickListener=object: View.OnClickListener
    {

        override fun onClick(v: View?)
        {
            Log.i("mytag","Client Send btn")
            messages.add(Message(myView!!.messageEditText.text.toString(),"You"))
            val messageText=myView!!.messageEditText.text.toString()
            Thread(Runnable {
                val jsonObject=JSONObject()
                jsonObject.put("messageContent",messageText)
                jsonObject.put("from",WifiService.getIpAddress192type().toString())
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
            messages.add(Message(myView!!.messageEditText.text.toString(),"You"))
            val messageText=myView!!.messageEditText.text.toString()
            Thread(Runnable {
                val jsonObject=JSONObject()
                jsonObject.put("messageContent",messageText)
                jsonObject.put("from",WifiService.getIpAddress192type().toString())
                jsonObject.put("to",receiverIP.toString())
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
        myView = inflater?.inflate(R.layout.fragment_messaging, container, false)

        messages.add(Message("awwlele hello bhai", "You"))
        messages.add(Message("kaisa hai hello bhai", "Palku"))


        setAdaptersAndOnClickListeners()

        Log.i("mytag", this.arguments["position"].toString())
        Log.i("mytag", this.arguments["devices"].toString())
        receiverIP = (this.arguments["devices"] as List<String>).get(this.arguments["position"] as Int) as String
        Log.i("mytag", receiverIP.toString())

        if (WifiService.isHotspotOn(context))
        {
            ConnectToClientSocket(5001)

        }



        if (WifiService.isWifiOn(context))
        {
            var temporaryPort=5005
            CreateServerHostWithDifferentPorts(temporaryPort)
        }


        if (WifiService.isWifiOn(context))
        {
            ConnectToServerSocket(5001)

        }




        if (WifiService.isHotspotOn(context))
        {
            var temporaryPort2=5005
            ConnectToServerSocketHostedByEachClient("192.168.43.195",temporaryPort2)
        }


        return myView!!

    }


    fun ConnectToClientSocket(port:Int) //5001 server mobile accepting (that is whose hotspot is on)
    {
        Log.i("mytag","ConnectToClientSocket")
        var serverSocket= ServerSocket(port)

        Thread(Runnable {
            while (true)
            {
                var newSocket=serverSocket.accept()
                var reader= BufferedReader(InputStreamReader(newSocket.getInputStream()))
                readContent(reader,newSocket)
            }
        }).start()
    }


    fun ConnectToServerSocket(port:Int) //5001  //receiver mobile (that is whose wifi is on)
    {
        Log.i("mytag","ConnectToServerSocket")

            Thread(Runnable {
                try
                {
                    val socket = Socket("192.168.43.1", port)  //use 1 instead of 76 -  -  - host ip for testing using 192.168.43.76
//                    val socket = Socket("10.132.240.103", port)
                    outFromClient = PrintWriter(socket.getOutputStream())

                }
                catch (ex:Exception)
                {
                    Log.i("mytag",ex.message.toString()+"in ConnectToServerSocket")
                }

            }).start()


        }



    fun CreateServerHostWithDifferentPorts(port:Int)  //client (whose wifi is on) port from homepage
    {
        Log.i("mytag","CreateServerHostWithDifferentPorts")
        var serverSocket= ServerSocket(port)

        Thread(Runnable {
            while (true)
            {
                var newSocket=serverSocket.accept()
                var reader= BufferedReader(InputStreamReader(newSocket.getInputStream()))
                readContent(reader,newSocket)
            }
        }).start()
    }


    fun ConnectToServerSocketHostedByEachClient(host:String,port:Int)  //receiver mobile (that is whose hotspot is on)
    {

        Log.i("mytag","CoonecttoServerSocketHostedByeachClient")
        Thread(Runnable {
            try
            {
                val socket = Socket(host, port)  //use 1 instead of 76 -  -  - host ip for testing using 192.168.43.76
                outFromServer = PrintWriter(socket.getOutputStream())

            }
            catch (ex:Exception)
            {
                Log.i("mytag",ex.message.toString()+"in ConnectToServerSocketHostedByEachClient")
            }

        }).start()


    }






    fun readContent(reader:BufferedReader,newSocket: Socket)
    {

        Thread(Runnable {
            while (true)
            {
                var messageContent: String? = reader.readLine()
                if (messageContent == null)
                {
                    break
                }
                println(messageContent)
                activity.runOnUiThread{
                    messages.add(Message(messageContent,"You"))
                    myMessageAdapter.notifyDataSetChanged()
                }
            }
        }).start()

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