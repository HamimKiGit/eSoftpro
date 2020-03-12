package com.esoftproindia.esoftpro.myfiles;

public class MarksListFile {
    private String uid,marks;

    public MarksListFile() {

    }

    public MarksListFile(String uid, String marks) {
        this.uid = uid;
        this.marks = marks;
    }

    public String getUid() {
        return uid;
    }

    public String getMarks() {
        return marks;
    }
}
