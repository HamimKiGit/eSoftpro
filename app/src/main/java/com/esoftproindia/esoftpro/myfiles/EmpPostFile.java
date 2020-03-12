package com.esoftproindia.esoftpro.myfiles;

public class EmpPostFile {
    private String subject,description,designation,pushKey,img;

    public EmpPostFile() {
    }

    public EmpPostFile(String subject, String description, String designation, String pushKey) {
        this.subject = subject;
        this.description = description;
        this.designation = designation;
        this.pushKey = pushKey;
    }


    public String getSubject() {
        return subject;
    }

    public String getImg() {
        return img;
    }

    public String getDescription() {
        return description;
    }

    public String getDesignation() {
        return designation;
    }

    public String getPushKey() {
        return pushKey;
    }
}
