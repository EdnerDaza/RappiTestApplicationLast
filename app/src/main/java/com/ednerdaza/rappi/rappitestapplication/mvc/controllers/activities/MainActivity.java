package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.helpers.Helpers;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.adapters.AdapterItems;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.base.VolleyQueue;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces.DelegateItemAdapter;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces.ItemModelInterface;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.ItemModel;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Children;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntityResponse;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Response_data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by administrador on 8/01/17.
 */
public class MainActivity extends AppCompatActivity implements DelegateItemAdapter, View.OnClickListener {

    private RecyclerView mRecyclerView ;
    private LinearLayoutManager mLinearLayoutManager;
    private AdapterItems mAdapterItems;
    private Context mContext;
    private ArrayList<Children> mItemsEntity = new ArrayList<Children>();
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mCoordinatorLayout;
    private ItemModel mItemModel;
    private Response_data mResponseData;
    private String mModHash;
    private ArrayList<Children> mChildrens;
    private String mAfter;
    private String mBefore;
    private TextView mTextviewTitleRoot, mTextviewModhash;
    private ImageButton mImageButtonBefore, mImageButtonAfter;
    private boolean mIsAppOnline = true;

    DownloadManager mDownloadManager;
    String downloadFileUrl = "http://www.101apps.co.za/" +
            "images/headers/101_logo_very_small.jpg";
    private long myDownloadReference;
    private BroadcastReceiver receiverDownloadComplete;
    private BroadcastReceiver receiverNotificationClicked;

    private String TAG = "download";
    private String mSavedFilePathJSON = "";
    private String mSavedFilePathJSONURI = "";

    //region LIFECYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(Config.LOG_TAG, "/ ON CREATE / "+this);

