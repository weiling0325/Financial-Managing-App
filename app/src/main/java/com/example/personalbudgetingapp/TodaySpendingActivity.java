package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import org.joda.time.Weeks;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalAmountSpendOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef;
    private DatabaseReference personalRef;

    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDatalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Spending");

        totalAmountSpendOn = findViewById(R.id.totalAmountSpendOn);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this);

//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app");
//        databaseReference = firebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("personal").child(onlineUserId);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDatalist = new ArrayList<>();
        todayItemsAdapter = new TodayItemsAdapter(TodaySpendingActivity.this, myDatalist);
        recyclerView.setAdapter(todayItemsAdapter);

        readItems();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpendOn();
            }
        });
    }

    private void readItems() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        totalAmountSpendOn.setText("Total Daily Spending: $");
        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDatalist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    myDatalist.add(data);
                }

                todayItemsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalAmountSpendOn.setText("Total Daily Spending: $" + totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpendOn() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final TextView item = myView.findViewById(R.id.item);
        item.setText("Select an expense item");
        final Spinner itemSpinner = myView.findViewById(R.id.itemsspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText note = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        note.setVisibility(View.VISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Amount = amount.getText().toString();
                TextView checkSpendingItem = (TextView) itemSpinner.getSelectedView();
                String Item = itemSpinner.getSelectedItem().toString();
                String notes = note.getText().toString();

                personalRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        float itemPercent;
                        float itemBudgetTotal;
                        float itemSpendingTotal;
                        switch (Item) {
                            case "Transport":
                                if (snapshot.hasChild("dayTrans")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayTransRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case "Food":
                                if (snapshot.hasChild("dayFood")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayFoodRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "House":
                                if (snapshot.hasChild("dayHouse")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayHouseRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Entertainment":
                                if (snapshot.hasChild("dayEnt")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayEntRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayEntRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Education":
                                if (snapshot.hasChild("dayEdu")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayEduRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayEduRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Charity":
                                if (snapshot.hasChild("dayCharity")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayCharityRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayCharityRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Apparel":
                                if (snapshot.hasChild("dayApparel")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayApparelRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayApparelRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Health":
                                if (snapshot.hasChild("dayHealth")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayHealthRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Personal":
                                if (snapshot.hasChild("dayPersonal")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayPersonalRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayPersonalRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Other":
                                if (snapshot.hasChild("dayOther")) {
                                    itemSpendingTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                                } else {
                                    itemSpendingTotal = 0;
                                }
                                if (snapshot.hasChild("dayOtherRatio")) {
                                    itemBudgetTotal = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                                } else {
                                    itemBudgetTotal = 0;
                                }
                                itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                                if (itemPercent >=80) {
                                    Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + " of your daily budget!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        /*if (Item.equals("Transport")) {
                            if (snapshot.hasChild("dayTrans")) {
                                itemSpendingTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                            } else {
                                itemSpendingTotal = 0;
                            }
                            if (snapshot.hasChild("dayTransRatio")) {
                                itemBudgetTotal = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                            } else {
                                itemBudgetTotal = 0;
                            }
                            itemPercent = (itemSpendingTotal/itemBudgetTotal)*100;
                            if (itemPercent >=80) {
                                Toast.makeText(TodaySpendingActivity.this, "You have used up " + itemPercent + "% of your daily budget!", Toast.LENGTH_SHORT).show();
                            }
                        }*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(TextUtils.isEmpty(Amount)) {
                    amount.setError("Amount is required!");
                    return;
                }

                if(Item.equals("Select item")) {
                    /*Toast.makeText(TodaySpendingActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();*/
                    checkSpendingItem.setTextColor(Color.RED);
                    checkSpendingItem.setError("Select a valid item");
                    return;
                }

                if (TextUtils.isEmpty(notes)) {
                    note.setError("Note is required");
                    return;
                } else {
                    loader.setMessage("adding an expense item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expensesRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    /*HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("item", Item);
                    hashMap.put("date", date);
                    hashMap.put("id", id);
                    hashMap.put("notes", notes);
                    hashMap.put("amount", Integer.parseInt(Amount));
                    hashMap.put("week", weeks.getWeeks());
                    hashMap.put("month", months.getMonths());*/

                    String itemNday = Item + date;
                    String itemNweek = Item + weeks.getWeeks();
                    String itemNmonth = Item + months.getMonths();

                    Data data = new Data(Item, date, id, itemNday, itemNweek, itemNmonth, Integer.parseInt(Amount), weeks.getWeeks(), months.getMonths(), notes);
                    //expensesRef.child(id).updateChildren(hashMap).addOnCompleteListener
                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TodaySpendingActivity.this, "Expense item added successfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TodaySpendingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}