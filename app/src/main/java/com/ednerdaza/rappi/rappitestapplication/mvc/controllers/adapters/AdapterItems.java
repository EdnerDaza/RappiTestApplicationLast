package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ednerdaza.rappi.rappitestapplication.R;
import com.ednerdaza.rappi.rappitestapplication.classes.utilities.Config;
import com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces.DelegateItemAdapter;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Children;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Data;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by administrador on 15/01/17.
 */
public class AdapterItems extends RecyclerView.Adapter<AdapterItems.ItemsViewHolder> {

    // delegate para que reciba el onclick desde fuera del adapter
    private DelegateItemAdapter mDelegateItemAdapter;
    public DelegateItemAdapter getDelegate() {
        return mDelegateItemAdapter;
    }
    public void setDelegate(DelegateItemAdapter mDelegateItemAdapter) {
        this.mDelegateItemAdapter = mDelegateItemAdapter;
    }

    ArrayList<Children> items;
    Activity context;

    public AdapterItems(Activity context, ArrayList<Children> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main, parent, false);
        ItemsViewHolder itemsViewHolder = new ItemsViewHolder(v);
        return itemsViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemsViewHolder holder, final int position) {

        Log.v(Config.LOG_TAG, " ITEM --> " + items.get(position));
        Log.v(Config.LOG_TAG, " TIPO --> " + items.get(position).getKind());
        Log.v(Config.LOG_TAG, " DATOS --> " + items.get(position).getData());

        String kind = items.get(position).getKind();
        Data data = items.get(position).getData();


        holder.tvTitle.setText(data.getTitle());
        holder.tvSummary.setText(data.getPublic_description());
        if(!data.getIcon_img().equals("")) {
            Picasso.with(context).load(data.getIcon_img())
                    .centerInside()
                    .fit()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.redditlogo)
                    .error(R.drawable.errorimage)
                    .into(holder.ivImageUrl);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(Config.LOG_TAG, "// setOnClickListener onClick( view : "+view+" ) //"+
                        "\nMETODO QUE OBTIENE EL EVENTO CLICK DE LA VISTA\n"+this);

                if(mDelegateItemAdapter!=null){
                    mDelegateItemAdapter.onItemClicked(items.get(position));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        //return 0;
        return items.size();
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {
        CardView cvRoot;
        TextView tvTitle;
        TextView tvSummary;
        ImageView ivImageUrl;

        ItemsViewHolder(View itemView) {
            super(itemView);
            cvRoot = (CardView)itemView.findViewById(R.id.cv_layout);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tvSummary = (TextView)itemView.findViewById(R.id.tv_summary);
            ivImageUrl = (ImageView)itemView.findViewById(R.id.iv_image_url);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
