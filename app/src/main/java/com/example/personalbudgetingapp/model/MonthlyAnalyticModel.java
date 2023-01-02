package com.example.personalbudgetingapp.model;

public class MonthlyAnalyticModel {
    String analyticItem;
    String analyticPeriod;
    int image, analyticItemAmount;

    public MonthlyAnalyticModel(String analyticItem, String analyticPeriod, int image, int analyticItemAmount) {
        this.analyticItem = analyticItem;
        this.analyticPeriod = analyticPeriod;
        this.image = image;
        this.analyticItemAmount = analyticItemAmount;
    }

    public MonthlyAnalyticModel(String analyticItem, String analyticPeriod, int image) {
        this.analyticItem = analyticItem;
        this.analyticPeriod = analyticPeriod;
        this.image = image;
    }

    public String getAnalyticItem() {
        return analyticItem;
    }

    public int getImage() {
        return image;
    }

    public String getAnalyticPeriod() {
        return analyticPeriod;
    }

    public int getAnalyticItemAmount() {
        return analyticItemAmount;
    }
}
