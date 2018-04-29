package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.mkmnim.socialize.Adapters.MessageAdapter
import com.example.mkmnim.socialize.Models.Message
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.DATABASE_HANDLER
import com.example.mkmnim.socialize.Utilities.MESSAGING_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.WifiService
import com.github.angads25.filepicker.model.DialogConfigs
import kotlinx.android.synthetic.main.fragment_messaging.view.*
import org.json.JSONObject
import java.util.*
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import com.github.angads25.filepicker.controller.DialogSelectionListener
import java.io.*


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
        myView!!.messagingListView.setSelection(myMessageAdapter.count-1)
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


    fun handleSendButtonEvent()
    {
        Log.i("mytag", "waiting for dialog")
        val properties = DialogProperties()
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root = File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = File(DialogConfigs.DEFAULT_DIR);
        properties.offset = File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        val dialog = FilePickerDialog(context, properties)
        dialog.setTitle("Select files")
        dialog.setDialogSelectionListener { array ->
            for (i in array)
            {
                activity.runOnUiThread {
                    Toast.makeText(context, i.toString(), Toast.LENGTH_SHORT).show()

                    var fileToSend = i;
                    var myFile = File(fileToSend);
                    var mybytearray = ByteArray(myFile.length().toInt())

                    var fis: FileInputStream? = null;

                    try
                    {
                        fis = FileInputStream(myFile);
                        var bis = BufferedInputStream(fis)
                        bis.read(mybytearray, 0, mybytearray.size)


                        Thread(Runnable {
                            val jsonObject = JSONObject()
                            jsonObject.put("fileContent", mybytearray.toString())
                            TODO("add file name and start and end flag")
                            outFromClient?.println(jsonObject.toString())
                            outFromClient?.flush()
                        }).start()

                    }
                    catch (ex: Exception)
                    {
                        Log.i("mytag", ex.toString())
                    }


                }
                //files is the array of the paths of files selected by the Application User.
            }
        }
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu)
    {
        menu.findItem(R.id.menu_send).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        Log.i("mytag",R.id.menu_send.toString())
        when(item?.itemId)
        {
            R.id.menu_send ->
            {
                handleSendButtonEvent()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



}

