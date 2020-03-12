package com.esoftproindia.esoftpro.myfiles;

public class QuestionFile {
    private String question,img,option1,option2,option3,option4,answer,time,uid,pushKey;

    public QuestionFile() {
    }

    public QuestionFile(String question, String img, String option1, String option2, String option3, String option4, String answer, String time, String uid, String pushKey) {
        this.question = question;
        this.img = img;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.time = time;
        this.uid = uid;
        this.pushKey = pushKey;
    }

    public String getQuestion() {
        return question;
    }

    public String getImg() {
        return img;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTime() {
        return time;
    }

    public String getUid() {
        return uid;
    }

    public String getPushKey() {
        return pushKey;
    }
}
