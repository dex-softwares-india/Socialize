package com.example.mkmnim.socialize.Utilities.API

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mkmnim.socialize.RequestClass.GETRequestAsyncTask
import com.example.mkmnim.socialize.Utilities.WifiService
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import com.koushikdutta.async.http.server.HttpServerRequestCallback
import org.json.JSONObject
import java.util.concurrent.TimeUnit

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
                    var asyncTask=GETRequestAsyncTask(context)
                    var stringResponse=asyncTask.execute("http://"+i+":5000/").get()
                    Log.i("mytag","string Response is "+stringResponse)
                    if (stringResponse!="")
                    {
                        answerList.add(stringResponse+"[${stringResponse}]")
                    }

                }
                jsonObject.put("Connected Users:",answerList.toString())
                response.send(jsonObject)
//                response.send(answerList.toString())
            }

        })
    }


    fun createOnlyConnectedDevices1(context: Context)
    {

        var jsonObject=JSONObject()


        server?.get("/connected_users", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                var listOfAlltimeConnectedDevices=WifiService.getClientList(context)
                var answerList= mutableListOf<Any>()
                var myResponse:String
                var ctr=0
                for (i in listOfAlltimeConnectedDevices)
                {

                    myResponse=""
                    var url="http://"+i+":5000/"
                    Log.i("mytag",url)
                    val myRequest=object: StringRequest(url,
                            Response.Listener { volleyresponse ->
                                ctr += 1
                                Log.i("mytag", "my volley reponse is" + volleyresponse)
                                myResponse = volleyresponse.toString()
                                var myJsonResponse=JSONObject(myResponse)["name"]
                                Log.i("mytag", myResponse)
                                Log.i("mytag", "asnwer list is $answerList")
                                if (myResponse != "")
                                {
                                    Log.i("mytag", myJsonResponse.toString())
                                    answerList.add(url + "[${myJsonResponse.toString()}]")
                                }
                                if (ctr == listOfAlltimeConnectedDevices.size)
                                {
                                    jsonObject.put("Connected Users:", answerList)
                                    Log.i("mytag", "in if block of response")
                                    response.send(jsonObject)

                                }

                            },
                            Response.ErrorListener { error ->
                                ctr += 1
                                myResponse = ""
                                Log.i("mytag", error.toString())
                                if (ctr == listOfAlltimeConnectedDevices.size)
                                {
                                    jsonObject.put("Connected Users:", answerList)
                                    Log.i("mytag", "in if block of error")
                                    response.send(jsonObject)

                                }

                            })
                    {}


                    var queue: Request<String>?= Volley.newRequestQueue(context).add(myRequest)


                }


//                response.send(answerList.toString())
            }

        })
    }


}