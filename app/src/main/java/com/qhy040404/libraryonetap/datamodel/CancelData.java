package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class CancelData implements Serializable {
    Gson gson = new Gson();

    private static class GsonData {
        private String message;
    }

    public String getMessage(String returnData) {
        return gson.fromJson(returnData, GsonData.class).message;
    }
}