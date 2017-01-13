package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.helpers.Helpers;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Children;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Data;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Children mChildren;
    public static String WORK_ID = "WORK_ID";

    ImageView mImageViewItemDetail, mHeaderViewItemDetail;
    TextView mTextViewTitle, mTextViewItemTitle, mTextViewItemSummary;

    Context mContext;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(Config.LOG_TAG, "/ ON CREATE / "+mChildren);
        setContentView(R.layout.activity_detail);
        Intent intent = this.getIntent();
        mChildren= (Children)intent.getSerializableExtra("children");

        String kind = mChildren.getKind();
        Data data = mChildren.getData();

        setTitle(data.getTitle());
        mContext = this;

        mTextViewTitle = (TextView) findViewById(R.id.textview_details);
        mImageViewItemDetail = (ImageView)findViewById(R.id.iv_image_url_detail);
        if(data.banner_size != null) {
            mImageViewItemDetail.setMinimumHeight(data.banner_size.get(1));
            mImageViewItemDetail.setMaxHeight(data.banner_size.get(1));
        }
        mHeaderViewItemDetail = (ImageView)findViewById(R.id.iv_header_url_detail);
        if(data.banner_size != null) {
            mHeaderViewItemDetail.setMinimumHeight(data.banner_size.get(1));
            mHeaderViewItemDetail.setMaxHeight(data.banner_size.get(1));
        }
        mTextViewItemTitle = (TextView) findViewById(R.id.tv_title_detail);
        mTextViewItemSummary = (TextView) findViewById(R.id.tv_summary_detail);
        if(!kind.equals("")){
            mTextViewTitle.setText(kind.trim());
        }
        mTextViewItemTitle.setText(data.getTitle());
        mTextViewItemSummary.setText(data.getDescription());
        if(!data.getBanner_img().equals("")) {
            Picasso.with(mContext).load(data.getBanner_img())
                    .centerInside()
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .into(mImageViewItemDetail);
        }

        if(data.getHeader_img()!= null) {
            Picasso.with(mContext).load(data.getHeader_img())
                    .centerInside()
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .into(mHeaderViewItemDetail);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(Config.LOG_TAG, "/ ON RESUME / "+this);
        Helpers.setActivity(this);
    }

    // METODOS PRIVADOS

    /**
     * METODO QUE REVISA SI HAY CONEXION A INTERNET Y SI NO DEVUELVE UN DIALOG
     */
    private void testConectionInternet() {
        if(Helpers.isNetworkAvailable(this)){
            //Toast.makeText(getApplicationContext(), "con conexion", Toast.LENGTH_SHORT).show();
            Log.v(Config.LOG_TAG, "<< CON CONEXION >> "+Helpers.isNetworkAvailable(this));
        }else{
            //Toast.makeText(getApplicationContext(), "SIN CONEXION", Toast.LENGTH_SHORT).show();
            Log.v(Config.LOG_TAG, "<< SIN CONEXION >> "+Helpers.isNetworkAvailable(this));
            //MUESTRA UN DIALOG
            Helpers.customDialogConnection(String.format(getResources().getString(R.string.error_conexion),
                    getResources().getString(R.string.app_name))).show();

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
                .show(DetailActivity.this, "", getResources().getString(R.string.wait_loading), true, false);
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

}
