package com.example.mkmnim.socialize.Utilities.API

import android.content.Context
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import com.koushikdutta.async.http.server.HttpServerRequestCallback
import org.json.JSONObject

/*
This file is used tp create web page based api
 */
object PageCreator
{
    fun createHomePage(context: Context,name:String,serviceName:String)
    {

        var jsonObject=JSONObject()
        jsonObject.put("name",name.toString())


        val server = AsyncHttpServer()

        server.get("/", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                response.send(jsonObject)
            }

        })
        server.listen(5000)
    }


    fun createConnectedDevices(context: Context)
    {

    }



}