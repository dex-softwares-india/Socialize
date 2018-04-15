package com.example.mkmnim.socialize.Controllers

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.RequestClass.VolleyCallBack
import com.example.mkmnim.socialize.Utilities.CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.DATABASE_HANDLER
import com.example.mkmnim.socialize.Utilities.WifiService
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpRequest
import com.koushikdutta.async.http.AsyncHttpResponse
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_messaging.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*


class ConnectedUsersFragment:android.support.v4.app.Fragment(),AdapterView.OnItemClickListener
{
    var myView: View? = null
    var initiateMessagingFragment:MessagingFragment?=null
    var connectedDevicesWithGreenCircle:MutableSet<String>?=null
//    lateinit var connectedDevices: ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {

        getViewSetListenersAdapters(inflater,container,savedInstanceState)

        if (WifiService.isHotspotOn(context) && !CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE)
        {
            ConnectToClientSocket(5001) //hosting 5001
        }

        if (WifiService.isWifiOn(context) && !CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE) //edit to receive port from portpage
        {

            Handler().postDelayed(Runnable {

                var ipFromWifiService=WifiService.getIpAddress192type(context = activity.baseContext).toString()
                Log.i("mytag", ipFromWifiService+"in ConnectedHandler")
                var temporaryPort: String = getPortForToIp(ipFromWifiService).toString()

                if (temporaryPort != "None")
                {
                    Log.i("mytag", "creating my host at" + temporaryPort.toString())
                    CreateServerHostWithDifferentPorts(Integer.parseInt(temporaryPort))
                }
                //for each device only one statement
            },1000)

            Handler().postDelayed({
                connectToServerSocket(5001)
            },1500)
        }

        CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE=true


