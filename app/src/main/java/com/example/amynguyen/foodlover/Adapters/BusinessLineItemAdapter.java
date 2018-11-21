package com.example.amynguyen.foodlover.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


import java.util.ArrayList;
import java.util.List;

public class BusinessLineItemAdapter extends BaseAdapter {
    List<Business> businessInfoList;
    Context context;
    LayoutInflater layoutInflater;
    private DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private static LayoutInflater inflater=null;
    public BusinessLineItemAdapter(List<Business> businessInfo, Context context)   {
         businessInfoList = businessInfo;
         layoutInflater = LayoutInflater.from(context);

/*        for(Business bus : businessInfoList) {
            System.out.println("result #" + bus.getName());
        }*/
        this.context = context;


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_icon)
                //.showImageForEmptyUri(R.drawable.ic_empty)
                // .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        // imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    public void addListItemToAdapter(List<Business> list)  {
        // System.out.println("Thang dau tien:" + list.get(0).getName());
        businessInfoList.addAll(list);
        // System.out.println("Thang 21: " + businessInfoList.get(20).getName());

        // this.notifyDataSetChanged();
        // for(Business bus : list) {
        //    businessInfoList.add(bus);
        // }
        // notifyDataSetChanged();
        //this.notifyDataSetInvalidated();

    }

    @Override
    public int getCount() {
        return businessInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return businessInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.layout_items, parent, false);
            holder.img = view.findViewById(R.id.imgRestaurant);
            holder.txtName = view.findViewById(R.id.txtName);
            holder.txtReview = view.findViewById(R.id.ratingBarReview);
            holder.txtCategory = view.findViewById(R.id.txtCategory);
            holder.txtAddress = view.findViewById(R.id.txtAddress);
            holder.txtDistance = view.findViewById(R.id.txtDistance);
            holder.isClosed = view.findViewById(R.id.imgViewOpenClose);
            view.setTag(holder);
        }else {
                holder = (ViewHolder) view.getTag();
        }
        holder.txtName.setText(businessInfoList.get(i).getName());
        holder.txtReview.setEnabled(false);
        holder.txtReview.setMax(5);
        holder.txtReview.setStepSize(0.01f);
        holder.txtReview.setRating(Float.parseFloat(String.valueOf(businessInfoList.get(i).getRating())));
        holder.txtReview.invalidate();
        holder.txtCategory.setText(businessInfoList.get(i).getCategory());
        holder.txtAddress.setText(businessInfoList.get(i).getAddress());
        String distance = businessInfoList.get(i).getDistanceFromCurrentLocation();
        if(distance != null) holder.txtDistance.setText(businessInfoList.get(i).getDistanceFromCurrentLocation());
        ImageLoader.getInstance().displayImage(businessInfoList.get(i).getImgURL(), holder.img,options);
        return view;

    }
    static class ViewHolder {
        ImageView img;
        TextView txtName;
        RatingBar txtReview;
        TextView txtCategory;
        TextView txtAddress;
        TextView txtDistance;
        ImageView isClosed;

    }

}
