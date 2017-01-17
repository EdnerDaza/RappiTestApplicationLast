package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.base;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Created by administrador on 15/01/17.
 */
public class VolleyQueue extends Application {

    private static RequestQueue queue;
    public static final String TAG = "VolleyPatterns";

    public static void createQueue(Context context){
        if(queue==null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    public static RequestQueue getRequestQueue(){
        return queue;
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);

    }


    public static void cancelAll(String tag){
        getRequestQueue().cancelAll(tag);
    }

}
