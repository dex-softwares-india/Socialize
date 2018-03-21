package com.example.mkmnim.socialize.Controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.*
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.view.View
import android.view.inputmethod.InputMethodManager


//DISCOVER_CLIENTS WILL BE TRUE WHEN HOTSPOT IS ON
//i.e. Constant Searching is on when HOTSPOT IS ON



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    lateinit var wifiStateChangeReceiver:BroadcastReceiver
    lateinit var hotspotStateChangeReceiver: BroadcastReceiver
    lateinit var connectedClientslist:ArrayList<String>
    lateinit var wifi:WifiManager


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setUpConstants()  //DISCOVER_CLIENTS,HOTSPOT ON ,WIFI ON
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        wifi=applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        //declaring receivers for hotspot and wifi  *********
        declareReceivers()
        registerReceivers()
        //****************************************************




//
//        var sharedPreferences:SharedPreferences=getSharedPreferences("com.example.mkmnim.socialize", Context.MODE_PRIVATE)
//        sharedPreferences.edit().putString("users","none").apply()
//        Log.i("mytag",sharedPreferences.getString("users","default"))


        val toggle = object:ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            override fun onDrawerOpened(drawerView: View?)
            {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)

                super.onDrawerClosed(drawerView)
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }





/*Initial fragment added as well*/
    fun setUpConstants()
    {
    DISCOVER_CLIENTS = WifiService.isHotspotOn(this@MainActivity)
    HOTSPOT_ON = WifiService.isHotspotOn(this@MainActivity)
    WIFI_ON = WifiService.isWifiOn(this@MainActivity)

    var fragment=MainFragment() as android.support.v4.app.Fragment
    var fragmentManager = getSupportFragmentManager()
    fragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment).commit()

}


    fun declareReceivers()
    {
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        // Handle navigation view item clicks here.
        var selectedFragment:android.support.v4.app.Fragment?=null
        when (item.itemId)
        {
            R.id.nav_chat ->
            {

                selectedFragment=ChatFragment()

            }
            R.id.nav_main ->
            {
                selectedFragment=MainFragment()
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


        if (selectedFragment != null)
        {
            var fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container,selectedFragment).commit()

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
