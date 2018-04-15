package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.mkmnim.socialize.Adapters.MessageAdapter
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.RequestClass.VolleyCallBack
import com.example.mkmnim.socialize.Utilities.DATABASE_HANDLER
import com.example.mkmnim.socialize.Utilities.MESSAGING_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.WifiService
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpRequest
import com.koushikdutta.async.http.AsyncHttpResponse
import kotlinx.android.synthetic.main.fragment_messaging.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
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


//        messages.add(Message("awwlele hello bhai", "You", WifiService.getIpAddress192type().toString()))
//        messages.add(Message("kaisa hai hello bhai", "Palku", WifiService.getIpAddress192type().toString()))



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
                /* may be uncommented  later*/
//                connectToServerSocket(5001)//outFromClient always 5001
                MESSAGING_FRAGMENT_INITIALIZED_ONCE=true
            }

        }

        return myView!!
        if (WifiService.isHotspotOn(context))
        {

            var appUsers:MutableList<String>
//            appUsers=getAppUsers()
//            Log.i("mytag","received app users are ${appUsers}")

//            for (i in listOf<String>("192.168.43.195","192.168.43.76")) //these are connected devices
//            for (i in appUsers)

            for (i in WifiService.getClientList(context))
            {
                Thread(Runnable {
                    try
                    {
                        var myPort = getPortForToIp(i)
                        if (myPort!="None")
                        {
                            ConnectToServerSocketHostedByEachClient(i, Integer.parseInt(myPort))//5004 of micromax
                        }
                    }
                    catch (ex: Exception)
                    {
                        Log.i("error", ex.message.toString() + "in ConnectToServerSocketHostedByEachClient for " + i.toString())
                    }
                }).start()
            }

        }


        return myView!!

    }



    fun ConnectToServerSocketHostedByEachClient(host:String,port:Int)  //receiver mobile (that is whose hotspot is on)
    {

        Log.i("mytag","CoonecttoServerSocketHostedByeachClient")
        Thread(Runnable {
            try
            {
                val socket = Socket(host, port)  //use 1 instead of 76 -  -  - host ip for testing using 192.168.43.76
                var outFromServer = PrintWriter(socket.getOutputStream())
                outFromServerHashMap[host]=outFromServer
            }
            catch (ex:Exception)
            {
                Log.i("mytag",ex.message.toString()+"in ConnectToServerSocketHostedByEachClient")
            }

        }).start()


    }


    fun connectToServerSocket(port:Int) //5001  //receiver mobile (that is whose wifi is on)
    {
        Log.i("mytag","connectToServerSocket")

            Thread(Runnable {
                try
                {
                    val socket = Socket("192.168.43.1", port)  //use 1 instead of 76 -  -  - host ip for testing using 192.168.43.76
//                    val socket = Socket("10.132.240.103", port)

                    outFromClient = PrintWriter(socket.getOutputStream())

                }
                catch (ex:Exception)
                {
                    Log.i("mytag",ex.message.toString()+"in connectToServerSocket")
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
                    messages.add(Message(messageContent, "You", WifiService.getIpAddress192type(context).toString()))
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


    public fun getPortForToIp(to:String):String    //returns None or port no
    {
        var requestString="http://"+to+":5000/portno"
        Log.i("mytag","request string is $requestString")
        var port:String=""  //possible values ["",None,port no]
        var volleyCallBack=object: VolleyCallBack
        {
            override fun onSuccess(result: String)
            {
                Log.i("mytag","result is $result from callback")
                if (result!="failed")
                {
                    port=JSONObject(result)["port"].toString()

                }
                else if (result=="failed")
                {
                    port="None"
                }
                Log.i("mytag","result is $result")

            }
        }
//        VolleyService.getPort(requestString,context,volleyCallBack)
//        {
//           Log.i("mytag","post find port in volleyService")
//
//        }

        AsyncHttpClient.getDefaultInstance().executeJSONObject(AsyncHttpRequest(Uri.parse(requestString),"GET"), object : AsyncHttpClient.JSONObjectCallback()
        {
            override fun onCompleted(e: java.lang.Exception?, source: AsyncHttpResponse?, result: JSONObject?)
            {
                if (e != null)
                {
                    Log.i("mytag",e.message.toString())
                    return
                }
                Log.i("mytag","I got a string: ${result.toString()}")
                port=JSONObject(result.toString())["port"].toString()
            }
        })

        var startTime=Calendar.getInstance().timeInMillis
        while(true)
        {

//            Log.i("mytag", "stuck in looping volley port is $port")
//            Log.i("MYTIME",(Calendar.getInstance().timeInMillis-startTime).toString())

            if ((Calendar.getInstance().timeInMillis-startTime)>1000)
            {
                Log.i("mytag","quitting after trying connecting for 1 second")
                port="None"
            }

            if (port != "")
            {
                Log.i("mytag","answerFromPortForIp is $port while looking $requestString")
                break
            }
        }
//        return 1234.toString()
        activity.runOnUiThread {
            Toast.makeText(activity,to+"   "+(Calendar.getInstance().timeInMillis-startTime).toString()+" with port $port",Toast.LENGTH_LONG).show()
        }
//        Log.i("TAKENTIME",(Calendar.getInstance().timeInMillis-startTime).toString())
        return port.toString()

    }

    public fun getAppUsers() : MutableList<String>
    {
        var appUsers: MutableList<String>
        appUsers = mutableListOf()
        var countFounded = 0
        var max_limit = 9999999
        var AppUsersListWithName: JSONArray

        var listOfAlltimeConnectedDevices = WifiService.getClientList(context)
        var answerList = mutableListOf<Any>()
        var myResponse: String
        var ctr = 0
        for (i in listOfAlltimeConnectedDevices)
        {
            myResponse = ""
            var url = "http://" + i + ":5000/"

            AsyncHttpClient.getDefaultInstance().executeJSONObject(AsyncHttpRequest(Uri.parse(url), "GET"), object : AsyncHttpClient.JSONObjectCallback()
            {
                override fun onCompleted(e: java.lang.Exception?, source: AsyncHttpResponse?, result: JSONObject?)
                {
                    ctr += 1
                    if (e != null)
                    {
                        Log.i("mytag", e.message.toString())
                        return
                    }
                    Log.i("mytag", "I got a string: ${result.toString()}")
                    try
                    {
                        var name = JSONObject(result.toString())["name"].toString()
                        appUsers.add(i)
                    }
                    catch (ex: Exception)
                    {
                        Log.i("mytag", ex.message.toString())
                    }
                }

            })
        }
        while (true)
        {
            if (ctr == listOfAlltimeConnectedDevices.size)
            {
                break
            }
        }
        return appUsers
    }


    fun loadMessages()
    {
        if (DATABASE_HANDLER?.allMessages?.size != 0)
        {
            for (i in DATABASE_HANDLER?.allMessages!!)
            {
                var myIp: String? = WifiService.getIpAddress192type(context)
                Log.i("tag1","my ip is $myIp")
                Log.i("tag1","my receiver ip is $receiverIP")
                Log.i("tag1","my i from is ${i.from}")
                Log.i("tag1","my i recever is ${i.receiver}")

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

    companion object
    {
        var outFromServerHashMap=HashMap<String,PrintWriter>()
        var outFromClient:PrintWriter?=null
    }
}