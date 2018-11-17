package com.example.amynguyen.foodlover.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.amynguyen.foodlover.Models.Business;
import com.example.amynguyen.foodlover.R;

import java.util.ArrayList;
import java.util.List;

public class BusinessLineItemAdapter extends BaseAdapter {
    List<Business> businessInfoList;
    Context context;

    public BusinessLineItemAdapter(List<Business> businessInfo, Context context)   {
         businessInfoList = businessInfo;
/*        for(Business bus : businessInfoList) {
            System.out.println("result #" + bus.getName());
        }*/
/*        businessInfoList = new ArrayList<>();
        businessInfoList.add(new Business("asdasd", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd2", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd3", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd4", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd5", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd2", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd3", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd4", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd5", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd2", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd3", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd4", "asdasd", "asdsad", 3.5, "http://google.com"));
        businessInfoList.add(new Business("asdasd5", "asdasd", "asdsad", 3.5, "http://google.com"));*/
        this.context = context;
    }

    public void addListItemToAdapter(List<Business> list)  {
        // System.out.println("Thang dau tien:" + list.get(0).getName());
        businessInfoList.addAll(list);
        // System.out.println("Thang 21: " + businessInfoList.get(20).getName());

        // this.notifyDataSetChanged();
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
        if(view == null) {
            System.out.println("Thang so " + i + " ten la :"  + businessInfoList.get(i).getName()) ;
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.layout_items, parent, false);

            ImageView img = (ImageView) view.findViewById(R.id.imgRestaurant);
            TextView txtName = (TextView) view.findViewById(R.id.txtName);
            RatingBar txtReview = (RatingBar) view.findViewById(R.id.ratingBarReview);
            TextView txtCategory = (TextView) view.findViewById(R.id.txtCategory);
            TextView txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);
            txtName.setText(businessInfoList.get(i).getName());
            //txtReview.setText(String.valueOf(businessInfoList.get(i).getRating()));
            txtReview.setEnabled(false);
            txtReview.setMax(5);
            txtReview.setStepSize(0.01f);
            txtReview.setRating(Float.parseFloat(String.valueOf(businessInfoList.get(i).getRating())));
            txtReview.invalidate();
            txtCategory.setText(businessInfoList.get(i).getCategory());
            txtAddress.setText(businessInfoList.get(i).getAddress());
            String distance = businessInfoList.get(i).getDistanceFromCurrentLocation();
            if(distance != null) txtDistance.setText(businessInfoList.get(i).getDistanceFromCurrentLocation());
            new DownloadImageTask(img).execute(businessInfoList.get(i).getImgURL());
        }

        return view;

    }
    public synchronized void refresAdapter(ArrayList<Business> busisnesses) {
        businessInfoList.clear();
        businessInfoList.addAll(busisnesses);
        // System.out.println(businessInfoList.get(0).getName());
        notifyDataSetChanged();
    }
}
