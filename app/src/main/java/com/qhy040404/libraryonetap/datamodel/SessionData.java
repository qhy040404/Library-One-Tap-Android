package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class SessionData implements Serializable {
    Gson gson = new Gson();

    private static class GsonData {
        private boolean success;
        private String message;
        private String user_id;
    }

    public boolean isSuccess(String returnData) {
        GsonData gsonData = gson.fromJson(returnData, GsonData.class);
        return gsonData.success;
    }
}
