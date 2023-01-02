package com.example.personalbudgetingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalbudgetingapp.R;
import com.example.personalbudgetingapp.model.MonthlyAnalyticModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Map;

public class MonthlyAnalyticItemsAdapter extends RecyclerView.Adapter<MonthlyAnalyticItemsAdapter.MyViewHolder> {
    Context context;
    ArrayList<MonthlyAnalyticModel> monthlyAnalyticModels;

    public MonthlyAnalyticItemsAdapter(Context context, ArrayList<MonthlyAnalyticModel> monthlyAnalyticModels) {
        this.context = context;
        this.monthlyAnalyticModels = monthlyAnalyticModels;
    }

    @NonNull
    @Override
    public MonthlyAnalyticItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is the place we inflate the layout (Giving a look to our rows)

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_monthly_analytic_row, parent, false);
        return new MonthlyAnalyticItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyAnalyticItemsAdapter.MyViewHolder holder, int position) {
        //assigning values to the views we created in the monthly_analytic_row layout file
        //based on the position of the recycler view
        holder.AnalyticItemsName.setText(monthlyAnalyticModels.get(position).getAnalyticItem());
        holder.itemMonth.setText(monthlyAnalyticModels.get(position).getAnalyticPeriod());
        holder.imageView.setImageResource(monthlyAnalyticModels.get(position).getImage());
        holder.AnalyticItemAmount.setText("$" + monthlyAnalyticModels.get(position).getAnalyticItemAmount());
    }

    @Override
    public int getItemCount() {
        //the recycler view just wants to know the number of items you want to display
        return monthlyAnalyticModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from our recyclerview layout file

        ImageView imageView;
        TextView AnalyticItemsName, itemMonth;
        TextView AnalyticItemAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.analyticItemsImageView);
            AnalyticItemsName = itemView.findViewById(R.id.analyticItemsName);
            itemMonth = itemView.findViewById(R.id.itemPeriod);
            AnalyticItemAmount = itemView.findViewById(R.id.analyticsItemAmount);

        }
    }
}
