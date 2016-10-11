package com.pelsoczi.vendship.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pelsoczi.vendship.R;
import com.pelsoczi.vendship.VendorActivity;
import com.pelsoczi.vendship.util.Utility;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = Adapter.class.getSimpleName();

    public final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Business> items;
    // cache for quicker response
    private int itemsSize;

    public Adapter(Context context, List<Business> businesses) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        setHasStableIds(true);
        items = businesses;
        itemsSize = items.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = R.layout.list_item_vendor;
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(viewType, parent, false);
        return new VendorViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Business business = items.get(position);
        ((VendorViewHolder)holder).setBusiness(business);
    }

    @Override
    public int getItemCount() {
        return itemsSize;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class VendorViewHolder extends ViewHolder implements View.OnClickListener {

        private final Context context;
        private int position;
        private Business business;
        private TextView name;
        private TextView rating;
        private ImageView ratingImage;
        private TextView snippet;
        private TextView category;
        private ImageView image;

        public VendorViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            position = getAdapterPosition();

            name = (TextView) itemView.findViewById(R.id.vendor_name);
            rating = (TextView) itemView.findViewById(R.id.vendor_rating);
            ratingImage = (ImageView) itemView.findViewById(R.id.vendor_rating_image);
            snippet = (TextView) itemView.findViewById(R.id.vendor_snippet);
            category = (TextView) itemView.findViewById(R.id.vendor_category);
            image = (ImageView) itemView.findViewById(R.id.vendor_image);

            itemView.setOnClickListener(this);
        }

        public void setBusiness(Business business) {
            this.business = business;

            image.setImageBitmap(null);
            Picasso.with(context)
                    .load(business.imageUrl())
                    .into(image);

            ratingImage.setImageBitmap(null);
            Picasso.with(context)
                    .load(business.ratingImgUrlLarge())
                    .into(ratingImage);

            name.setText(business.name());

            if (business.snippetText() != null) {
                snippet.setText(business.snippetText());
            }
            else {
                snippet.setVisibility(View.GONE);
            }

            rating.setText(String.valueOf(business.reviewCount()));

            String categories = Utility.getYelpCategories(business);
            if (categories != null) {
                category.setText(categories);
            }
            else {
                category.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            VendorActivity activity = ((VendorActivity) context);
            activity.showDetails(business, position);
        }
    }
}