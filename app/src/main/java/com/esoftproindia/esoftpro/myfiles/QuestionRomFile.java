package com.esoftproindia.esoftpro.myfiles;

public class QuestionRomFile {
    private String uid,date,description,file,pushKey;

    public QuestionRomFile() {
    }

    public QuestionRomFile(String uid, String date, String description, String file, String pushKey) {
        this.uid = uid;
        this.date = date;
        this.description = description;
        this.file = file;
        this.pushKey = pushKey;
    }

    public QuestionRomFile(String uid, String date, String description, String pushKey) {
        this.uid = uid;
        this.date = date;
        this.description = description;
        this.pushKey = pushKey;
    }

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getFile() {
        return file;
    }

    public String getPushKey() {
        return pushKey;
    }
}
