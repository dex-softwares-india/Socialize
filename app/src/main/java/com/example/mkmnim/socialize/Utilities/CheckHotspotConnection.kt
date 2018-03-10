package com.example.mkmnim.socialize.Utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.mkmnim.socialize.WifiService



/**
 * Created by nimish on 10/3/18.
 */


class CheckHotSpotConnection(context: Context)  : Runnable
{
    var context=context
    override fun run()
    {
        Looper.prepare()
        var i = 0
        while (DISCOVER_CLIENTS)
        {
//            Log.i("mytag","DISCOVERING_Clients")
            Toast.makeText(context,"Discovering Clients",Toast.LENGTH_SHORT).show()
            i = WifiService.getConnectedDevices(context).size
            if (i>=1)
            {
                //client discovered
                Log.i("mytag","DISCOVERING_Clients if block")
                Log.i("mytag",WifiService.getConnectedDevices(context)[0].toString())
                try
                {
                    Thread.sleep(3000)
                }
                catch (e: InterruptedException)
                {
                    e.printStackTrace()
                }
                //disable client discovery to end thread
            }
            else
            {
                Log.i("mytag","DISCOVERING_Clients else block")
//                Toast.makeText(context,"Discovering Clients else block",Toast.LENGTH_SHORT).show()
                try
                {
                    Thread.sleep(3000)
                }
                catch (e: InterruptedException)
                {
                    e.printStackTrace()
                }

            }
        }
    }
}