package com.example.mkmnim.socialize.RequestClass

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

/**
 * Created by nimish on 2/4/18.
 */
object VolleyService
{
    fun getPort(url:String,context: Context, callBack: VolleyCallBack, complete: (Boolean) -> Unit)
    {

        val registerRequest = object : StringRequest(Method.GET, url, Response.Listener { response ->
            Log.i("mytag","response is "+response.toString())
            callBack.onSuccess(response.toString())
            complete(true)
        }, Response.ErrorListener { error ->
            Log.i("mytag", "error is "+" $error")
            callBack.onSuccess("failed")
            complete(false)

        })

        {}
        Volley.newRequestQueue(context).add(registerRequest)
    }
}