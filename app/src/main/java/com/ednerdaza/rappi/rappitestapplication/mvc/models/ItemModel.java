package com.ednerdaza.rappi.rappitestapplication.mvc.models;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.helpers.JsonArrayListRequest;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.base.VolleyQueue;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces.ItemModelInterface;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntity;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntityResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by administrador on 8/01/17.
 */
public class ItemModel {


    public static void getItems(final Context context, final ItemModelInterface itemModelInterface){

        String url = Config.BASE_URL;
        final Gson gson = new Gson();
        Log.v(Config.LOG_TAG, " <ItemModel> URL --> " + url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response==null){
                    Log.v(Config.LOG_TAG, "Response --> Cadena nula "+response);
                }else{
                    Log.v(Config.LOG_TAG, "Response --> Cadena buena "+response);
                }

                ItemEntityResponse res = gson.fromJson(response.toString(), ItemEntityResponse.class);
                Log.v(Config.LOG_TAG, "Res --> "+res);

                if (res != null){
                    itemModelInterface.completeSuccess(res);
                }else {
                    itemModelInterface.completeFail(context.getResources().getString(R.string.error_response_null));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(Config.LOG_TAG, "ERROR --> "+error);
                itemModelInterface.completeFail(context.getResources().getString(R.string.error_listener));
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleyQueue.addToRequestQueue(request, "Item");
    }
}
