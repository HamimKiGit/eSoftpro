package com.esoftproindia.esoftpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG="LoginActivity";
    private EditText mobile, otp;
    private TextView student, teacher, message;
    private String logInAs = "student";
    private CardView cardViewOtp;
    private Button sendBtn;
    private String isAction = "mobile";
    private AlertDialog.Builder ad;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private boolean goNow = false;
    private ImageView otpImg;
    private ProgressBar otpPb;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mobile = (EditText) findViewById(R.id.signInMobileET);
        otp = (EditText) findViewById(R.id.signInOtpET);
        otpImg = (ImageView) findViewById(R.id.signInOtpImg);
        otpPb = (ProgressBar) findViewById(R.id.signInOtpPB);
        student = (TextView) findViewById(R.id.signInStudentTV);
        teacher = (TextView) findViewById(R.id.signInTeacherTV);
        message = (TextView) findViewById(R.id.signInMessageTV);
        cardViewOtp = (CardView) findViewById(R.id.signInOtpCV);
        sendBtn = (Button) findViewById(R.id.signInBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInAs = "student";
                student.setTextColor(getResources().getColor(R.color.colorPrimary));
                student.setTextSize(18);
                teacher.setTextColor(getResources().getColor(R.color.black));
                teacher.setTextSize(15);
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInAs = "employee";
                teacher.setTextColor(getResources().getColor(R.color.colorPrimary));
                teacher.setTextSize(18);
                student.setTextColor(getResources().getColor(R.color.black));
                student.setTextSize(15);
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isAction.equals("mobile")) {
                        final String sNumber = "+91"+mobile.getText().toString();
                        if (check()) {
                            try {
                                ad = new AlertDialog.Builder(LoginActivity.this);
                                ad.setTitle("We will be verifying the phone number: ");
                                ad.setMessage(sNumber + "  Is this OK, or would you like to edit the number");
                                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mobile.setEnabled(false);
                                        verifyNumber(sNumber);
                                        cardViewOtp.setVisibility(View.VISIBLE);
                                        isAction = "otp";
                                        sendBtn.setText("Verify");
                                    }
                                });
                                ad.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        mobile.setEnabled(true);
                                        cardViewOtp.setVisibility(View.GONE);
                                    }
                                });
                                ad.setCancelable(false);
                                ad.show();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, "mobile btn " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (isAction.equals("otp")) {
                        if (!otp.getText().toString().isEmpty()) {
                            try {
                                String mVerificationCode = otp.getText().toString();
                                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, mVerificationCode);
                                signInWithPhoneAuth(phoneAuthCredential);
                                message.setText("Wait...");
                                otpPb.setVisibility(View.VISIBLE);
                                otpImg.setVisibility(View.GONE);
                            } catch (Exception e) {
                                message.setText("Invalid OTP number");
                            }
                        } else {
                            otp.setError("Empty");
                            message.setText("Enter one time password");
                        }

                    } else {
                        try {
                            if (goNow) {
                                databaseReference.child("users").child(firebaseUser.getUid()).child("type").setValue(logInAs);
                                databaseReference.child("users").child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());
                                databaseReference.child("users").child(firebaseUser.getUid()).child("mobile").setValue(firebaseUser.getPhoneNumber());

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                ActivityCompat.finishAffinity(LoginActivity.this);
                            } else {
                                message.setText("Verify number first");
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onClick: "+e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuth(phoneAuthCredential);
                Log.d(TAG, "onVerificationCompleted: "+phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: "+e.getMessage());

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                mResendingToken = forceResendingToken;
            }
        };
    }

    private void signInWithPhoneAuth(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        otp.setText("xxxxxx");
                        otp.setTextSize(15);
                        otp.setEnabled(false);
                        otpImg.setVisibility(View.VISIBLE);
                        otpPb.setVisibility(View.GONE);
                        message.setText("Verification done");
                        goNow = true;
                        isAction = "";
                        sendBtn.setText("Continue");
                        firebaseUser = task.getResult().getUser();
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    String decision = dataSnapshot.child("users").child(firebaseUser.getUid()).child("type").getValue(String.class);
                                    if (decision != null) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        ActivityCompat.finishAffinity(LoginActivity.this);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "onDataChange: "+e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "onComplete: "+e.getMessage());
                    }
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private boolean check() {
        boolean x = true;
        if (mobile.getText().toString().isEmpty()) {
            mobile.setError("Empty");
            mobile.requestFocus();
            x = false;
        } else if (mobile.getText().toString().length() < 10) {
            mobile.setError("Invalid number");
            message.setText("Invalid number");
            mobile.requestFocus();
            x = false;
        }
        return x;
    }

    public void verifyNumber(String num) {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    num,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks);
        } catch (Exception e) {
            Log.d(TAG, "verifyNumber: "+e.getMessage());
        }
    }
}