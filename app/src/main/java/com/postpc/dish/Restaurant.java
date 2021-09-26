package com.postpc.dish;

public class Restaurant {
    public String id;
    public String name;
    public String city;
    public String category;
    public String code;
    public String Wifi;

    Restaurant(String name, String city, String category, String code ,String Wifi) {
        this.name = name;
        this.city = city;
        this.category = category;
        this.code = code;
        this.id = "";
        this.Wifi = Wifi;
    }

    Restaurant() { }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
