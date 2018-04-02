package com.example.mkmnim.socialize.RequestClass

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class GETRequestAsyncTaskWithCallback(context:Context,callBack: VolleyCallBack):AsyncTask<String,Int,String>()
{
    var callback=callBack
    var context=context
    override fun doInBackground(vararg params: String?): String?
    {
        var url=params[0]
        var myResponse:String?="khaali"
        val myRequest=object: StringRequest(url,Response.Listener {
            response ->
            myResponse=response.toString()
            Log.i("mytag",myResponse)
            callback.onSuccess(myResponse.toString())

        },Response.ErrorListener {error->
            myResponse=""
            Log.i("mytag",error.toString())
            callback.onSuccess("failed")
        }
        )
        {

        }
        var queue:Request<String>?=Volley.newRequestQueue(context).add(myRequest)
//        while(!queue!!.hasHadResponseDelivered())
//        {
//
//            Log.i("mytag","response na aara bhaiya")
//        }
        return myResponse



    }


}
