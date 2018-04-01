package com.example.mkmnim.socialize.Controllers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.Utilities.DATABASE_HANDLER
import org.json.JSONObject
import java.io.PrintWriter


class ConnectedUsersFragment:android.support.v4.app.Fragment(),AdapterView.OnItemClickListener
{
    var myView: View? = null
    lateinit var connectedDevices: ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
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
            DATABASE_HANDLER!!.deleteAllMessages()
        }


        if (WifiService.isHotspotOn(context) && !CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE)
        {
            ConnectToClientSocket(5001) //writing to server

        }

        if (WifiService.isWifiOn(context) && !CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE)
        {
//            var temporaryPort=5004   //receive the port by requesting from Port page
            var temporaryPort=5004 //for micromax instead of 5123
            Log.i("mytag","inConnected User fragment Change temporary port by requesting from port page")
            CreateServerHostWithDifferentPorts(temporaryPort)
            //for each device only one statement

        }

        CONNECTED_USERS_FRAGMENT_INITIALIZED_ONCE=true


        return myView!!
    }

    fun ConnectToClientSocket(port: Int) //5001 server mobile accepting (that is whose hotspot is on)
    {
        Log.i("mytag", "ConnectToClientSocket")
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
                        DATABASE_HANDLER?.addMessage(Message(messageContentText,to,from))
                    }

                    if (WifiService.isHotspotOn(context))
                    {
//                        Log.i("mytag","send to $to from $from")
//                        if self ip
                        if (to=="192.168.43.1") //to==admin Ip
                        {
                            DATABASE_HANDLER?.addMessage(Message(messageContentText,to,from))
                        }
                        else
                        {
                            //send message to "to"
                            Thread(Runnable {
                                try
                                {
//                                    var port=getPortForToIp()  //port of receiver
                                    val socket = Socket(to, 5123)//port of receiver
                                    var outFromServer:PrintWriter? = PrintWriter(socket.getOutputStream())
                                    outFromServer?.println(messageContent.toString())
                                    outFromServer?.flush()
                                }
                                catch (ex:Exception)
                                {
                                    Log.i("mytag",ex.toString()+ex.message.toString()+"in Sending a message through admin")
                                }

                            }).start()

                        }



                    }




                    //else
                    //send messageContent to "to"

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


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        var initiateMessagingFragment = MessagingFragment()
        var args = Bundle()

        args.putInt("position", position)
        args.putStringArrayList("devices", connectedDevices)

        initiateMessagingFragment.arguments = args
        replaceFragment(initiateMessagingFragment)
    }

    fun replaceFragment(someFragment: android.support.v4.app.Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, someFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}