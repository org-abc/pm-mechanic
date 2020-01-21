package com.kondie.pm_mechanic;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryHolder extends RecyclerView.ViewHolder {

    TextView clientName, date, status, serviceFee, issue, car;
    public HistoryHolder(@NonNull View itemView) {
        super(itemView);

        clientName = itemView.findViewById(R.id.req_client);
        date = itemView.findViewById(R.id.req_date);
        car = itemView.findViewById(R.id.req_car);
        issue = itemView.findViewById(R.id.req_issue);
        status = itemView.findViewById(R.id.req_status);
        serviceFee = itemView.findViewById(R.id.history_service_fee);
    }
}
