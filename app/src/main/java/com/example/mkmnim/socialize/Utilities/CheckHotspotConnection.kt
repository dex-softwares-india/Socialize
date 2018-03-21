package com.example.mkmnim.socialize.Utilities

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast


/**
 * Created by nimish on 10/3/18.
 * called whenever the hotspot is on
 */


class CheckHotSpotConnection(context: Context)  : Runnable
{
    var context = context
    override fun run()
    {
        try
        {


            Looper.prepare()
            var i = 0
            while (DISCOVER_CLIENTS)
            {
//            Log.i("mytag","DISCOVERING_Clients")
                Toast.makeText(context, "Discovering Clients", Toast.LENGTH_SHORT).show()
                i = WifiService.getConnectedDevices(context).size
                if (i >= 1)
                {
                    //client discovered
                    Log.i("mytag", "DISCOVERING_Clients if block")
                    Log.i("mytag", WifiService.getConnectedDevices(context)[0].toString())
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
                    Log.i("mytag", "DISCOVERING_Clients else block")
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
        catch (ex:Exception)
        {
            Toast.makeText(context,"error in CheckHotspotConnection,${ex.toString()}",Toast.LENGTH_SHORT).show()
        }

    }
}














