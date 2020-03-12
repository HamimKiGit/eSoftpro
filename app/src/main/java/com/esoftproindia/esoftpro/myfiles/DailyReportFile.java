package com.esoftproindia.esoftpro.myfiles;

public class DailyReportFile {
    private String uid,description,editable,time,date,pushKey;

    public DailyReportFile() {
    }

    public DailyReportFile(String uid, String description, String editable, String time, String date,String pushKey) {
        this.uid = uid;
        this.description = description;
        this.editable = editable;
        this.time = time;
        this.date = date;
        this.pushKey = pushKey;
    }

    public String getUid() {
        return uid;
    }

    public String getDescription() {
        return description;
    }

    public String getEditable() {
        return editable;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getPushKey() {
        return pushKey;
    }
}