        return myView!!
    }

    fun getViewSetListenersAdapters(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
    {
        myView = inflater?.inflate(R.layout.fragment_chat, container, false)
        var previousDevices = connectedDevices.clone() as ArrayList<String>

        /* may be uncommented  later*/
//        connectedDevices = WifiService.getConnectedDevices(context)

        for (device in previousDevices)
        {
            if (connectedDevices.contains(device))
            { }
            else
            {
                connectedDevices.add(device)
            }
        }

        myView!!.chatListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, connectedDevices)

        myView!!.chatListView.onItemClickListener=this
        myView!!.searchDevices.setOnClickListener {
            /* may be uncommented  later*/
//            connectedDevices = WifiService.getConnectedDevices(context)
            myView!!.chatListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, connectedDevices)
            myView!!.chatListView.onItemClickListener=this@ConnectedUsersFragment

        }
        myView!!.dataClear.setOnClickListener {
            val builder: AlertDialog.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                builder = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            }
            else
            {
                builder = AlertDialog.Builder(context)
            }
            builder.setTitle("Delete:")
                    .setMessage("Are you sure you want to delete all your chats")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                        // continue with delete
                        DATABASE_HANDLER!!.deleteAllMessages()
                        Toast.makeText(context,"Your all messages are deleted",Toast.LENGTH_SHORT).show()
                    })
                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

        }
    }

    fun ConnectToClientSocket(port: Int) //5001 server mobile accepting (that is whose hotspot is on)
    {
        Log.i("mytag", "ConnectToClientSocket")
        var serverSocket = ServerSocket(port)

        Thread(Runnable {
            while (true)
            {
                var newSocket = serverSocket.accept()
                Log.i("mytag", "client arrived")
                Log.i("mytag", newSocket.inetAddress.toString())

                call_ConnectToServerHostedByEachClient(newSocket.inetAddress.toString().substring(1))


                if (connectedDevices.contains(newSocket.inetAddress.toString().substring(1)))
                {
                    activity.runOnUiThread {
                        Toast.makeText(context, "added in if", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    connectedDevices.add(newSocket.inetAddress.toString().substring(1))
                    activity.runOnUiThread {
                        Toast.makeText(context, "added in else", Toast.LENGTH_SHORT).show()
                    }
                }

                //Devices Sending to Other Devices
                for (device1 in connectedDevices)
                {
                    for (device2 in connectedDevices)
                    {
                        if (device1 != device2)
                        {
                            var connectedDeviceJSONObject = JSONObject()
                            connectedDeviceJSONObject.put("newDevice", device2)
                            sendMessage(device1, connectedDeviceJSONObject.toString())
                        }
                    }
                    var connectedDeviceJSONObject = JSONObject()
                    connectedDeviceJSONObject.put("newDevice", "192.168.43.1")
                    sendMessage(device1, connectedDeviceJSONObject.toString())

                }




                activity.runOnUiThread {
                    myView!!.chatListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, connectedDevices)
                    myView!!.chatListView.onItemClickListener = this@ConnectedUsersFragment
                }


                var reader = BufferedReader(InputStreamReader(newSocket.getInputStream()))
                readContent(reader, newSocket)


            }
        }).start()
    }

    fun CreateServerHostWithDifferentPorts(port: Int)  //client (whose wifi is on) port from homepage
    {
        Log.i("mytag", "CreateServerHostWithDifferentPorts")
        var serverSocket = ServerSocket(port)

        Thread(Runnable {
            while (true)
            {
                var newSocket = serverSocket.accept()
                var reader = BufferedReader(InputStreamReader(newSocket.getInputStream()))
                readContent(reader, newSocket)
            }
        }).start()
    }


    fun readContent(reader: BufferedReader, newSocket: Socket)
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
                activity.runOnUiThread {
                    Toast.makeText(context, messageContent.toString(), Toast.LENGTH_LONG).show()
                    Log.i("mytag","readContent in ConnectedUserFragment")

                    //if JSON object contains a message
                    var receivedJSONObject=JSONObject(messageContent)

                    if (receivedJSONObject.has("newDevice") && WifiService.isWifiOn(context))
                    {
                        if (connectedDevices.contains(receivedJSONObject["newDevice"].toString()))
                        {}
                        else
                        {
                            connectedDevices.add(receivedJSONObject["newDevice"].toString())
                        }

                        activity.runOnUiThread {
                            myView!!.chatListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, connectedDevices)
                            myView!!.chatListView.onItemClickListener = this@ConnectedUsersFragment
                        }

                    }


                    else if (receivedJSONObject.has("messageContent"))
                    {
                        var messageContentText = JSONObject(messageContent)["messageContent"].toString()
                        var to = JSONObject(messageContent)["to"].toString()
                        var from = JSONObject(messageContent)["from"].toString()

                        Log.i("mytag", "send to $to from $from")

                        if (WifiService.isWifiOn(context))
                        {
                            addMessageToDatabase(messageContentText, to, from)

                            if (initiateMessagingFragment != null)
                            {
//                                initiateMessagingFragment!!.messagingListView.setSelection(initiateMessagingFragment!!.myMessageAdapter.count-1)
                            }
                        }

                        if (WifiService.isHotspotOn(context))
                        {
//                        if self ip
                            Log.i("mytag", "to is" + to.toString() + "p")
                            if (to == "192.168.43.1") //to==admin Ip
                            {
                                addMessageToDatabase(messageContentText, to, from)

                                if (initiateMessagingFragment != null)
                                {
                                    initiateMessagingFragment!!.messagingListView.setSelection(initiateMessagingFragment!!.myMessageAdapter.count)
                                }
                            }
                            else
                            {
                                //send message to "to"
                                sendMessage(to, messageContent)
                            }


                        }
                    }




                    Log.i("mytag","Contact Count : "+DATABASE_HANDLER!!.messagesCount.toString())

                    //printAllMessages() //will print all messages linked to DATABASE_HANDLER


                }

            }
        }).start()

    }


    fun addMessageToDatabase(messageContentText: String,to: String,from:String)
    {
        initiateMessagingFragment?.messages?.add(Message(messageContentText, to, from))
        initiateMessagingFragment?.myMessageAdapter?.notifyDataSetChanged()
        DATABASE_HANDLER?.addMessage(Message(messageContentText, to, from))

    }


    fun sendMessage(to:String,messageContent:String)
    {
        Thread(Runnable {
            try
            {
                var port = getPortForToIp(to)  //port of receiver

//                                    val socket = Socket(to, 5004)//above port of receiver
                if (port != "None")
                {
                    val socket = Socket(to, Integer.parseInt(port))
                    var outFromServer: PrintWriter? = PrintWriter(socket.getOutputStream())
                    outFromServer?.println(messageContent.toString())
                    outFromServer?.flush()
                }
            }
            catch (ex: Exception)
            {
                Log.i("mytag", ex.toString() + ex.message.toString() + "in Sending a message through admin")
            }

        }).start()
    }


    fun printAllMessages()
    {
        for (i in DATABASE_HANDLER!!.allMessages)
        {
            Log.i("mytag",i.message.toString()+" from:"+i.from.toString()+" to "+i.receiver.toString())
        }
    }


    public fun getPortForToIps(to:String):String    //returns None or port no
    {
        var requestString="http://"+to+":5000/portno"
        Log.i("mytag","request string is $requestString")
        var port:String=""  //possible values ["",None,port no]

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


        while(true)
        {

            Log.i("mytag", "stuck in looping volley port")
            Log.i("mytag","port is $port")


            if (port != "")
            {
                Log.i("mytag","answerFromPortForIp is $port while looking $requestString")
                break
            }
        }
//        return 1234.toString()
        return port.toString()

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

        var startTime= Calendar.getInstance().timeInMillis
        while(true)
        {

//            Log.i("mytag", "stuck in looping volley port is $port")
//            Log.i("MYTIME",(Calendar.getInstance().timeInMillis-startTime).toString())

            if ((Calendar.getInstance().timeInMillis-startTime)>1000)
            {
                Log.i("mytag","quitting after trying connecting for 1 second in connectedUserfragment")
                port="None"
            }

            if (port != "")
            {
                Log.i("mytag","answerFromPortForIp is $port while looking $requestString in connected user fragment")
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


    fun connectToServerSocket(port:Int) //5001  //receiver mobile (that is whose wifi is on)
    {
        Log.i("mytag","connectToServerSocket")

        Thread(Runnable {
            try
            {
                val socket = Socket("192.168.43.1", port)  //use 1 instead of 76 -  -  - host ip for testing using 192.168.43.76
//                    val socket = Socket("10.132.240.103", port)

                MessagingFragment.outFromClient = PrintWriter(socket.getOutputStream())

            }
            catch (ex:Exception)
            {
                Log.i("mytag",ex.message.toString()+"in connectToServerSocket")
            }

        }).start()


    }



    fun call_ConnectToServerHostedByEachClient(i:String)
    {

        Thread(Runnable {
            try
            {
                var myPort = getPortForToIp(i)
                if (myPort != "None")
                {
                    ConnectToServerSocketHostedByEachClient(i, Integer.parseInt(getPortForToIp(i)))
                }
            }
            catch (ex: Exception)
            {
                Log.i("mytag", ex.message.toString() + "in ConnectToServerSocketHostedByEachClient for " + i.toString())
            }
        }).start()

    }

    fun ConnectToServerSocketHostedByEachClient(host:String,port:Int)  //receiver mobile (that is whose hotspot is on)
    {

        Log.i("mytag", "ConnecttoServerSocketHostedByeachClient")
        Thread(Runnable {
            try
            {
                val socket = Socket(host, port)  //use 1 instead of 76 -  -  - host ip for testing using 192.168.43.76
                var outFromServer = PrintWriter(socket.getOutputStream())
                MessagingFragment.outFromServerHashMap[host] = outFromServer
            }
            catch (ex: Exception)
            {
                Log.i("mytag", ex.message.toString() + "in ConnectToServerSocketHostedByEachClient")
            }

        }).start()
    }

    companion object
    {
        var connectedDevices=ArrayList<String>()
    }




    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        initiateMessagingFragment = MessagingFragment()
        var args = Bundle()

        args.putInt("position", position)
        args.putStringArrayList("devices", connectedDevices)

        initiateMessagingFragment!!.arguments = args
        replaceFragment(initiateMessagingFragment!!)
    }

    fun replaceFragment(someFragment: android.support.v4.app.Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, someFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}