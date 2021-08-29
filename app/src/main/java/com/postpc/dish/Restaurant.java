package com.postpc.dish;

public class Restaurant {
    public String name;
    public String city;
    public String category;
    public String code;

    Restaurant(String name, String city, String category, String code) {
        this.name = name;
        this.city = city;
        this.category = category;
        this.code = code;
    }

    Restaurant() { }
}
