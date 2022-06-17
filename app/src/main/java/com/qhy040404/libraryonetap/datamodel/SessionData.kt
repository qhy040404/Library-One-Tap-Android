package com.qhy040404.libraryonetap.datamodel;

import com.google.gson.Gson;

import java.io.Serializable;

public class SessionData {
    Gson gson = new Gson();

    public boolean isSuccess(String returnData) {
        GsonData gsonData = gson.fromJson(returnData, GsonData.class);
        return gsonData.success;
    }

    private static class GsonData implements Serializable {
        private boolean success;
        private String message;
        private String user_id;
    }
}
