package com.example.mkmnim.socialize.Utilities.API

import android.content.Context
import com.example.mkmnim.socialize.Utilities.WifiService
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
    var server:AsyncHttpServer?=null
    fun createHomePage(context: Context,name:String,serviceName:String)
    {

        var jsonObject=JSONObject()
        jsonObject.put("name",name.toString())


        server = AsyncHttpServer()

        server?.get("/", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                response.send(jsonObject)
            }

        })
//        server?.listen(5000)
    }


    fun createConnectedDevices(context: Context)
    {

        var jsonObject=JSONObject()


        server?.get("/all_users", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                var listOfConnectedDevices=WifiService.getClientList(context)
                var listOfConnectedDevices1=WifiService.getConnectedDevices(context)
                var listOfConnectedDevices2=WifiService.getConnectedDevicesFromPING("http://192.168.43.1")

                jsonObject.put("Users:",listOfConnectedDevices.toString())
                jsonObject.put("Users1:",listOfConnectedDevices1.toString())
                jsonObject.put("Users2:",listOfConnectedDevices2.toString())
                response.send(jsonObject)
            }

        })
    }



}