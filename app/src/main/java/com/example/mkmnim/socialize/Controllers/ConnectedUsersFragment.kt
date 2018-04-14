package com.example.mkmnim.socialize.Controllers

import android.app.AlertDialog
import android.net.Uri
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
import com.example.mkmnim.socialize.Utilities.CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.DATABASE_HANDLER
import com.example.mkmnim.socialize.Utilities.WifiService
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpRequest
import com.koushikdutta.async.http.AsyncHttpResponse
import kotlinx.android.synthetic.main.fragment_chat.view.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import android.content.DialogInterface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import com.example.mkmnim.socialize.Utilities.DEVICES_WITH_GREEN_CIRCLE_FOR_HOST


class ConnectedUsersFragment:android.support.v4.app.Fragment(),AdapterView.OnItemClickListener
{
    var myView: View? = null
    var initiateMessagingFragment:MessagingFragment?=null
    var connectedDevicesWithGreenCircle:MutableSet<String>?=null
    lateinit var connectedDevices: ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {

        getViewSetListenersAdapters(inflater,container,savedInstanceState)

        if (WifiService.isHotspotOn(context) && !CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE)
        {
            ConnectToClientSocket(5001) //writing to server
        }

        if (WifiService.isWifiOn(context) && !CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE) //edit to receive port from portpage
        {
//            var temporaryPort=5004   //receive the port by requesting from Port page
//            var temporaryPort=5004 //for micromax instead of 5123

            Handler().postDelayed(Runnable {

                Log.i("mytag", WifiService.getIpAddress192type(context = activity.baseContext).toString()+"in ConnectedHandler")
                var temporaryPort: String = getPortForToIp(WifiService.getIpAddress192type(context=activity).toString())

                Log.i("mytag", "inConnected User fragment Change temporary port by requesting from port page")

                if (temporaryPort != "None")
                {
                    Log.i("mytag", "creating my host at" + temporaryPort.toString())
                    CreateServerHostWithDifferentPorts(Integer.parseInt(temporaryPort))
                }
                //for each device only one statement
            },2000)
        }

        CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE=true


        return myView!!
    }

    fun getViewSetListenersAdapters(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
    {
        myView = inflater?.inflate(R.layout.fragment_chat, container, false)
        connectedDevices = WifiService.getConnectedDevices(context)
        myView!!.chatListView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, connectedDevices)

        myView!!.chatListView.onItemClickListener=this
        myView!!.searchDevices.setOnClickListener {
            connectedDevices = WifiService.getConnectedDevices(context)
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
                        // do nothing
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
                Log.i("DEVICE","client arrived")
                Log.i("DEVICE",newSocket.inetAddress.toString())


                if (connectedDevices.contains(newSocket.inetAddress.toString()))
                {

                }
                else
                {
                    connectedDevices.add(newSocket.inetAddress.toString()+"hehe")
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

                    var messageContentText=JSONObject(messageContent)["messageContent"].toString()
                    var to=JSONObject(messageContent)["to"].toString()
                    var from=JSONObject(messageContent)["from"].toString()



                    Log.i("mytag","send to $to from $from")

                    if (WifiService.isWifiOn(context))
                    {
                        initiateMessagingFragment?.messages?.add(Message(messageContentText,to,from))
                        initiateMessagingFragment?.myMessageAdapter?.notifyDataSetChanged()
                        DATABASE_HANDLER?.addMessage(Message(messageContentText,to,from))
                    }

                    if (WifiService.isHotspotOn(context))
                    {
//                        if self ip
                        Log.i("tag1","to is"+ to.toString()+"p")
                        if (to=="192.168.43.1") //to==admin Ip
                        {
                            initiateMessagingFragment?.messages?.add(Message(messageContentText,to,from))
                            initiateMessagingFragment?.myMessageAdapter?.notifyDataSetChanged()
                            DATABASE_HANDLER?.addMessage(Message(messageContentText,to,from))
                        }
                        else
                        {
                            //send message to "to"
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
                                catch (ex:Exception)
                                {
                                    Log.i("mytag",ex.toString()+ex.message.toString()+"in Sending a message through admin")
                                }

                            }).start()

                        }



                    }


                    Log.i("mytag","Contact Count : "+DATABASE_HANDLER!!.messagesCount.toString())

                    printAllMessages() //will print all messages linked to DATABASE_HANDLER


                }

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


    public fun getPortForToIp(to:String):String    //returns None or port no
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