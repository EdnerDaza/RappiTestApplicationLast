package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.activities;

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


/**
 * Created by administrador on 15/01/17.
 */
public class DetailActivity extends AppCompatActivity {

    private Children mChildren;

    ImageView mImageViewItemDetail, mHeaderViewItemDetail;
    TextView mTextViewTitle, mTextViewItemTitle, mTextViewItemSummary;

    Context mContext;

    //region LIFECYCLE METHODS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(Config.LOG_TAG, "/ ON CREATE / "+mChildren);
        mContext = this;
        Helpers.setContexto(mContext);
        Helpers.setActivity(this);
        setContentView(R.layout.activity_detail);
        Intent intent = this.getIntent();
        mChildren= (Children)intent.getSerializableExtra("children");

        String kind = mChildren.getKind();
        Data data = mChildren.getData();

        setTitle(data.getTitle());

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
    }

    //endregion

}
