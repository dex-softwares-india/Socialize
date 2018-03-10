package com.example.mkmnim.socialize.Utilities.Broadcast_Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.support.v4.content.LocalBroadcastManager
import com.example.mkmnim.socialize.Utilities.HOTSPOT_STATE_CHANGE

/**
 * Created by nimish on 10/3/18.
 */
open class HotspotStateChangeReceiver:BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        var action = intent?.getAction();
        if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action))
        {
            var state = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
            if (WifiManager.WIFI_STATE_ENABLED == state!!%10)
            {
                var myBroadcastIntent = Intent(HOTSPOT_STATE_CHANGE)
                myBroadcastIntent.putExtra("value", "true")
                LocalBroadcastManager.getInstance(context).sendBroadcast(myBroadcastIntent)
            }
            else if (WifiManager.WIFI_STATE_DISABLED==state!!%10)
            {
                var myBroadcastIntent1 = Intent(HOTSPOT_STATE_CHANGE)
                myBroadcastIntent1.putExtra("value", "false")
                LocalBroadcastManager.getInstance(context).sendBroadcast(myBroadcastIntent1)
            }
        }
    }
}
