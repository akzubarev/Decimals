package com.education4all.firebase;

import com.education4all.mathCoachAlg.tours.Tour;

import java.util.ArrayList;

public class User {

    private String email;
    private String id;
    private ArrayList<Tour> statistics;

    public User() {
    }

    public User(String name, String email, String id, ArrayList<Tour> statistics) {
        this.email = email;
        this.id = id;
        this.statistics = statistics;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Tour> getStatistics() {
        return statistics;
    }

    public void setStatistics(ArrayList<Tour> statistics) {
        this.statistics = statistics;
    }


}

