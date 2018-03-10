package com.example.mkmnim.socialize.Utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
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
            Toast.makeText(context, "enabled", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "disabled", Toast.LENGTH_SHORT).show()


    }
}