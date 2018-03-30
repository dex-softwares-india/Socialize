package com.example.mkmnim.socialize.Utilities.API

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mkmnim.socialize.RequestClass.GETRequestAsyncTask
import com.example.mkmnim.socialize.Utilities.NO_OF_CLIENTS
import com.example.mkmnim.socialize.Utilities.WifiService
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import com.koushikdutta.async.http.server.HttpServerRequestCallback
import org.json.JSONObject
import java.util.*
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


    fun createAllConnectedDevices(context: Context)     //create page at /all_users
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


    fun createOnlyConnectedDevices(context: Context)   //delete later
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


    fun createOnlyConnectedDevices1(context: Context)  //create /connected_users
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
                    val myRequest=object: StringRequest(url,
                            Response.Listener { volleyresponse ->
                                ctr += 1
                                myResponse = volleyresponse.toString()
                                var myJsonResponse=JSONObject(myResponse)["name"]

                                if (myResponse != "")
                                {
                                    Log.i("mytag", myJsonResponse.toString())
                                    answerList.add(url + ",${myJsonResponse.toString()}")
                                }
                                if (ctr == listOfAlltimeConnectedDevices.size)
                                {
                                    jsonObject.put("Connected Users:", answerList)
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


    fun createOnlyConnectedDeviceswithPort(context: Context)  //create /connected_users
    {

        var jsonObject=JSONObject()


        server?.get("/portnos", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                var listOfAlltimeConnectedDevices=WifiService.getClientList(context)
                var answerList= mutableListOf<Any>()
                var myResponse:String
                jsonObject.put("id","1")
                NO_OF_CLIENTS=0
                for (i in listOfAlltimeConnectedDevices)
                {

                    myResponse=""
                    var url= "http://$i:5000/"
                    val myRequest=object: StringRequest(url,
                            Response.Listener { volleyresponse ->
                                NO_OF_CLIENTS+=1
                                myResponse = volleyresponse.toString()

                                if (myResponse != "")
                                {
                                    Log.i("mytag", myResponse.toString())
                                    jsonObject.put(url,5001+ NO_OF_CLIENTS)
                                }
                                if (NO_OF_CLIENTS == listOfAlltimeConnectedDevices.size)
                                {
                                    response.send(jsonObject)
                                }

                            },
                            Response.ErrorListener { error ->
                                NO_OF_CLIENTS += 1
                                myResponse = ""
                                Log.i("mytag", error.toString())
                                if (NO_OF_CLIENTS == listOfAlltimeConnectedDevices.size)
                                {
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


    fun createPortPage()
    {
        val jsonObject=JSONObject()


        server?.get("/portno", object : HttpServerRequestCallback
        {
            override fun onRequest(request: AsyncHttpServerRequest, response: AsyncHttpServerResponse)
            {
                var randomInt=Random().nextFloat()
                jsonObject.put("port",(5002+(100*randomInt)).toInt())
                response.send(jsonObject)
            }

        })
    }

}


