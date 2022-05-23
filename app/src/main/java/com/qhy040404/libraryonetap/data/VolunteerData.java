package com.qhy040404.libraryonetap.data;

import com.google.gson.Gson;

import java.io.Serializable;

public class VolunteerData implements Serializable {
    Gson gson = new Gson();

    private static class Gsondata {
        private int numSameID;
        private int numSameName;
        private double totalDuration;
    }

    public int getSameID(String data) {
        return gson.fromJson(data, Gsondata.class).numSameID;
    }

    public int getSameName(String data) {
        return gson.fromJson(data, Gsondata.class).numSameName;
    }

    public double getTotalHours(String data) {
        return gson.fromJson(data, Gsondata.class).totalDuration;
    }
}
