package com.example.mkmnim.socialize.RequestClass

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

/**
 * Created by nimish on 9/3/18.
 */
class GETRequestAsyncTask(context:Context):AsyncTask<String,Int,String>()
{
    var context=context
    /**
     * Runs on the UI thread after [.publishProgress] is invoked.
     * The specified values are the values passed to [.publishProgress].
     *
     * @param values The values indicating progress.
     *
     * @see .publishProgress
     *
     * @see .doInBackground
     */
    override fun onProgressUpdate(vararg values: Int?)
    {
        super.onProgressUpdate(*values)
    }

    /**
     *
     * Runs on the UI thread after [.doInBackground]. The
     * specified result is the value returned by [.doInBackground].
     * This method won't be invoked if the task was cancelled.
    */
    override fun onPostExecute(result: String?)
    {
        super.onPostExecute(result)
    }

    /*
     * Override this method to perform a computation on a background thread
     * @return A result, defined by the subclass of this task.
     */
    override fun doInBackground(vararg params: String?): String?
    {
        var url=params[0]
        val myRequest=object: StringRequest(url,Response.Listener {
            response ->
            Log.i("mytag",response.toString())
        },Response.ErrorListener {error->
            Log.i("mytag",error.toString())
        }
        )
        {

        }
        Volley.newRequestQueue(context).add(myRequest)
        return params[0]

    }


}
