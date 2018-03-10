package com.example.mkmnim.socialize

import android.content.Context
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
}