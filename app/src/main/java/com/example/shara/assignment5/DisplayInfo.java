package com.example.shara.assignment5;

/**
 * Created by shara on 3/16/2017.
 */

public class DisplayInfo {

    private String nickname, country, state, city, year, email,uid;
    private Double lat, lng;

    public DisplayInfo() {
    }

    public DisplayInfo(String nickname, String state, String city, String year) {
        this.nickname = nickname;
        this.state = state;
        this.city = city;
        this.year = year;
    }

    public DisplayInfo(String nickname, String country, String state, String city, String year, Double lat, Double lng) {
        this.nickname = nickname;
        this.state = state;
        this.city = city;
        this.country = country;
        this.year = year;
        this.lat = lat;
        this.lng = lng;
    }

    public DisplayInfo(String uid, String nickname, String email)
    {
        this.nickname = nickname;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return nickname;
    }

    public void setName(String name) {
        this.nickname = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
