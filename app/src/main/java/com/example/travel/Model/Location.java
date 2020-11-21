package com.example.travel.Model;

public class Location {

    private String locationName, locationImage;

    public Location(){

    }

    public Location(String locationName, String locationImage) {
        this.locationName = locationName;
        this.locationImage = locationImage;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public void setLocationImage(String locationImage) {
        this.locationImage = locationImage;
    }
}
