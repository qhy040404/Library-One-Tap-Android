package com.qhy040404.libraryonetap.data;

import static com.qhy040404.libraryonetap.utils.TimeUtilsKt.timeSingleToDouble;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class OrderList implements Serializable {
    Gson gson = new Gson();

    private static class GsonData {
        private String total;
        private List<RowsBean> rows;
        private final Calendar calendar = Calendar.getInstance();
        private final String today = calendar.get(Calendar.YEAR) + "-" + timeSingleToDouble(calendar.get(Calendar.MONTH) + 1) + "-" + timeSingleToDouble(calendar.get(Calendar.DAY_OF_MONTH));

        private String getOrder_id(String mode) {
            String order_id = "oid";
            boolean notHasToday = true;
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode) && list.order_date.equals(today)) {
                    order_id = list.order_id;
                    notHasToday = false;
                    break;
                }
            }
            if (notHasToday) {
                for (int i = 0; i < rows.size(); i++) {
                    list = rows.get(i);
                    if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode)) {
                        order_id = list.order_id;
                        break;
                    }
                }
            }
            return order_id;
        }

        private String getOrder_process(String mode) {
            String order_process = "";
            boolean notHasToday = true;
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode) && list.order_date.equals(today)) {
                    order_process = list.order_process;
                    notHasToday = false;
                    break;
                }
            }
            if (notHasToday) {
                for (int i = 0; i < rows.size(); i++) {
                    list = rows.get(i);
                    if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode)) {
                        order_process = list.order_process;
                        break;
                    }
                }
            }
            return order_process;
        }

        private String getSpace_name(String mode) {
            String space_name = "";
            boolean notHasToday = true;
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode) && list.order_date.equals(today)) {
                    space_name = list.space_name;
                    notHasToday = false;
                    break;
                }
            }
            if (notHasToday) {
                for (int i = 0; i < rows.size(); i++) {
                    list = rows.get(i);
                    if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode)) {
                        space_name = list.space_name;
                        break;
                    }
                }
            }
            return space_name;
        }

        private String getSeat_label(String mode) {
            String seat_label = "";
            boolean notHasToday = true;
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode) && list.order_date.equals(today)) {
                    seat_label = (String) list.seat_label;
                    notHasToday = false;
                    break;
                }
            }
            if (notHasToday) {
                for (int i = 0; i < rows.size(); i++) {
                    list = rows.get(i);
                    if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode)) {
                        seat_label = (String) list.seat_label;
                        break;
                    }
                }
            }
            return seat_label;
        }

        private String getOrder_date(String mode) {
            String order_date = "";
            boolean notHasToday = true;
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode) && list.order_date.equals(today)) {
                    order_date = list.order_date;
                    notHasToday = false;
                    break;
                }
            }
            if (notHasToday) {
                for (int i = 0; i < rows.size(); i++) {
                    list = rows.get(i);
                    if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals(mode)) {
                        order_date = list.order_date;
                        break;
                    }
                }
            }
            return order_date;
        }

        private String getBack_time(String mode, String prompt) {
            String back_time = "";
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if (list.order_process.equals("暂离") && list.order_type.equals(mode)) {
                    if (!list.back_time.equals("00:00:00")) {
                        back_time = prompt + list.back_time;
                    }
                    break;
                }
            }
            return back_time;
        }

        private String getAll_users() {
            String all_users = "";
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals("1")) {
                    all_users = (String) list.all_users;
                    break;
                }
            }
            return all_users;
        }

        private String getFull_time() {
            String full_time = "";
            RowsBean list = new RowsBean();
            for (int i = 0; i < rows.size(); i++) {
                list = rows.get(i);
                if ((list.order_process.equals("进行中") || list.order_process.equals("暂离") || list.order_process.equals("审核通过")) && list.order_type.equals("1")) {
                    full_time = (list.order_start_time.split(" "))[1] + "-" + (list.order_end_time.split(" "))[1];
                    break;
                }
            }
            return full_time;
        }

        public static class RowsBean implements Serializable {
            private String order_id;
            private String order_type;
            private String space_name;
            private Object seat_label;
            private Object all_users;
            private String order_start_time;
            private String area_id;
            private String order_date;
            private String back_time;
            private String order_end_time;
            private String order_users;
            private String order_admin_user;
            private String order_time;
            private String order_process;
        }
    }

    public String getTotal(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.total;
    }

    public String getOrder_id(String data, String mode) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getOrder_id(mode);
    }

    public String getOrder_process(String data, String mode) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getOrder_process(mode);
    }

    public String getSpace_name(String data, String mode) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getSpace_name(mode);
    }

    public String getSeat_label(String data, String mode) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getSeat_label(mode);
    }

    public String getOrder_date(String data, String mode) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getOrder_date(mode);
    }

    public String getBack_time(String data, String mode, String prompt) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getBack_time(mode, prompt);
    }

    public String getAll_users(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getAll_users();
    }

    public String getFull_time(String data) {
        GsonData gsonData = gson.fromJson(data, GsonData.class);
        return gsonData.getFull_time();
    }
}
