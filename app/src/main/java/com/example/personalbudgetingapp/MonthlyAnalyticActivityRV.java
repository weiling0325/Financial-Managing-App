package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.AnyChartView;
import com.example.personalbudgetingapp.adapter.MonthlyAnalyticItemsAdapter;
import com.example.personalbudgetingapp.model.Data;
import com.example.personalbudgetingapp.model.MonthlyAnalyticModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Objects;

public class MonthlyAnalyticActivityRV extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView totalBudgetAmountTextView, monthSpentAmount;
    private TextView analyticsItemAmount;

    RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private AnyChartView pieChartView;

    ArrayList<MonthlyAnalyticModel> analyticModels = new ArrayList<>();

    int[] analyticModelsImages = {R.drawable.transport, R.drawable.food, R.drawable.house, R.drawable.entertainment,
            R.drawable.education, R.drawable.charity, R.drawable.apparel, R.drawable.health, R.drawable.personal,
            R.drawable.others};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytic_rv);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        monthSpentAmount = findViewById(R.id.monthSpentAmount);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Monthly Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("personal").child(onlineUserId);


        recyclerView = findViewById(R.id.recyclerView);

        setUpMonthlyAnalyticModels();

        MonthlyAnalyticItemsAdapter adapter = new MonthlyAnalyticItemsAdapter(this, analyticModels);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMonthlyAnalyticModels() {
        String[] monthlyAnalyticNames = getResources().getStringArray(R.array.analytic_items);
        String itemTime = getResources().getString(R.string.ThisMonth);
        String[] databaseRef = getResources().getStringArray(R.array.databaseRef);
        ArrayList<Integer> analyticAmountTest = new ArrayList<>();

        for (int i=0; i<analyticAmountTest.size(); i++) {
            getTotalmonthItemExpenses(monthlyAnalyticNames[i], databaseRef[i], analyticAmountTest);
        }

        for (int i=0; i<monthlyAnalyticNames.length; i++) {
            analyticModels.add(new MonthlyAnalyticModel(monthlyAnalyticNames[i], itemTime, analyticModelsImages[i], analyticAmountTest.get(i)));
        }
        //analyticAmounttest.clear();
    }

    private void getTotalmonthItemExpenses(String spendingItem, String databaseRef, ArrayList<Integer> itemAmount) {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = spendingItem + months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNmonth").equalTo(itemNmonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        //analyticsItemAmount.setText("$" + totalAmount);
                    } itemAmount.add(totalAmount);
                    personalRef.child("month" + databaseRef).setValue(totalAmount);
                } else {
                    personalRef.child("month" + databaseRef).setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalmonthSpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child(onlineUserId);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Objects> map = (Map<String, Objects>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    totalBudgetAmountTextView.setText("Total month's spending: $" + totalAmount);
                    monthSpentAmount.setText("Total Spent: $" + totalAmount);
                } else {
                    pieChartView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}