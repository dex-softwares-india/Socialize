package com.example.mkmnim.socialize.Controllers

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.mkmnim.socialize.R
import com.example.mkmnim.socialize.Utilities.API.PageCreator
import com.example.mkmnim.socialize.Utilities.WifiService
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment:android.support.v4.app.Fragment(),View.OnClickListener
{
    var myView:View?=null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        myView=inflater?.inflate(R.layout.fragment_main,container,false)
        myView!!.Go.setOnClickListener(this)
        return myView!!

    }


    fun onGoPressed(view: View?)
    {
        var wifi=activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

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

            },1000)
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

            },1000)
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