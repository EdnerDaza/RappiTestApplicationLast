package com.ednerdaza.rappi.rappitestapplication.mvc.models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.helpers.Helpers;
import com.ednerdaza.rappi.rappitestapplication.classes.helpers.JsonArrayListRequest;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.base.VolleyQueue;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces.ItemModelInterface;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntity;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntityResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by administrador on 8/01/17.
 */
public class ItemModel {

    public static void getItems(final Context context, final ItemModelInterface itemModelInterface){

        String url = Config.BASE_URL_JSON;

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

    public static void getItemsAssets(final Context context, final ItemModelInterface itemModelInterface){

        final Gson gson = new Gson();
        String json = null;
        try {
            InputStream is = context.getAssets().open( Config.BASE_JSON);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject obj = new JSONObject(json);

            if (obj==null){
                Log.v(Config.LOG_TAG, "Response --> Cadena nula "+obj);
            }else{
                Log.v(Config.LOG_TAG, "Response --> Cadena buena "+obj);
            }

            ItemEntityResponse res = gson.fromJson(obj.toString(), ItemEntityResponse.class);
            Log.v(Config.LOG_TAG, "Res --> "+res);

            if (res != null){
                itemModelInterface.completeSuccess(res);
            }else {
                itemModelInterface.completeFail(context.getResources().getString(R.string.error_response_null));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            itemModelInterface.completeFail(context.getResources().getString(R.string.error_response_null));
        }

    }

    public static void getItemsDownload(final Context context, final String path,
                                        final ItemModelInterface itemModelInterface){

        final Gson gson = new Gson();
        String json = null;

        try {
            File dir = Environment.getExternalStorageDirectory();
            File yourFile = new File(path);
            FileInputStream stream = new FileInputStream(yourFile);
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                json = Charset.defaultCharset().decode(bb).toString();
            }
            finally {
                stream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject obj = new JSONObject(json);

            if (obj==null){
                Log.v(Config.LOG_TAG, "Response --> Cadena nula "+obj);
            }else{
                Log.v(Config.LOG_TAG, "Response --> Cadena buena "+obj);
            }

            ItemEntityResponse res = gson.fromJson(obj.toString(), ItemEntityResponse.class);
            Log.v(Config.LOG_TAG, "Res --> "+res);

            if (res != null){
                itemModelInterface.completeSuccess(res);
            }else {
                itemModelInterface.completeFail(context.getResources().getString(R.string.error_response_null));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            itemModelInterface.completeFail(context.getResources().getString(R.string.error_response_null));
        }

    }

}
