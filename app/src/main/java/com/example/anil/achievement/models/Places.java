package com.example.anil.achievement.models;

/**
 * Created by anil on 08/08/2017.
 */

public class Places {
    private float latitude;
    private float longitude;
    private String locationTitle;
    private String locationAddress;
    private String locationImgUrl;

    public Places(float latitude, float longitude, String locationTitle, String locationAddress, String locationImgUrl) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTitle = locationTitle;
        this.locationAddress = locationAddress;
        this.locationImgUrl = locationImgUrl;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationImgUrl() {
        return locationImgUrl;
    }
}
