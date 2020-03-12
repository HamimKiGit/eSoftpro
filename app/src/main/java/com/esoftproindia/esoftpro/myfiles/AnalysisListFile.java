package com.esoftproindia.esoftpro.myfiles;

public class AnalysisListFile {
    private String name,qNum,uid,pushKey,enable,time;

    public AnalysisListFile() {
    }

    public AnalysisListFile(String name,String qNum, String uid, String pushKey, String enable,String time) {
        this.name = name;
        this.uid = uid;
        this.pushKey = pushKey;
        this.enable = enable;
        this.time=time;
        this.qNum=qNum;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getPushKey() {
        return pushKey;
    }

    public String getEnable() {
        return enable;
    }

    public String getTime() {
        return time;
    }

    public String getqNum() {
        return qNum;
    }
}
