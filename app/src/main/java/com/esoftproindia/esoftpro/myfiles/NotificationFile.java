package com.esoftproindia.esoftpro.myfiles;

public class NotificationFile {
    private String img,pushKey,subject,text,time,date,uid;

    public NotificationFile() {
    }

    public NotificationFile(String uid, String pushKey, String subject, String text, String time, String date, String img) {
        this.img = img;
        this.pushKey = pushKey;
        this.subject = subject;
        this.text = text;
        this.time = time;
        this.uid = uid;
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public String getPushKey() {
        return pushKey;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }
}
