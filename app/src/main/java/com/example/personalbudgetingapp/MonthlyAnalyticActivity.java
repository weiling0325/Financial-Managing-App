package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class MonthlyAnalyticActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_monthly_analytic);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Monthly Analytics");

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

        /*getTotalweekTransportExpenses("Transport", "Trans");
        getTotalweekFoodExpenses();
        getTotalweekHouseExpenses();
        getTotalweekEntertainmentExpenses();
        getTotalweekEducationExpenses();
        getTotalweekCharityExpenses();
        getTotalweekApparelExpenses();
        getTotalweekHealthExpenses();
        getTotalweekPersonalExpenses();
        getTotalweekOtherExpenses();*/
        getTotalmonthItemExpenses("Transport", "Trans");
        getTotalmonthItemExpenses("Food", "Food");
        getTotalmonthItemExpenses("House", "House");
        getTotalmonthItemExpenses("Entertainment", "Ent");
        getTotalmonthItemExpenses("Education", "Edu");
        getTotalmonthItemExpenses("Charity", "Charity");
        getTotalmonthItemExpenses("Apparel", "Apparel");
        getTotalmonthItemExpenses("Health", "Health");
        getTotalmonthItemExpenses("Personal", "Personal");
        getTotalmonthItemExpenses("Other", "Other");
        getTotalmonthSpending();

        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResource();
            }
        }, 2000);
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

    private void getTotalmonthItemExpenses(String spendingItem, String databaseRef) {
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
                        analyticsTransportAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("month" + databaseRef).setValue(totalAmount);
                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
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

    public void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int traTotal;
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    int houseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    int entTotal;
                    if (snapshot.hasChild("monthEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    int eduTotal;
                    if (snapshot.hasChild("monthEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    int chaTotal;
                    if (snapshot.hasChild("monthCha")) {
                        chaTotal = Integer.parseInt(snapshot.child("monthCha").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    int appTotal;
                    if (snapshot.hasChild("monthApp")) {
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    int heaTotal;
                    if (snapshot.hasChild("monthHea")) {
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    int perTotal;
                    if (snapshot.hasChild("monthPer")) {
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    int othTotal;
                    if (snapshot.hasChild("monthOther")) {
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
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
                    pie.title("Monthly Analysis");
                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Monthly Expenses").padding(0d, 0d, 10d, 0d);
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

                    PieDataSet pieDataSet = new PieDataSet(data, "Monthly Analysis");
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieChartView.setData(new PieData(pieDataSet));
                    pieChartView.animateXY(5000,5000);
                    pieChartView.getDescription().setEnabled(false);

                    //Initialize array list
                    ArrayList<Da> pieEntries = new ArrayList<>();

                    //Initialize pie data set
                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "Student");
                    //Set colors
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    //Set pie data
                    pieChartView.setData(new PieData(pieDataSet));
                    //Set animation
                    pieChartView.animateXY(5000, 5000);
                    //Hide description
                    pieChartView.getDescription().setEnabled(false);*/

                } else {
                    Toast.makeText(MonthlyAnalyticActivity.this, "Child does not exist!", Toast.LENGTH_SHORT).show();
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
                    if (snapshot.hasChild("monthTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("monthFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("monthHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("monthEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("monthEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    float chaTotal;
                    if (snapshot.hasChild("monthCha")) {
                        chaTotal = Integer.parseInt(snapshot.child("monthCha").getValue().toString());
                    } else {
                        chaTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("monthApp")) {
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("monthHea")) {
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("monthPer")) {
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("monthOther")) {
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("month")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("month").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }



                    float traRatio;
                    if (snapshot.hasChild("monthTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("monthTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("monthFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("monthFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("monthHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("monthHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("monthEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("monthEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }

                    float eduRatio;
                    if (snapshot.hasChild("monthEduRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("monthEduRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }

                    float chaRatio;
                    if (snapshot.hasChild("monthChaRatio")) {
                        chaRatio = Integer.parseInt(snapshot.child("monthChaRatio").getValue().toString());
                    } else {
                        chaRatio = 0;
                    }

                    float appRatio;
                    if (snapshot.hasChild("monthAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("monthAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }

                    float heaRatio;
                    if (snapshot.hasChild("monthHeaRatio")) {
                        heaRatio = Integer.parseInt(snapshot.child("monthHeaRatio").getValue().toString());
                    } else {
                        heaRatio = 0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("monthPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("monthPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("monthOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("monthOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("budget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("budget").getValue().toString());
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
                    Toast.makeText(MonthlyAnalyticActivity.this, "setStatusAndImageResource Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };

    /*private void getTotalweekTransportExpenses(String spendingItem, String databaseRef) {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Transport" + months.getMonths();

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
                        analyticsTransportAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthTrans").setValue(totalAmount);
                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
                    personalRef.child("monthTrans").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Food" + months.getMonths();

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
                        analyticsFoodAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthFood").setValue(totalAmount);
                } else {
                    linearLayoutFood.setVisibility(View.GONE);
                    personalRef.child("monthFood").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "House" + months.getMonths();

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
                        analyticsHouseAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthHouse").setValue(totalAmount);
                } else {
                    linearLayoutHouse.setVisibility(View.GONE);
                    personalRef.child("monthHouse").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Entertainment" + months.getMonths();

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
                        analyticsEntertainmentAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthEnt").setValue(totalAmount);
                } else {
                    linearLayoutEntertainment.setVisibility(View.GONE);
                    personalRef.child("monthEnt").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Education" + months.getMonths();

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
                        analyticsEducationAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthEdu").setValue(totalAmount);
                } else {
                    linearLayoutEducation.setVisibility(View.GONE);
                    personalRef.child("monthEdu").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Charity" + months.getMonths();

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
                        analyticsCharityAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthCharity").setValue(totalAmount);
                } else {
                    linearLayoutCharity.setVisibility(View.GONE);
                    personalRef.child("monthCharity").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Apparel" + months.getMonths();

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
                        analyticsApparelAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthApparel").setValue(totalAmount);
                } else {
                    linearLayoutApparel.setVisibility(View.GONE);
                    personalRef.child("monthApparel").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Health" + months.getMonths();

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
                        analyticsHealthAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthHealth").setValue(totalAmount);
                } else {
                    linearLayoutHealth.setVisibility(View.GONE);
                    personalRef.child("monthHealth").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Personal" + months.getMonths();

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
                        analyticsPersonalAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthPersonal").setValue(totalAmount);
                } else {
                    linearLayoutPersonal.setVisibility(View.GONE);
                    personalRef.child("monthPersonal").setValue(0);
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
        Months months = Months.monthsBetween(epoch, now);

        String itemNmonth = "Other" + months.getMonths();

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
                        analyticsOtherAmount.setText("$" + totalAmount);
                    }
                    personalRef.child("monthOther").setValue(totalAmount);
                } else {
                    linearLayoutOther.setVisibility(View.GONE);
                    personalRef.child("monthOther").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}