package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.API.PageCreator
import com.example.mkmnim.socialize.Utilities.MAIN_FRAGMENT_INITIALIZED_ONCE
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment:android.support.v4.app.Fragment(),View.OnClickListener
{
    var myView:View?=null
    var mysharedPrefs:SharedPreferences?=null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        myView=inflater?.inflate(R.layout.fragment_main,container,false)

        //Getting name from SharedPrefs
        mysharedPrefs=activity.getSharedPreferences("com.example.socialize",Context.MODE_PRIVATE)


        myView!!.Username.setText(mysharedPrefs?.getString("username",""))

        if (MAIN_FRAGMENT_INITIALIZED_ONCE)
        {
            myView!!.Username.isEnabled=false
        }

        myView!!.Go.setOnClickListener(this)

        //playing GIF
        Glide.with(context)
                .load(R.drawable.butwhataboutsocialization)
                .into(myView!!.whataboutgif)
        return myView!!

    }


    fun onGoPressed(view: View?)
    {
        MAIN_FRAGMENT_INITIALIZED_ONCE=true
        var wifi=activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mysharedPrefs?.edit()?.putString("username",myView!!.Username.text.toString())?.apply()
        if (wifi.isWifiEnabled)
        {
            myView!!.progressBar.visibility=View.VISIBLE
            hideKeyboardFromNameInputScreen()
            Handler().postDelayed(Runnable {
                PageCreator.createHomePage(activity.applicationContext, myView!!.Username.text.toString(), "None")
                PageCreator.createPortPage()
                myView!!.progressBar.visibility=View.INVISIBLE
                PageCreator.server?.listen(5000)
                Log.i("mytag","all pages created")

            },500)
        }
        if (WifiService.isHotspotOn(context))
        {
            myView!!.progressBar.visibility=View.VISIBLE
            hideKeyboardFromNameInputScreen()
            Handler().postDelayed(Runnable {

                PageCreator.createHomePage(activity.applicationContext, myView!!.Username.text.toString(), "None")
                PageCreator.createAllConnectedDevices(context)
                PageCreator.createOnlyConnectedDevices1(context)
                PageCreator.server?.listen(5000)
                Log.i("mytag","all pages created")

                myView!!.progressBar.visibility=View.INVISIBLE

            },500)
        }
    }





    private fun hideKeyboardFromNameInputScreen()
    {

        var im=activity.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(
                myView!!.Username.getWindowToken(), 0);
    }

    override fun onClick(v: View?)
    {
        v?.isEnabled=false
        onGoPressed(v)
        hideKeyboardFromNameInputScreen()

        var chatFragment=ConnectedUsersFragment()
        replaceFragment(chatFragment)
    }


    fun replaceFragment(someFragment: android.support.v4.app.Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, someFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}