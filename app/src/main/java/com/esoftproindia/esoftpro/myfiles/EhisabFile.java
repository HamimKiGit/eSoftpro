package com.esoftproindia.esoftpro.myfiles;

public class EhisabFile {
    private String subject,money,description,receipt,time,status,pushKey,searchDate,searchMonth,searchYear,category,date;

    public EhisabFile() {
    }




    public EhisabFile(String subject, String money, String description, String receipt, String time, String status, String pushKey, String searchDate, String searchMonth, String searchYear, String category) {
        this.subject = subject;
        this.money = money;
        this.description = description;
        this.receipt = receipt;
        this.time = time;
        this.status = status;
        this.pushKey = pushKey;
        this.searchDate = searchDate;
        this.searchMonth = searchMonth;
        this.searchYear = searchYear;
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public String getMoney() {
        return money;
    }

    public String getDescription() {
        return description;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getPushKey() {
        return pushKey;
    }

    public String getSearchDate() {
        return searchDate;
    }

    public String getCategory() {
        return category;
    }

    public String getSearchMonth() {
        return searchMonth;
    }

    public String getSearchYear() {
        return searchYear;
    }

    public String getDate() {
        return date;
    }
}
