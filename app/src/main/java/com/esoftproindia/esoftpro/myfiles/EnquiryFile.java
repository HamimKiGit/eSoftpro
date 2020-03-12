package com.esoftproindia.esoftpro.myfiles;

public class EnquiryFile {
    private String uid,email,mobile,note,pushKey,time,date;

    public EnquiryFile() {

    }

    public EnquiryFile(String uid, String email, String mobile, String note, String pushKey, String time, String date) {
        this.uid = uid;
        this.email = email;
        this.mobile = mobile;
        this.note = note;
        this.pushKey = pushKey;
        this.time = time;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getNote() {
        return note;
    }

    public String getPushKey() {
        return pushKey;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
