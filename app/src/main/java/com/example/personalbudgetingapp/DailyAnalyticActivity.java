package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DailyAnalyticActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount, analyticsFoodAmount,
            analyticsHouseAmount, analyticsEntertainmentAmount, analyticsEducationAmount,
            analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount,
            analyticsPersonalAmount, analyticsOtherAmount;

    private RelativeLayout linearLayoutTransport, linearLayoutFood, linearLayoutHouse,
            linearLayoutEntertainment, linearLayoutEducation, linearLayoutCharity,
            linearLayoutApparel, linearLayoutHealth, linearLayoutPersonal,
            linearLayoutOther, linearLayoutAnalysis;

    private AnyChartView pieChartView;

    private TextView progress_ratio_transport, progress_ratio_food, progress_ratio_house,
            progress_ratio_entertainment, progress_ratio_education, progress_ratio_charity,
            progress_ratio_apparel, progress_ratio_health, progress_ratio_personal,
            progress_ratio_other, monthRatioSpending, monthSpentAmount;

    private ImageView status_image_transport, status_image_food, status_image_house,
            status_image_entertainment, status_image_education, status_image_charity,
            status_image_apparel, status_image_health, status_image_personal,
            status_image_other, monthRatioSpending_Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytic);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Daily Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("personal").child(onlineUserId);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);

        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);

        //general analytic
        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseAmount = findViewById(R.id.analyticsHouseAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalAmount = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //Relative layouts view
        linearLayoutTransport = findViewById(R.id.linearLayoutTransport);
        linearLayoutFood = findViewById(R.id.linearLayoutFood);
        linearLayoutHouse = findViewById(R.id.linearLayoutHouse);
        linearLayoutEntertainment = findViewById(R.id.linearLayoutEntertainment);
        linearLayoutEducation = findViewById(R.id.linearLayoutEducation);
        linearLayoutCharity = findViewById(R.id.linearLayoutCharity);
        linearLayoutApparel = findViewById(R.id.linearLayoutApparel);
        linearLayoutHealth = findViewById(R.id.linearLayoutHealth);
        linearLayoutPersonal = findViewById(R.id.linearLayoutPersonal);
        linearLayoutOther = findViewById(R.id.linearLayoutOther);

        //TextView
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_entertainment = findViewById(R.id.progress_ratio_entertainment);
        progress_ratio_education = findViewById(R.id.progress_ratio_education);
        progress_ratio_charity = findViewById(R.id.progress_ratio_charity);
        progress_ratio_apparel = findViewById(R.id.progress_ratio_apparel);
        progress_ratio_health = findViewById(R.id.progress_ratio_health);
        progress_ratio_personal = findViewById(R.id.progress_ratio_personal);
        progress_ratio_other = findViewById(R.id.progress_ratio_other);

        //ImageView
        status_image_transport = findViewById(R.id.status_image_transport);
        status_image_food = findViewById(R.id.status_image_food);
        status_image_house = findViewById(R.id.status_image_house);
        status_image_entertainment = findViewById(R.id.status_image_entertainment);
        status_image_education = findViewById(R.id.status_image_education);
        status_image_charity = findViewById(R.id.status_image_charity);
        status_image_apparel = findViewById(R.id.status_image_apparel);
        status_image_health = findViewById(R.id.status_image_health);
        status_image_personal = findViewById(R.id.status_image_personal);
        status_image_other = findViewById(R.id.status_image_other);

        //AnyChart
        pieChartView = findViewById(R.id.pieChartView);

        /*getTotalweekTransportExpenses();
        getTotalweekFoodExpenses();
        getTotalweekHouseExpenses();
        getTotalweekEntertainmentExpenses();
        getTotalweekEducationExpenses();
        getTotalweekCharityExpenses();
        getTotalweekApparelExpenses();
        getTotalweekHealthExpenses();
        getTotalweekPersonalExpenses();
        getTotalweekOtherExpenses();*/
        getTotalDailyItemExpenses("Transport", "Trans");
        getTotalDailyItemExpenses("Food", "Food");
        getTotalDailyItemExpenses("House", "House");
        getTotalDailyItemExpenses("Entertainment", "Ent");
        getTotalDailyItemExpenses("Education", "Edu");
        getTotalDailyItemExpenses("Charity", "Charity");
        getTotalDailyItemExpenses("Apparel", "Apparel");
        getTotalDailyItemExpenses("Health", "Health");
        getTotalDailyItemExpenses("Personal", "Personal");
        getTotalDailyItemExpenses("Other", "Other");
        getTotalDaySpending();

        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResource();
            }
        }, 2000);
    }

    private void getTotalDailyItemExpenses(String dailySpendingItem, String databaseRef) {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = dailySpendingItem + date;

//        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsTransportAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("day" + databaseRef).setValue(totalAmount);
                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalDaySpending() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
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
                    totalBudgetAmountTextView.setText("Total day's spending: $" + totalAmount);
                    monthSpentAmount.setText("Total Spent: $" + totalAmount);
                } else {
                    totalBudgetAmountTextView.setText("Good job, you haven't spend today.");
                    pieChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    int houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    int entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    int eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    int chaTotal;
                    if (snapshot.hasChild("dayCharity")) {
                        chaTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    int appTotal;
                    if (snapshot.hasChild("dayApparel")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    int heaTotal;
                    if (snapshot.hasChild("dayHealth")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    int perTotal;
                    if (snapshot.hasChild("dayPersonal")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    int othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    Pie pie = AnyChart.pie();

                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("House expenses", houseTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", chaTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Health", heaTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("Other", othTotal));

                    pie.data(data);
                    pie.title("Daily Analysis");
                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Daily Expenses").padding(0d, 0d, 10d, 0d);
                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);

                    pieChartView.setChart(pie);

/*                    List<PieEntry> data = new ArrayList<>();
                    data.add(new PieEntry(traTotal, "Transport"));
                    data.add(new PieEntry(houseTotal, "House exp"));
                    data.add(new PieEntry(foodTotal, "Food"));
                    data.add(new PieEntry(entTotal, "Entertainment"));
                    data.add(new PieEntry(eduTotal, "Education"));
                    data.add(new PieEntry(chaTotal, "Charity"));
                    data.add(new PieEntry(appTotal, "Apparel"));
                    data.add(new PieEntry(heaTotal, "Health"));
                    data.add(new PieEntry(perTotal, "Personal"));
                    data.add(new PieEntry(othTotal, "Other"));

                    PieDataSet pieDataSet = new PieDataSet(data, "Daily Analysis");
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieChartView.setData(new PieData(pieDataSet));
                    pieChartView.animateXY(5000,5000);
                    pieChartView.getDescription().setEnabled(false);*/

                } else {
                    Toast.makeText(DailyAnalyticActivity.this, "Child does not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setStatusAndImageResource() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    float chaTotal;
                    if (snapshot.hasChild("dayCharity")) {
                        chaTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("dayApparel")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("dayHealth")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("dayPersonal")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("today")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }



                    float traRatio;
                    if (snapshot.hasChild("dayTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("dayFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("dayHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("dayEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("dayEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }

                    float eduRatio;
                    if (snapshot.hasChild("dayEduRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("dayEduRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }

                    float chaRatio;
                    if (snapshot.hasChild("dayCharityRatio")) {
                        chaRatio = Integer.parseInt(snapshot.child("dayCharityRatio").getValue().toString());
                    } else {
                        chaRatio = 0;
                    }

                    float appRatio;
                    if (snapshot.hasChild("dayApparelRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("dayApparelRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }

                    float heaRatio;
                    if (snapshot.hasChild("dayHealthRatio")) {
                        heaRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                    } else {
                        heaRatio = 0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("dayPersonalRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("dayPersonalRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("dayOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("dailyBudget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }



                    float monthPercent = (monthTotalSpentAmount/monthTotalSpentAmountRatio)*100;
                    if (monthPercent < 50) {
                        monthRatioSpending.setText(monthPercent + "% used of " + monthTotalSpentAmountRatio + ". Status: ");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    } else if (monthPercent >= 50 && monthPercent < 100) {
                        monthRatioSpending.setText(monthPercent + "% used of " + monthTotalSpentAmountRatio + ". Status: ");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    } else {
                        monthRatioSpending.setText(monthPercent + "% used of " + monthTotalSpentAmountRatio + ". Status: ");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);
                    }

                    float transportPercent = (traTotal/traRatio)*100;
                    if (transportPercent < 50) {
                        progress_ratio_transport.setText(transportPercent + "% used of " + traRatio + ". Status: ");
                        status_image_transport.setImageResource(R.drawable.green);
                    } else if (transportPercent >= 50 && transportPercent < 100) {
                        progress_ratio_transport.setText(transportPercent + "% used of " + traRatio + ". Status: ");
                        status_image_transport.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_transport.setText(transportPercent + "% used of " + traRatio + ". Status: ");
                        status_image_transport.setImageResource(R.drawable.red);
                    }

                    float foodPercent = (foodTotal/foodRatio)*100;
                    if (transportPercent < 50) {
                        progress_ratio_food.setText(foodPercent + "% used of " + foodRatio + ". Status: ");
                        status_image_food.setImageResource(R.drawable.green);
                    } else if (foodPercent >= 50 && foodPercent < 100) {
                        progress_ratio_food.setText(foodPercent + "% used of " + foodRatio + ". Status: ");
                        status_image_food.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_food.setText(foodPercent + "% used of " + foodRatio + ". Status: ");
                        status_image_food.setImageResource(R.drawable.red);
                    }

                    float housePercent = (houseTotal/houseRatio)*100;
                    if (housePercent < 50) {
                        progress_ratio_house.setText(housePercent + "% used of " + houseRatio + ". Status: ");
                        status_image_house.setImageResource(R.drawable.green);
                    } else if (housePercent >= 50 && housePercent < 100) {
                        progress_ratio_house.setText(housePercent + "% used of " + houseRatio + ". Status: ");
                        status_image_house.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_house.setText(housePercent + "% used of " + houseRatio + ". Status: ");
                        status_image_house.setImageResource(R.drawable.red);
                    }

                    float entPercent = (entTotal/entRatio)*100;
                    if (entPercent < 50) {
                        progress_ratio_entertainment.setText(entPercent + "% used of " + entRatio + ". Status: ");
                        status_image_entertainment.setImageResource(R.drawable.green);
                    } else if (entPercent >= 50 && entPercent < 100) {
                        progress_ratio_entertainment.setText(entPercent + "% used of " + entRatio + ". Status: ");
                        status_image_entertainment.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_entertainment.setText(entPercent + "% used of " + entRatio + ". Status: ");
                        status_image_entertainment.setImageResource(R.drawable.red);
                    }

                    float eduPercent = (eduTotal/eduRatio)*100;
                    if (eduPercent < 50) {
                        progress_ratio_education.setText(eduPercent + "% used of " + eduRatio + ". Status: ");
                        status_image_education.setImageResource(R.drawable.green);
                    } else if (eduPercent >= 50 && eduPercent < 100) {
                        progress_ratio_education.setText(eduPercent + "% used of " + eduRatio + ". Status: ");
                        status_image_education.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_education.setText(eduPercent + "% used of " + eduRatio + ". Status: ");
                        status_image_education.setImageResource(R.drawable.red);
                    }

                    float charityPercent = (chaTotal/chaRatio)*100;
                    if (charityPercent < 50) {
                        progress_ratio_charity.setText(charityPercent + "% used of " + chaRatio + ". Status: ");
                        status_image_charity.setImageResource(R.drawable.green);
                    } else if (charityPercent >= 50 && charityPercent < 100) {
                        progress_ratio_charity.setText(charityPercent + "% used of " + chaRatio + ". Status: ");
                        status_image_charity.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_charity.setText(charityPercent + "% used of " + chaRatio + ". Status: ");
                        status_image_charity.setImageResource(R.drawable.red);
                    }

                    float apparelPercent = (appTotal/appRatio)*100;
                    if (apparelPercent < 50) {
                        progress_ratio_apparel.setText(apparelPercent + "% used of " + appRatio + ". Status: ");
                        status_image_apparel.setImageResource(R.drawable.green);
                    } else if (apparelPercent >= 50 && apparelPercent < 100) {
                        progress_ratio_apparel.setText(apparelPercent + "% used of " + appRatio + ". Status: ");
                        status_image_apparel.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_apparel.setText(apparelPercent + "% used of " + appRatio + ". Status: ");
                        status_image_apparel.setImageResource(R.drawable.red);
                    }

                    float healthPercent = (heaTotal/heaRatio)*100;
                    if (healthPercent < 50) {
                        progress_ratio_health.setText(healthPercent + "% used of " + heaRatio + ". Status: ");
                        status_image_health.setImageResource(R.drawable.green);
                    } else if (healthPercent >= 50 && healthPercent < 100) {
                        progress_ratio_health.setText(healthPercent + "% used of " + heaRatio + ". Status: ");
                        status_image_health.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_health.setText(healthPercent + "% used of " + heaRatio + ". Status: ");
                        status_image_health.setImageResource(R.drawable.red);
                    }

                    float personalPercent = (perTotal/perRatio)*100;
                    if (personalPercent < 50) {
                        progress_ratio_personal.setText(personalPercent + "% used of " + perRatio + ". Status: ");
                        status_image_personal.setImageResource(R.drawable.green);
                    } else if (personalPercent >= 50 && personalPercent < 100) {
                        progress_ratio_personal.setText(personalPercent + "% used of " + perRatio + ". Status: ");
                        status_image_personal.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_personal.setText(personalPercent + "% used of " + perRatio + ". Status: ");
                        status_image_personal.setImageResource(R.drawable.red);
                    }

                    float otherPercent = (othTotal/othRatio)*100;
                    if (otherPercent < 50) {
                        progress_ratio_other.setText(otherPercent + "% used of " + othRatio + ". Status: ");
                        status_image_other.setImageResource(R.drawable.green);
                    } else if (otherPercent >= 50 && otherPercent < 100) {
                        progress_ratio_other.setText(otherPercent + "% used of " + othRatio + ". Status: ");
                        status_image_other.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_other.setText(otherPercent + "% used of " + othRatio + ". Status: ");
                        status_image_other.setImageResource(R.drawable.red);
                    }
                } else {
                    Toast.makeText(DailyAnalyticActivity.this, "setStatusAndImageResource Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void getTotalweekTransportExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Transport" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsTransportAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayTrans").setValue(totalAmount);
                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekFoodExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Food" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsFoodAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayFood").setValue(totalAmount);
                } else {
                    linearLayoutFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekHouseExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "House" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsHouseAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayHouse").setValue(totalAmount);
                } else {
                    linearLayoutHouse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekEntertainmentExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsEntertainmentAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayEnt").setValue(totalAmount);
                } else {
                    linearLayoutEntertainment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekEducationExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsEducationAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayEdu").setValue(totalAmount);
                } else {
                    linearLayoutEducation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekCharityExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsCharityAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayCharity").setValue(totalAmount);
                } else {
                    linearLayoutCharity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekApparelExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsApparelAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayApparel").setValue(totalAmount);
                } else {
                    linearLayoutApparel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekHealthExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsHealthAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayHealth").setValue(totalAmount);
                } else {
                    linearLayoutHealth.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekPersonalExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Personal" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsPersonalAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayPersonal").setValue(totalAmount);
                } else {
                    linearLayoutPersonal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalweekOtherExpenses() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://budgeting-app-7fa87-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
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
                        analyticsOtherAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("dayOther").setValue(totalAmount);
                } else {
                    linearLayoutOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}