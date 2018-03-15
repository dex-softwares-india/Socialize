package com.example.mkmnim.socialize

import android.content.Context
import android.net.wifi.WifiManager
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileReader


/**
 * Created by nimish on 10/3/18.
 */

object WifiService
{
    fun getClientList(context: Context):List<String>
    {
        var macCount = 0
        var br: BufferedReader? = null
        var listOfIp= mutableListOf<String>()
        try
        {
            br = BufferedReader(FileReader("/proc/net/arp"))
            var line: String?
            while (true)
            {
                line = br!!.readLine()
                if (line==null)
                    break
                val splitted = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted != null)
                {
                    // Basic sanity check
                    val mac = splitted[3]
                    println("Mac : Outside If $mac")
                    if (mac.matches("..:..:..:..:..:..".toRegex()))
                    {
                        macCount++
                        listOfIp.add(splitted[0])
                    }
                    /* for (int i = 0; i < splitted.length; i++)
                    System.out.println("Addressssssss     "+ splitted[i]);*/

                }
            }
            return listOfIp
        }
        catch (e: Exception)
        {
            return listOfIp
        }

    }

    fun getConnectedDevices(context:Context): ArrayList<String>
    {
        val arrayList = ArrayList<String>()
        try
        {
            val bufferedReader = BufferedReader(FileReader("/proc/net/arp"))
            while (true)
            {
                val readLine = bufferedReader.readLine() ?: break

                val split = readLine.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split != null && split.size >= 4)
                {
                    Toast.makeText(context,readLine.toString(),Toast.LENGTH_SHORT).show()
                    if (split[0]!="IP")
                    {

                        arrayList.add(split[0])
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        return arrayList
    }
    //check whether wifi hotspot on or off
    fun isHotspotOn(context: Context): Boolean
    {

        val wifimanager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try
        {
            val method = wifimanager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            return method.invoke(wifimanager) as Boolean
        }
        catch (ignored: Throwable)
        {
        }

        return false
    }

    fun isWifiOn(context:Context): Boolean
    {
        var wifi=context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi==null)
            return false
        return wifi.isWifiEnabled
    }
}