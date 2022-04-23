package com.qhy040404.libraryonetap.web;

import com.google.gson.Gson;

import java.io.Serializable;

public class CheckSession implements Serializable {
    Gson gson = new Gson();

    /**
     * success : true
     * message : 用户在线！
     * user_id : 20211011300
     */
    private static class GsonData {
        private boolean success;
        private String message;
        private String user_id;

        private boolean getSuccess() {
            return success;
        }

        private String getMessage() {
            return message;
        }

        private String getUser_id() {
            return user_id;
        }
    }

    public boolean isSuccess(String returnData) {
        GsonData gsonData = gson.fromJson(returnData, GsonData.class);
        boolean successData = gsonData.getSuccess();
        return successData;
    }
}
