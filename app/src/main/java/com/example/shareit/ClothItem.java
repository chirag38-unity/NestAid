package com.example.shareit;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class ClothItem {

    String DonorID, DonorName, DonorNumber, ClothName, ClothCount;
    GeoPoint Location;
    Timestamp TimeStamp;
    Boolean Status, Verification;

    public ClothItem(String donorID, String donorName, String donorNumber, String clothName, String clothCount, GeoPoint location, Timestamp timestamp, Boolean status, Boolean verification) {
        DonorID = donorID;
        DonorName = donorName;
        DonorNumber = donorNumber;
        ClothName = clothName;
        ClothCount = clothCount;
        Location = location;
        TimeStamp = timestamp;
        Status = status;
        Verification = verification;
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

    public ClothItem() {
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
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

    public String getClothName() {
        return ClothName;
    }

    public void setClothName(String clothName) {
        ClothName = clothName;
    }

    public String getClothCount() {
        return ClothCount;
    }

    public void setClothCount(String clothCount) {
        ClothCount = clothCount;
    }

}
