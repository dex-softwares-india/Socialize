package com.example.mkmnim.socialize.Utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast

/**
 * Created by nimish on 10/3/18.
 */
open class WifiStateChangeReceiver:BroadcastReceiver()
{

    override fun onReceive(context: Context?, intent: Intent?)
    {
        var wifi = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (wifi.isWifiEnabled)
        {
            var myBroadcastIntent = Intent(WIFI_STATE_CHANGE)
            myBroadcastIntent.putExtra("value", "true")
            LocalBroadcastManager.getInstance(context).sendBroadcast(myBroadcastIntent)
        }
        else if (wifi.isWifiEnabled!=true)
        {
            var myBroadcastIntent1 = Intent(WIFI_STATE_CHANGE)
            myBroadcastIntent1.putExtra("value", "false")
            LocalBroadcastManager.getInstance(context).sendBroadcast(myBroadcastIntent1)
        }


    }
}