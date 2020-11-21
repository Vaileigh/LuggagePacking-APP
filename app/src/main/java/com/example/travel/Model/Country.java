package com.example.travel.Model;

public class Country {

    private String countryCode, countryName, countryImage;

    public Country(){

    }

    public Country(String countryCode, String countryImage, String countryName) {
        this.countryCode = countryCode;
        this.countryImage = countryImage;
        this.countryName = countryName;

    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryImage() {
        return countryImage;
    }

    public void setCountryImage(String countryImage) {
        this.countryImage = countryImage;
    }
}
