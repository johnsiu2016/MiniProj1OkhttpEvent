package com.example.john.miniproj1okhttpevent;

/**
 * Created by John on 15/4/2016.
 */
public class Results {
    String name;
    String address;
    String distance;

    public Results(String name, String address, String distance) {
        this.name = name;
        this.address = address;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return  name;

    }
}