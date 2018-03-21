package com.example.mkmnim.socialize.Utilities.API

import android.content.Context
import com.example.mkmnim.socialize.RequestClass.GETRequestAsyncTask
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


    fun createAllConnectedDevices(context: Context)
    {

        var jsonObject=JSONObject()


        server?.get("/all_users", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                var listOfConnectedDevices=WifiService.getClientList(context)
                jsonObject.put("Users:",listOfConnectedDevices.toString())
                response.send(jsonObject)
            }

        })
    }


    fun createOnlyConnectedDevices(context: Context)
    {

        var jsonObject=JSONObject()


        server?.get("/connected_users", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                var listOfAlltimeConnectedDevices=WifiService.getClientList(context)
                var answerList= mutableListOf<Any>()
                for (i in listOfAlltimeConnectedDevices)
                {
                    answerList.add(GETRequestAsyncTask(context).execute("http://"+i+":5000/").get())

                }
                jsonObject.put("Connected Users:",answerList.toString())
                response.send(jsonObject)
//                response.send(answerList.toString())
            }

        })
    }


}