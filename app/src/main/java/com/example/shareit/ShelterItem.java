package com.example.shareit;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class ShelterItem {

    String DonorID, DonorName, DonorNumber, ShelterDescription, ShelterAvailability;
    Boolean Status, Verification;
    Timestamp TimeStamp;
    GeoPoint Location;

    public ShelterItem(String donorID, String donorName, String donorNumber, String shelterDescription, String shelterAvailability, Timestamp timestamp, Boolean status, Boolean verification,GeoPoint location) {
        DonorID = donorID;
        DonorName = donorName;
        DonorNumber = donorNumber;
        ShelterDescription = shelterDescription;
        ShelterAvailability = shelterAvailability;
        Location = location;
        Status = status;
        Verification = verification;
        TimeStamp = timestamp;
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

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public ShelterItem() {
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

    public String getShelterDescription() {
        return ShelterDescription;
    }

    public void setShelterDescription(String shelterDescription) {
        ShelterDescription = shelterDescription;
    }

    public String getShelterAvailability() {
        return ShelterAvailability;
    }

    public void setShelterAvailability(String shelterAvailability) {
        ShelterAvailability = shelterAvailability;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return TimeStamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        TimeStamp = timestamp;
    }
}
