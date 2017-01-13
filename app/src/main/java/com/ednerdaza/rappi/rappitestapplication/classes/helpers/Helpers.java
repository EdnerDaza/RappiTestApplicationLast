package com.ednerdaza.rappi.rappitestapplication.classes.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;

/**
 * Created by administrador on 10/01/17.
 */

public class Helpers {

    public static Activity activity;
    public static Activity getActivity() {
        return activity;
    }
    public static void setActivity(Activity activity) {
        Helpers.activity = activity;
    }

    public static Context _contexto;
    public static Context getContexto() {
        return _contexto;
    }
    public static void setContexto(Context _contexto) {
        Helpers._contexto = _contexto;
    }

    private static ProgressDialog progressDialog;
    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }
    public static void setProgressDialog(ProgressDialog progressDialog) {
        Helpers.progressDialog = progressDialog;
    }

    private static Intent intent;


    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static AlertDialog customDialogConnection(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(resources.getString(R.string.store));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton("Ir a Configuracion de Red",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        getActivity().startActivity(intent);
                    }
                });

        final AlertDialog dialog = builder.create();
        return dialog;

    }

    public static AlertDialog customDialogMessage(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        TextView msg = new TextView(getActivity());
        msg.setGravity(Gravity.CENTER);
        return dialog;

    } // FIN DEL METODO

    public static ProgressDialog customProgressDialog(){
        if (progressDialog != null){
            customProgressDialogClose();
        }
        progressDialog = new ProgressDialog(getActivity());
        return progressDialog;

    }

    public static void customProgressDialogClose() {
        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }
    /**
     * METODO QUE REVISA SI HAY CONEXION A INTERNET Y SI NO DEVUELVE UN DIALOG
     * @param context
     */
    public static Boolean testConectionInternet(Context context) {
        if(Helpers.isNetworkAvailable(context)){
            //Toast.makeText(getApplicationContext(), "con conexion", Toast.LENGTH_SHORT).show();
            Log.v(Config.LOG_TAG, "<< CON CONEXION >> "+Helpers.isNetworkAvailable(context));
            //syncItems();
            return true;
        }else{
            //Toast.makeText(getApplicationContext(), "SIN CONEXION", Toast.LENGTH_SHORT).show();
            Log.v(Config.LOG_TAG, "<< SIN CONEXION >> "+Helpers.isNetworkAvailable(context));
            //MUESTRA UN DIALOG
            Helpers.customDialogConnection(String.format(context.getResources().getString(R.string.error_conexion),
                    context.getResources().getString(R.string.app_name))).show();
            return false;
        }
    }

}
