package com.example.mkmnim.socialize.Controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.telecom.Connection
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.RequestClass.GETRequestAsyncTask
import com.example.mkmnim.socialize.Utilities.*
import com.example.mkmnim.socialize.WifiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.chat_activity.*


//DISCOVER_CLIENTS WILL BE TRUE WHEN HOTSPOT IS ON
//i.e. Constant Searching is on when HOTSPOT IS ON



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    lateinit var wifiStateChangeReceiver:BroadcastReceiver
    lateinit var hotspotStateChangeReceiver: BroadcastReceiver
    var connectedClientslist:ArrayList<String>?=null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        DISCOVER_CLIENTS = WifiService.isHotspotOn(this@MainActivity)
        HOTSPOT_ON = WifiService.isHotspotOn(this@MainActivity)
        WIFI_ON = WifiService.isWifiOn(this@MainActivity)



//        if (DISCOVER_CLIENTS)
//        {
//
//            connectedClientslist=WifiService.getConnectedDevices(this@MainActivity)
//            ChatListView.adapter=ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_list_item_1,connectedClientslist)
//
//        }
//        else
//        {
//            connectedClientslist=ArrayList<String>()
//            ChatListView.adapter=ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_list_item_1,connectedClientslist)
//
//        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var wifi=applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        wifiStateChangeReceiver=object:BroadcastReceiver()
        {
            override fun onReceive(context: Context?, intent: Intent?)
            {
                if (intent?.getStringExtra("value")=="true")
                {
                    WIFI_ON=true

                    //on wifi enabled
//                    Toast.makeText(this@MainActivity,"wifi enabled",Toast.LENGTH_SHORT).show()

                }
                else if (intent?.getStringExtra("value")=="false")
                {
                    WIFI_ON=false
                    //on wfi disabled
//                    Toast.makeText(this@MainActivity,"wifi disabled",Toast.LENGTH_SHORT).show()

                }
                Log.i("mytag","wifistatechangereceiver wifi - "+ WIFI_ON)
                Log.i("mytag","wifistatechangereceiverhotspot - "+ HOTSPOT_ON)

            }
        }


        hotspotStateChangeReceiver=object:BroadcastReceiver()
        {
            override fun onReceive(context: Context?, intent: Intent?)
            {

                if (intent?.getStringExtra("value")=="true")
                {

//                    Toast.makeText(this@MainActivity,"hotspot enabled",Toast.LENGTH_SHORT).show()
                    DISCOVER_CLIENTS=true
                    HOTSPOT_ON=true



                    //start Scannning
                    try
                    {
//                        this@MainActivity.runOnUiThread(CheckHotSpotConnection(this@MainActivity))
//                      Log.i("mytag","hello")
                        var checkHotspotThread=Thread(CheckHotSpotConnection(this@MainActivity))
                        checkHotspotThread.start()
                    }
                    catch(ex:Exception)
                    {
                        Toast.makeText(this@MainActivity,ex.message.toString(),Toast.LENGTH_LONG).show()
                    }



                    //on hotspot enabled page is created


                }
                else if (intent?.getStringExtra("value")=="false")
                {
//                    Toast.makeText(this@MainActivity,"hotspot disabled",Toast.LENGTH_SHORT).show()

                    DISCOVER_CLIENTS=false
                    HOTSPOT_ON = false
                    //on hotspot disabled
                }
                Log.i("mytag","hotspotstatechangereceiver wifi - "+ WIFI_ON)
                Log.i("mytag","hotspotstatechangereceriver hotspot - "+ HOTSPOT_ON)

            }
        }

        registerReceivers()





//
//        var sharedPreferences:SharedPreferences=getSharedPreferences("com.example.mkmnim.socialize", Context.MODE_PRIVATE)
//        sharedPreferences.edit().putString("users","none").apply()
//        Log.i("mytag",sharedPreferences.getString("users","default"))


        fab.setOnClickListener{ view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

//            var myRequest=GETRequestAsyncTask(this)
//            myRequest.execute("http://192.168.0.105:9213/user1")

//            var list=WifiService.getClientList(this@MainActivity)
            Toast.makeText(this@MainActivity, DISCOVER_CLIENTS.toString(),Toast.LENGTH_SHORT).show()
            connectedClientslist=WifiService.getConnectedDevices(this@MainActivity)
            ChatListView.adapter=ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_list_item_1,connectedClientslist)
            Log.i("mytag",connectedClientslist.toString())
            for (elem in connectedClientslist!!)
            {
                Log.i("mytag",elem.toString())
            }


        }



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }



















    fun registerReceivers()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(wifiStateChangeReceiver, IntentFilter(WIFI_STATE_CHANGE))
        LocalBroadcastManager.getInstance(this).registerReceiver(hotspotStateChangeReceiver, IntentFilter(HOTSPOT_STATE_CHANGE))
    }
    override fun onBackPressed()
    {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
        {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId)
        {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        // Handle navigation view item clicks here.
        when (item.itemId)
        {
            R.id.nav_camera ->
            {
                // Handle the camera action
            }
            R.id.nav_gallery ->
            {

            }
            R.id.nav_slideshow ->
            {

            }
            R.id.nav_manage ->
            {

            }
            R.id.nav_share ->
            {

            }
            R.id.nav_send ->
            {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy()
    {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(wifiStateChangeReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(hotspotStateChangeReceiver)
        super.onDestroy()
    }
}
