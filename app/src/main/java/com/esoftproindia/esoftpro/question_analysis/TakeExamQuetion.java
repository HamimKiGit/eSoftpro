package com.esoftproindia.esoftpro.question_analysis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class TakeExamQuetion extends AppCompatActivity {

    public static final String TAG = "TakeExamQuetion";

    private ArrayList<String> mQuestion = new ArrayList<>();
    private ArrayList<String> mOption1 = new ArrayList<>();
    private ArrayList<String> mOption2 = new ArrayList<>();
    private ArrayList<String> mOption3 = new ArrayList<>();
    private ArrayList<String> mOption4 = new ArrayList<>();
    private ArrayList<String> mAnswer = new ArrayList<>();
    private ArrayList<String> mImg = new ArrayList<>();
    private ArrayList<String> mPushKey = new ArrayList<>();

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private TextView questionCount, timeCount, questionTv;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private Button previousBtn, nextBtn;
    private ImageView imageView;
    private Dialog dialog;
    private CountDownTimer countDownTimer;

    private SharedPreferences sharedPreferences;

    private String pushKey;
    private int countQ = 0, totalQ, totalA = 0;
    private static long TOTAL_TIMER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_exam_quetion);
        questionCount = (TextView) findViewById(R.id.questionCounterTakeExQTV);
        timeCount = (TextView) findViewById(R.id.timeCounterTakeExQTV);
        questionTv = (TextView) findViewById(R.id.questionTakeExQTV);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupTakeExQ);
        radioButton1 = (RadioButton) findViewById(R.id.radioBtn1TakeExQ);
        radioButton2 = (RadioButton) findViewById(R.id.radioBtn2TakeExQ);
        radioButton3 = (RadioButton) findViewById(R.id.radioBtn3TakeExQ);
        radioButton4 = (RadioButton) findViewById(R.id.radioBtn4TakeExQ);
        previousBtn = (Button) findViewById(R.id.previousBtnTakeExQ);
        nextBtn = (Button) findViewById(R.id.nextBtnTakeExQ);
        imageView = (ImageView) findViewById(R.id.imageTakeExQImg);

        sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pushKey = getIntent().getStringExtra("pushKey");
        TOTAL_TIMER = 60000 * getIntent().getLongExtra("time", 7200000);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_result);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        databaseReference.child("question").child(pushKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    totalQ = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        mQuestion.add(key.child("question").getValue(String.class));
                        mOption1.add(key.child("option1").getValue(String.class));
                        mOption2.add(key.child("option2").getValue(String.class));
                        mOption3.add(key.child("option3").getValue(String.class));
                        mOption4.add(key.child("option4").getValue(String.class));
                        mAnswer.add(key.child("answer").getValue(String.class));
                        mImg.add(key.child("img").getValue(String.class));
                        mPushKey.add(key.child("pushKey").getValue(String.class));
                    }
                    setOnQuestion(countQ);
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
        startCountDown();

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked() && countQ < totalQ) {
                    try {
                        databaseReference.child("question").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).setValue(mOption1.get(countQ));
                        if (mAnswer.get(countQ).equals("1")) {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("1");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        } else {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("0");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        }
                        radioButton1.setChecked(false);
                        setOnQuestion(++countQ);
                    } catch (Exception e) {
                        Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                    }
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked() && countQ < totalQ) {
                    try {
                        databaseReference.child("question").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).setValue(mOption2.get(countQ));
                        if (mAnswer.get(countQ).equals("2")) {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("1");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        } else {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("0");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        }
                        radioButton2.setChecked(false);
                        setOnQuestion(++countQ);
                    } catch (Exception e) {
                        Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                    }
                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked() && countQ < totalQ) {
                    try {
                        databaseReference.child("question").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).setValue(mOption3.get(countQ));
                        if (mAnswer.get(countQ).equals("3")) {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("1");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        } else {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("0");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        }
                        radioButton3.setChecked(false);
                        setOnQuestion(++countQ);
                    } catch (Exception e) {
                        Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                    }
                }
            }
        });
        radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked() && countQ < totalQ) {
                    try {
                        databaseReference.child("question").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).setValue(mOption4.get(countQ));
                        if (mAnswer.get(countQ).equals("4")) {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("1");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        } else {
                            try {
                                databaseReference.child("answer").child(pushKey).child(mPushKey.get(countQ)).child(firebaseUser.getUid()).child("marks").setValue("0");
                            } catch (Exception e) {
                                Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                            }
                        }
                        radioButton4.setChecked(false);
                        setOnQuestion(++countQ);
                    } catch (Exception e) {
                        Log.d(TAG, "onCheckedChanged: " + e.getMessage());
                    }
                }
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countQ--;
                if (countQ > -1) {
                    setOnQuestion(countQ);
                } else {
                    countQ = 0;
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (countQ < totalQ) {
                    countQ++;
                    setOnQuestion(countQ);
                    previousBtn.setVisibility(View.VISIBLE);


                } else {
                    Snackbar.make(view, "Submit Your Exam ?", Snackbar.LENGTH_LONG)
                            .setAction("SUBMIT", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setExamOver();
                                }
                            }).show();
                }
            }
        });
    }

    private void setExamOver() {
        try {
            databaseReference.child("analysis").child(pushKey).child(firebaseUser.getUid()).setValue(firebaseUser.getUid());
            final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressPopupResult);
            final TextView textView = (TextView) dialog.findViewById(R.id.textViewPopupResult);
            Button button = (Button) dialog.findViewById(R.id.btnPopupResult);
            databaseReference.child("answer").child(pushKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot key : dataSnapshot.getChildren()) {
                            String value = key.child(firebaseUser.getUid()).child("marks").getValue(String.class);
                            totalA += Integer.parseInt(value);
                        }
                        int result = (totalA * 100) / totalQ;
                        progressBar.setProgress(result);
                        textView.setText(String.valueOf(result).concat("%"));
                        databaseReference.child("marksList").child(new GetDateTime().getYear()).child(pushKey).child(firebaseUser.getUid()).child("marks").setValue(totalA + "");
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: " + e.getMessage());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
            databaseReference.child("marksList").child(new GetDateTime().getYear()).child(pushKey).child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(pushKey, false);
            editor.apply();

            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onCheckedChanged: " + e.getMessage());
        }
    }

    private void setOnQuestion(int qNum) {
        try {
            if (qNum == 0) {
                previousBtn.setVisibility(View.GONE);
            } else {
                previousBtn.setVisibility(View.VISIBLE);
            }
            int num = qNum + 1;
            if (num < totalQ) {
                questionCount.setText(String.valueOf(num).concat("/" + totalQ));
                nextBtn.setText("NEXT");
            } else {
                questionCount.setText("FULL");
                nextBtn.setText("SUBMIT");
            }
            questionTv.setText(mQuestion.get(qNum));
            radioButton1.setText(mOption1.get(qNum));
            radioButton2.setText(mOption2.get(qNum));
            radioButton3.setText(mOption3.get(qNum));
            radioButton4.setText(mOption4.get(qNum));
            radioGroup.clearCheck();

            Glide.with(TakeExamQuetion.this).load(mImg.get(qNum)).asBitmap().into(imageView);
        } catch (Exception e) {
            Log.d(TAG, "setOnQuestion: " + e.getMessage());
        }
    }

    private void startCountDown() {
        try {
            countDownTimer = new CountDownTimer(TOTAL_TIMER, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TOTAL_TIMER = millisUntilFinished;
                    int minutes = (int) (TOTAL_TIMER / 1000) / 60;
                    int second = (int) (TOTAL_TIMER / 1000) % 60;
                    String timeFormat = String.format(Locale.getDefault(), "%2d:%2d", minutes, second);
                    timeCount.setText(timeFormat);
                    if (TOTAL_TIMER < 300000) {
                        timeCount.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        timeCount.setTextColor(getResources().getColor(R.color.green));
                    }
                }

                @Override
                public void onFinish() {
                    setExamOver();
                    countDownTimer.cancel();
                }
            }.start();
        } catch (Exception e) {
            Log.d(TAG, "startCountDown: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            setExamOver();
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        } catch (Exception e) {
            Log.d(TAG, "onDestroy: " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Not Allowed", Toast.LENGTH_SHORT).show();
    }
}