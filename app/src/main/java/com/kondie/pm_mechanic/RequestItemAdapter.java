package com.kondie.pm_mechanic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemHolder> implements OnEndOfListListener {

    Activity activity;
    List<RequestItem> requestItems;
    LayoutInflater inflater;
    private static boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private OnEndOfListListener onEndOfListListener;
    SharedPreferences prefs;

    public RequestItemAdapter(Activity activity, final List<RequestItem> requestItems, RecyclerView requestList)
    {
        this.activity = activity;
        this.requestItems = requestItems;
        inflater = LayoutInflater.from(activity);
        prefs = activity.getSharedPreferences("PM_M", Context.MODE_PRIVATE);
        setHasStableIds(true);

        final LinearLayoutManager linearLayMan = (LinearLayoutManager) requestList.getLayoutManager();
        requestList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayMan.getItemCount();
                lastVisibleItem = linearLayMan.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && requestItems.size()%10 == 0) {
                    if (onEndOfListListener != null) {
                        onEndOfListListener.onEndOfList();
                    }
                }
            }
        });
    }

    @Override
    public void onEndOfList() {
        isLoading = true;
    }

    public void setOnEndOfListListener(OnEndOfListListener onEndOfListListener){
        this.onEndOfListListener = onEndOfListListener;
    }

    public static void setLoaded(){
        isLoading = false;
    }

    @NonNull
    @Override
    public RequestItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View menuItemView = inflater.inflate(R.layout.request_item, parent, false);
        RequestItemHolder holder = new RequestItemHolder(menuItemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItemHolder holder, int position) {

        RequestItem item = requestItems.get(position);

        try{
            holder.email.setText(item.getClientEmail());
            holder.phone.setText(item.getClientPhone());
            holder.fname.setText(item.getClientFName());
            holder.lname.setText(item.getClientLName());
            holder.fullName.setText(item.getClientLName() + " " + item.getClientFName());
            holder.lng.setText(String.valueOf(item.getLng()));
            holder.lat.setText(String.valueOf(item.getLat()));
            holder.shopLat.setText(String.valueOf(item.getShopLat()));
            holder.shopLng.setText(String.valueOf(item.getShopLng()));
            holder.requestComment.setText(item.getRequestComment());
            holder.requestId.setText(item.getId());
            holder.imagePath.setText(item.getImagePath());
            holder.requestIssue.setText(item.getIssue() + " Problems");
            holder.serviceFee.setText("Minimum fee: R" + item.getServiceFee());
            holder.makeAndModel.setText(item.getMakeAndModel());
            holder.hStatus.setText(item.getStatus());

            if (item.getStatus().equalsIgnoreCase("waiting")) {
                holder.acceptButt.setText("Accept");
            }
            else if (item.getStatus().equalsIgnoreCase("accept")) {
                holder.acceptButt.setText("Arrived");
            }
            else if (item.getStatus().equalsIgnoreCase("arrived")){
                holder.acceptButt.setText("Done");
            }
            Picasso.with(MainActivity.activity).load(item.getImagePath()).into(holder.clientDp);

        }catch (Exception e){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return requestItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