        mContext = this;
        Helpers.setContexto(mContext);
        Helpers.setActivity(this);
        Helpers.haveStoragePermission();
        //Se crea la cola de peticiones
        VolleyQueue.createQueue(getApplicationContext());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_root);
        mRecyclerView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mTextviewTitleRoot = (TextView) findViewById(R.id.textview_root);
        mTextviewModhash = (TextView) findViewById(R.id.textview_modhash);

        mImageButtonBefore = (ImageButton) findViewById(R.id.imagebutton_before);
        mImageButtonAfter = (ImageButton) findViewById(R.id.imagebutton_after);
        mImageButtonBefore.setOnClickListener(this);
        mImageButtonAfter.setOnClickListener(this);

        mAdapterItems = new AdapterItems(MainActivity.this, mItemsEntity);
        mRecyclerView.setAdapter(mAdapterItems);

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Helpers.setDownloadManager(mDownloadManager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Config.LOG_TAG, "/ ON RESUME / "+this);
        if(Helpers.testConectionInternet(mContext))
        {
            if(mIsAppOnline) {
                Log.v(Config.LOG_TAG, "/ syncItems / " + this);
                syncItems();
            }else{
            // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
            Helpers.customDialogMessage(getResources().getString(R.string.offline_dialog)).show();
            }
        }else{
            syncItems(mIsAppOnline);
        }

        // filter for notifications - only acts on notification while download busy
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);

        receiverNotificationClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraId);
                for (long reference : references) {
                    if (reference == Helpers.getMyDownloadReference()) {
                        // do something with the download file
                    }
                }
            }
        };
        registerReceiver(receiverNotificationClicked, filter);

        // filter for download - on completion
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (Helpers.getMyDownloadReference() == reference) {
                    // do something with the download file
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = Helpers.getDownloadManager().query(query);
                    cursor.moveToFirst();
                    // get the status of the download
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);

                    int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                    String savedFilePath = cursor.getString(fileNameIndex);
                    mSavedFilePathJSON = savedFilePath;

                    // get the reason - more detail on the status
                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);

                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            String uriString = cursor.getString(
                                    cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            mSavedFilePathJSONURI = uriString;
                            // start activity to display the downloaded image
                            Toast.makeText(MainActivity.this, "SUCCESS DOWNLOAD: " + reason + " "
                                    +mSavedFilePathJSONURI , Toast.LENGTH_LONG).show();
                            syncItems(mSavedFilePathJSON);
                            break;
                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(MainActivity.this, "FAILED: " + reason, Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            Toast.makeText(MainActivity.this, "PAUSED: " + reason, Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_PENDING:
                            Toast.makeText(MainActivity.this, "PENDING!", Toast.LENGTH_LONG).show();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            Toast.makeText(MainActivity.this, "RUNNING!", Toast.LENGTH_LONG).show();
                            break;
                    }
                    cursor.close();
                }
            }
        };
        registerReceiver(receiverDownloadComplete, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(Config.LOG_TAG, "/ ON PAUSE / "+this);
        unregisterReceiver(receiverDownloadComplete);
        unregisterReceiver(receiverNotificationClicked);
    }

    //endregion

    //region MENU METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.v(Config.LOG_TAG, "/ ON onCreateOptionsMenu / "+this);
        //mIsAppOnline = true;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(Config.LOG_TAG, "/ ON onOptionsItemSelected / "+this);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_ilike, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if(Helpers.testConectionInternet(mContext))
            {
                if(mIsAppOnline) {
                    Log.v(Config.LOG_TAG, "/ syncItems / " + this);
                    syncItems();
                }else{
                    // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
                    Helpers.customDialogMessage(getResources().getString(R.string.offline_dialog)).show();
                }
            };
            return true;
        }

        if (id == R.id.action_online_mode) {
            if(mIsAppOnline){
                Toast.makeText(MainActivity.this, "MODO ONLINE DESACTIVADO", Toast.LENGTH_LONG).show();
                item.setIcon(R.drawable.ic_cloud_off_red_700_48dp);
                mIsAppOnline = false;
                Helpers.customDialogDownload(getResources().getString(R.string.download_dialog)).show();
                useAssetsJSON();
            }else{
                item.setIcon(R.drawable.ic_cloud_queue_green_a700_48dp);
                mIsAppOnline = true;
                useOnlineJSON();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void useAssetsJSON() {
        if(!mIsAppOnline) {
            Log.v(Config.LOG_TAG, "/ syncItems / " + this);
            syncItems(mIsAppOnline);
        }else{
            // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
            Helpers.customDialogMessage(getResources().getString(R.string.offline_dialog)).show();
        }
    }



    //endregion

    //region PRIVATE METHODS

    private void useDownloadJSON() {
        Log.v(Config.LOG_TAG, "useDownloadJSON --> ");
        Uri uri = Uri.parse(Config.BASE_URL_JSON);
        DownloadManager.Request request = new DownloadManager.Request(uri);

//                set the notification
        request.setDescription("JSON Reddit")
                .setTitle("Descargando JSON");

//                set the path to where to save the file
//                      save in app package directory
        request.setDestinationInExternalFilesDir(MainActivity.this,
                Environment.DIRECTORY_DOWNLOADS, "reddits.json");
//                      save in the public downloads folder
//                request.setDestinationInExternalPublicDir(Environment.
//                              DIRECTORY_DOWNLOADS, "MyWebsiteLogo.png");

//                make file visible by and manageable by system's download app
        request.setVisibleInDownloadsUi(true);

//                select which network, etc
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE);

//                queue the download
        myDownloadReference = mDownloadManager.enqueue(request);

    }

    private void useOnlineJSON() {
        Log.v(Config.LOG_TAG, "useOnlineJSON --> ");
        mSavedFilePathJSON = "";
        mSavedFilePathJSONURI ="";
        Toast.makeText(MainActivity.this, "MODO ONLINE ACTIVADO", Toast.LENGTH_LONG).show();
        if(Helpers.testConectionInternet(mContext))
        {
            if(mIsAppOnline) {
                Log.v(Config.LOG_TAG, "/ syncItems / " + this);
                syncItems();
            }else{
                // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
                Helpers.customDialogMessage(getResources().getString(R.string.offline_dialog)).show();
            }
        };
    }



    /**
     * METODO QUE DIBUJA VALORES PARA EL RECYCLEVIEW CUANDO ESTE LEE DEL SERVICIO
     * @param childrens
     */
    private void drawPrizes(ArrayList<Children> childrens) {
        Log.v(Config.LOG_TAG, "HICE CLICK EN --> " + childrens);
        mAdapterItems = new AdapterItems(MainActivity.this, childrens);
        mAdapterItems.setDelegate(this);
        mRecyclerView.setAdapter(mAdapterItems);
    }


    /**
     * METODO QUE LEE UN SERVICIO Y CARGA LAS OBRAS QUE VIENEN DESDE ESTE
     */
    private void syncItems() {

        // MUESTRO UN CARGANDO
        progressDialogLoadingShow();

        mItemModel.getItems(mContext, new ItemModelInterface<ItemEntityResponse>() {

            @Override
            public void completeSuccess(ItemEntityResponse entity) {
                Log.v(Config.LOG_TAG, "EXITO SINCRONIZACION >>> " + entity);
                if(!entity.getKind().equals("")){
                    mTextviewTitleRoot.setText(entity.getKind().trim());
                }else{
                    mTextviewTitleRoot.setText(getResources().getString(R.string.title));
                }

                if(entity.getData() != null){
                    mResponseData = entity.getData();
                    responseDataItemsView();
                }

                // CIERRO EL CARGANDO
                progressDialogClose();

            }

            @Override
            public void completeFail(String message) {
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                Log.v(Config.LOG_TAG, "FALLO SINCRONIZACION >>> " + message);
                // CIERRO EL CARGANDO
                progressDialogClose();
                // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
                Helpers.customDialogMessage(message).show();

            }
        });

    }

    private void syncItems(String mSavedFilePathJSONURI) {

        // MUESTRO UN CARGANDO
        progressDialogLoadingShow();

        mItemModel.getItemsDownload(mContext, mSavedFilePathJSONURI,
                new ItemModelInterface<ItemEntityResponse>() {
            @Override
            public void completeSuccess(ItemEntityResponse entity) {
                Log.v(Config.LOG_TAG, "EXITO SINCRONIZACION >>> " + entity);
                if(!entity.getKind().equals("")){
                    mTextviewTitleRoot.setText(entity.getKind().trim());
                }else{
                    mTextviewTitleRoot.setText(getResources().getString(R.string.title));
                }

                if(entity.getData() != null){
                    mResponseData = entity.getData();
                    responseDataItemsView();
                }

                // CIERRO EL CARGANDO
                progressDialogClose();
            }

            @Override
            public void completeFail(String message) {
                Log.v(Config.LOG_TAG, "FALLO SINCRONIZACION >>> " + message);
                // CIERRO EL CARGANDO
                progressDialogClose();
                // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
                Helpers.customDialogMessage(message).show();
            }
        });

    }

    private void syncItems(boolean mIsAppOnline) {
        // MUESTRO UN CARGANDO
        progressDialogLoadingShow();

        mItemModel.getItemsAssets(mContext, new ItemModelInterface<ItemEntityResponse>() {
            @Override
            public void completeSuccess(ItemEntityResponse entity) {
                Log.v(Config.LOG_TAG, "EXITO SINCRONIZACION >>> " + entity);
                if(!entity.getKind().equals("")){
                    mTextviewTitleRoot.setText(entity.getKind().trim());
                }else{
                    mTextviewTitleRoot.setText(getResources().getString(R.string.title));
                }

                if(entity.getData() != null){
                    mResponseData = entity.getData();
                    responseDataItemsView();
                }

                // CIERRO EL CARGANDO
                progressDialogClose();
            }

            @Override
            public void completeFail(String message) {
                Log.v(Config.LOG_TAG, "FALLO SINCRONIZACION >>> " + message);
                // CIERRO EL CARGANDO
                progressDialogClose();
                // ABRIMOS UN DIALOG CON EL MENSAJE QUE VIENE DEL SERVICIO
                Helpers.customDialogMessage(message).show();
            }
        });
    }

    private void responseDataItemsView() {
        if(mResponseData != null){
            mModHash = mResponseData.getModhash();
            if(!mModHash.equals("")){
                mTextviewModhash.setText(mModHash.trim());
            }else{
                mTextviewModhash.setText(getResources().getString(R.string.modhash));
            }
            mChildrens = mResponseData.getChildren();
            if(mChildrens.size() > 0){
                drawPrizes(mChildrens);
            }else{
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
            }
            mBefore = mResponseData.getBefore();
            if(mBefore != null){
                mImageButtonBefore.setEnabled(true);
                mImageButtonBefore.setImageResource(R.drawable.ic_chevron_left_white_48dp);
            }else{
                mImageButtonBefore.setEnabled(false);
                mImageButtonBefore.setImageResource(R.drawable.ic_chevron_left_grey_50_48dp);
            }
            mAfter = mResponseData.getAfter();
            if(mAfter != null){
                mImageButtonAfter.setEnabled(true);
                mImageButtonAfter.setImageResource(R.drawable.ic_chevron_right_white_48dp);
            }else{
                mImageButtonAfter.setEnabled(false);
                mImageButtonAfter.setImageResource(R.drawable.ic_chevron_right_grey_50_48dp);
            }



        }
    }

    /**
     * METODO QUE MUESTRA UN LOADING
     */
    private void progressDialogLoadingShow(){
        // SI EL LOADING ES DIFERENTE DE NULO, LO CERRAMOS
        if (mProgressDialog != null){
            progressDialogClose();
        }
        // Y CREAMOS UNO NUEVO
        mProgressDialog = Helpers.customProgressDialog()
                .show(MainActivity.this, "", getResources().getString(R.string.wait_loading), true, false);
    }

    /**
     * METODO QUE OCULTA UN LOADING
     */
    private void progressDialogClose() {
        // SI EL LOADING ES DIFERENTE DE NULO Y SE ESTA MOSTRANDO LO CERRAMOS
        if ((mProgressDialog != null) && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        // LO CONVERTIMOS EN NULO
        mProgressDialog = null;
    }

    //endregion

    //region VIEW.ONCLICKLISTENER METHODS

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.imagebutton_before:
                Toast.makeText(getApplicationContext(), mBefore.trim(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.imagebutton_after:
                Toast.makeText(getApplicationContext(), mAfter.trim(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //endregion

    //region DELEGATEITEMADAPTER METHODS

    @Override
    public void onItemClicked(Children entity) {
        Log.v(Config.LOG_TAG, "HICE CLICK EN --> " + entity);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("children", (Serializable) entity);
        startActivity(intent);
    }

    //endregion

}
