package com.example.mkmnim.socialize.Controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.RequestClass.GETRequestAsyncTask
import com.example.mkmnim.socialize.Utilities.HOTSPOT_STATE_CHANGE
import com.example.mkmnim.socialize.Utilities.HotspotStateChangeReceiver
import com.example.mkmnim.socialize.Utilities.WifiStateChangeReceiver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
//    lateinit var wifiStateChangeReceiver:BroadcastReceiver
    lateinit var hotspotStateChangeReceiver: BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var wifi=applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        wifiStateChangeReceiver=object:BroadcastReceiver()
//        {
//            override fun onReceive(context: Context?, intent: Intent?)
//            {
//                var wifi=context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                if (wifi.isWifiEnabled)
//                    Toast.makeText(context,"wifi enabled", Toast.LENGTH_SHORT).show()
//                else
//                    Toast.makeText(context,"wifi disabled", Toast.LENGTH_SHORT).show()
//
//            }
//        }
        hotspotStateChangeReceiver=object:BroadcastReceiver()
        {
            override fun onReceive(context: Context?, intent: Intent?)
            {
                var action = intent?.getAction()
                var state = intent?.getStringExtra("value")
            }
        }


//        LocalBroadcastManager.getInstance(this).registerReceiver(wifiStateChangeReceiver,IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"))
        LocalBroadcastManager.getInstance(this).registerReceiver(hotspotStateChangeReceiver, IntentFilter(HOTSPOT_STATE_CHANGE))





//
//        var sharedPreferences:SharedPreferences=getSharedPreferences("com.example.mkmnim.socialize", Context.MODE_PRIVATE)
//        sharedPreferences.edit().putString("users","none").apply()
//        Log.i("mytag",sharedPreferences.getString("users","default"))


        fab.setOnClickListener{ view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            var myRequest=GETRequestAsyncTask(this)
            myRequest.execute("http://192.168.0.105:9213/user1")

        }



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
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

//        LocalBroadcastManager.getInstance(this).unregisterReceiver(wifiStateChangeReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(hotspotStateChangeReceiver)
        super.onDestroy()
    }
}
