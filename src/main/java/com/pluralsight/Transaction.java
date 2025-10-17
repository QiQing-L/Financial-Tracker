package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        ///truncate the string to fit the width without messing up the column alignment.
        String descriptionTruncate = description.length() > 30 ? description.substring(0, 27) + "..." : description;
        String vendorTruncate = vendor.length() > 20 ? vendor.substring(0, 17) + "..." : vendor;

        String DATE_PATTERN = "yyyy-MM-dd";
        String TIME_PATTERN = "HH:mm:ss";

        DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);

        return String.format("%s|%s|%-30s|%-20s|%.2f", date.format(DATE_FMT), time.format(TIME_FMT),
                descriptionTruncate, vendorTruncate, amount);
    }


}
