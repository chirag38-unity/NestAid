package com.example.shareit;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class FoodItem {
    String DonorID, DonorName, DonorNumber, FoodName, FoodCount;
    Boolean Status, Verification;
    String Hash;
    Timestamp TimeStamp;
    GeoPoint Location;


    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public FoodItem(String donorID, String donorName, String donorNumber, String foodName, String foodCount, String hash, Boolean status, Boolean verification ,Timestamp timestamp, GeoPoint location) {
        DonorID = donorID;
        DonorName = donorName;
        DonorNumber = donorNumber;
        FoodName = foodName;
        FoodCount = foodCount;
        Status = status;
        Verification = verification;
        TimeStamp = timestamp;
        Location = location;
        Hash = hash;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public Boolean getVerification() {
        return Verification;
    }

    public void setVerification(Boolean verification) {
        Verification = verification;
    }

    public GeoPoint getLocation() {
        return Location;
    }

    public void setLocation(GeoPoint location) {
        Location = location;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return TimeStamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        TimeStamp = timestamp;
    }

    public FoodItem() {
    }


    public String getDonorID() {
        return DonorID;
    }

    public void setDonorID(String donorID) {
        DonorID = donorID;
    }


    public String getDonorName() {
        return DonorName;
    }

    public void setDonorName(String donorName) {
        DonorName = donorName;
    }

    public String getDonorNumber() {
        return DonorNumber;
    }

    public void setDonorNumber(String donorNumber) {
        DonorNumber = donorNumber;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodCount() {
        return FoodCount;
    }

    public void setFoodCount(String foodCount) {
        FoodCount = foodCount;
    }


}
