package com.postpc.dish;

import java.util.ArrayList;

public class User{

    public String name, email;
    public ArrayList<String> dishes;

    public User() {}

    public User(String name, String email){
        this.name = name;
        this.email = email;
        this.dishes = new ArrayList<>();
    }
}