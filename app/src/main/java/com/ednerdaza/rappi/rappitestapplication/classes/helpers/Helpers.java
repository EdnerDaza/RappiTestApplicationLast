package com.ednerdaza.rappi.rappitestapplication.classes.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by administrador on 15/01/17.
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

    public static boolean useAssets = true;
    public static boolean isUseAssets() {
        return useAssets;
    }
    public static void setUseAssets(boolean useAssets) {
        Helpers.useAssets = useAssets;
    }

    private static long myDownloadReference;
    public static long getMyDownloadReference() {
        return myDownloadReference;
    }
    public static void setMyDownloadReference(long myDownloadReference) {
        Helpers.myDownloadReference = myDownloadReference;
    }

    private static DownloadManager downloadManager;
    public static DownloadManager getDownloadManager() {
        return downloadManager;
    }
    public static void setDownloadManager(DownloadManager downloadManager) {
        Helpers.downloadManager = downloadManager;
    }

    private static Intent intent;

    public static boolean isNetworkAvailable(Context context){
        Log.v(Config.LOG_TAG, "// isNetworkAvailable(context : "+context+") //\n"+"HELPERS.CLASS");
        ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        Log.v(Config.LOG_TAG, "--  isConnected :"+isConnected+" \n"+"HELPERS.CLASS");
        if (isConnected) {
            boolean isWiFi = activeNetworkInfo.getType() == connectivityManager.TYPE_WIFI;
            boolean isMobile = activeNetworkInfo.getType() == connectivityManager.TYPE_MOBILE;
            if (isWiFi) {
                //Toast.makeText(context, "Connected via WiFi", Toast.LENGTH_SHORT).show();
                Log.v(Config.LOG_TAG, "-- CONECTADO VIA WIFI \n"+"HELPERS.CLASS");
            } else if (isMobile) {
                //Toast.makeText(context, "Connected via Mobile", Toast.LENGTH_SHORT).show();
                Log.v(Config.LOG_TAG, "-- CONECTADO VIA MOBILE \n"+"HELPERS.CLASS");
            }
        } else {
            //Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
            Log.v(Config.LOG_TAG, "-- NO CONECTADO \n"+"HELPERS.CLASS");
        }

        return isConnected;
    }

    public static AlertDialog customDialogConnection(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(resources.getString(R.string.store));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(getContexto().getResources().getString(R.string.accept_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton(getContexto().getResources().getString(R.string.network_button),
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
        builder.setPositiveButton(getContexto().getResources().getString(R.string.accept_button),
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

    public static AlertDialog customDialogDownload (String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(resources.getString(R.string.store));
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setNeutralButton(getContexto().getResources().getString(R.string.download_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        dialog.cancel();
                        setUseAssets(false);
                        cleanDownloadsPublicDir(Config.BASE_JSON);
                    }
                });

        builder.setNegativeButton(getContexto().getResources().getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        dialog.dismiss();
                        setUseAssets(true);
                    }
                });

        final AlertDialog dialog = builder.create();
        return dialog;

    }

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
        Log.v(Config.LOG_TAG, "// testConectionInternet(context : "+context+") //\n"+"HELPERS.CLASS");
        if(Helpers.isNetworkAvailable(context)){
            Log.v(Config.LOG_TAG, "--  TIENES CONEXION \n"+"HELPERS.CLASS");
            return true;
        }else{
            Log.v(Config.LOG_TAG, "--  NO TIENES CONEXION \n"+"HELPERS.CLASS");
            //MUESTRA UN DIALOG
            Helpers.customDialogConnection(String.format(context.getResources().
                    getString(R.string.error_conexion), context.getResources().
                    getString(R.string.app_name))).show();
            return false;
        }
    }

    public static String readFile(String path){
        String jString = null;
        try {
            File dir = Environment.getExternalStorageDirectory();
            File yourFile = new File(path);
            FileInputStream stream = new FileInputStream(yourFile);
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                jString = Charset.defaultCharset().decode(bb).toString();
            }
            finally {
                stream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            jString = e.getMessage();
        }

        return jString;

    }



    public static boolean isFileExistsPublicDir(String filename){
        File folder1 = new File(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                filename);
        return folder1.exists();
    }

    public static boolean deleteFilePublicDir(String filename){
        File folder1 = new File(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                filename);
        return folder1.delete();
    }

    public static boolean isFileExistsPackage(String filename){
        File folder1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + filename);
        return folder1.exists();
    }

    public static boolean deleteFilePackage(String filename){
        File folder1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + filename);
        return folder1.delete();
    }

    public static void cleanDownloadsPublicDir(String filename){
        if(isFileExistsPublicDir(filename)){
            Log.v(Config.LOG_TAG, "EXISTE --> BORREMOSLO");
            deleteFilePublicDir(filename);
            callBroadCast();
            Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getFreeSpace();
        }
        useDownloadJSON(Config.BASE_URL_JSON,
                getContexto().getResources().getString(R.string.download_descripcion),
                getContexto().getResources().getString(R.string.download_title),
                filename,true,
                getDownloadManager());
    }

    public static void cleanDownloadsPackage(String filename){
        if(isFileExistsPublicDir(filename)){
            Log.v(Config.LOG_TAG, "EXISTE --> BORREMOSLO");
            deleteFilePublicDir(filename);
            callBroadCast();
        }
        useDownloadJSON(Config.BASE_URL_JSON,
                getContexto().getResources().getString(R.string.download_descripcion),
                getContexto().getResources().getString(R.string.download_title),
                filename,false,
                getDownloadManager());
    }

    public static boolean haveStoragePermission()
    {
        Log.v(Config.LOG_TAG, "// haveStoragePermission() //\n"+"HELPERS.CLASS");
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContexto().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(Config.LOG_TAG, "--  TIENES PERMISO \n"+"HELPERS.CLASS");
                return true;
            } else {
                Log.v(Config.LOG_TAG, "--  PREGUNTAS POR PERMISO \n"+"HELPERS.CLASS\n...");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.v(Config.LOG_TAG, "--  YA TENIAS PERMISO DESDE EL MANIFEST\n"+"HELPERS.CLASS\n...");
            return true;
        }
    }

    public static void useDownloadJSON(String url, String description, String title, String name,
                                       boolean publicDir, DownloadManager downloadManager) {
        Log.v(Config.LOG_TAG, "useDownloadJSON --> ");

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // set the notification
        request.setDescription(description)
                .setTitle(title);

        if(publicDir){
            // save in the public downloads folder
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);

        }else{
            // set the path to where to save the file save in app package directory
            request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS,
                    name);
        }

        //  make file visible by and manageable by system's download app
        request.setVisibleInDownloadsUi(true);

        // select which network, etc
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE);

        // queue the download
        setMyDownloadReference(downloadManager.enqueue(request));
        // myDownloadReference = downloadManager.enqueue(request);
    }
    public static void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(getContexto(), new String[]{
                            Environment.getExternalStorageDirectory().toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        /*
                         *   (non-Javadoc)
                         * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                         */
                        public void onScanCompleted(String path, Uri uri) {
                            Log.v(Config.LOG_TAG, "Scanned " + path + ":");
                            Log.v(Config.LOG_TAG, "-> uri=" + uri);
                        }
                    });
        } else {
            Log.v(Config.LOG_TAG, " < 14");
            getContexto().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }



}
