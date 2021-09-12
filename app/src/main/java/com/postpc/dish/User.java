package com.postpc.dish;

import java.util.ArrayList;

public class User{

    public String name, email, password;
    public ArrayList<String> dishes;

    public User() {}

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.dishes = new ArrayList<>();
    }
}