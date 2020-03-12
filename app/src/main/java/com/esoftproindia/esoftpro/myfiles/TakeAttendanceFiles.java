package com.esoftproindia.esoftpro.myfiles;

public class TakeAttendanceFiles {
    private String enrollment,uid,time,date;

    public TakeAttendanceFiles() {
    }

    public TakeAttendanceFiles(String enrollment, String uid, String time, String date) {
        this.enrollment = enrollment;
        this.uid = uid;
        this.time = time;
        this.date = date;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public String getUid() {
        return uid;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
