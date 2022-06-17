package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class VolunteerData {
    Gson gson = new Gson();

    public int getSameID(String data) {
        return gson.fromJson(data, GsonData.class).numSameID;
    }

    public int getSameName(String data) {
        return gson.fromJson(data, GsonData.class).numSameName;
    }

    public double getTotalHours(String data) {
        return gson.fromJson(data, GsonData.class).totalDuration;
    }

    private static class GsonData implements Serializable {
        private int numSameID;
        private int numSameName;
        private double totalDuration;
    }
}