package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class CancelData {
    Gson gson = new Gson();

    public String getMessage(String returnData) {
        return gson.fromJson(returnData, GsonData.class).message;
    }

    private static class GsonData implements Serializable {
        private String message;
    }
}