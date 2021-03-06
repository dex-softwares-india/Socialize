package com.example.mkmnim.socialize.Controllers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.*
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.mkmnim.socialize.Databases.DatabaseHandler


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

        //reach directly to messaging fragment from notfication
       // goToMessagingFragment()


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
/*Database Handler added as well*/

    fun setUpConstants()
    {
    DISCOVER_CLIENTS = WifiService.isHotspotOn(this@MainActivity)
    HOTSPOT_ON = WifiService.isHotspotOn(this@MainActivity)
    WIFI_ON = WifiService.isWifiOn(this@MainActivity)
    DATABASE_HANDLER = DatabaseHandler(this@MainActivity)


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

//                selectedFragment=ConnectedUsersFragment()

            }
            R.id.nav_main ->
            {
//                selectedFragment=MainFragment()
            }
            R.id.nav_slideshow ->
            {
//                selectedFragment=MessagingFragment()

            }
            R.id.nav_manage ->
            {

            }
            R.id.nav_share ->
            {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=com.example.mkmnim.socialize")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.nav_feedback ->
            {
                val Email = Intent(Intent.ACTION_SEND)
                Email.type = "text/email"
                Email.putExtra(Intent.EXTRA_EMAIL, arrayOf("nimish4july1998@gmail.com"))
                Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "")
                startActivity(Intent.createChooser(Email, "Send Feedback:"))
//                return true
                val intent = Intent(Intent.ACTION_SEND)//common intent
                intent.data = Uri.parse("mailto:")

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

    override fun onResume()
    {
        super.onResume()
        appInFront = true
    }

    override fun onPause()
    {
        super.onPause()
        appInFront = false
    }

    companion object
    {
        var appInFront:Boolean=false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.sendfile,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {

        Log.i("mytag", "MainActivitiy.onOptionsItemSelected")
        when (item.itemId)
        {
            R.id.menu_send ->
            {
                Log.i("mytag","koh")

            }
            else ->
            {
                Log.i("mytag","hurr")
            }
        }
        return false

    }

    fun goToMessagingFragment()
    {
        try
        {
            if(intent.getStringExtra("fragment")=="messaging")
            {
                var initiateMessagingFragment = MessagingFragment()
                var args = Bundle()

                args.putInt("position", intent.getIntExtra("position",0))
                args.putStringArrayList("devices", intent.getStringArrayListExtra("devices"))

                initiateMessagingFragment.arguments = args
                var fragment=initiateMessagingFragment
                var fragmentManager = getSupportFragmentManager()
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit()

            }
        }
        catch (ex:Exception)
        {
            Log.i("mytag",ex.toString())
        }
    }

}
