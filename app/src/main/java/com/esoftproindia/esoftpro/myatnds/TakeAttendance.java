package com.esoftproindia.esoftpro.myatnds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.TakeAttendanceFiles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TakeAttendance extends AppCompatActivity {

    public static final String TAG="TakeAttendance";

    private EditText enroll;
    private ProgressBar progressBar;
    private String YEAR="",TIME="",DAY="";
    private boolean batch=true;
    private TextView changeBatch,counter;
    private DatabaseReference databaseReference;
    private GetDateTime getDateTime;
    private FirebaseUser firebaseUser;
    private QRCodeReaderView qrCodeReaderView;

    private TextView nameTextView,batchTextView,collegeTextView,timeTextView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        enroll=(EditText)findViewById(R.id.newChatEditText);
        nameTextView=(TextView)findViewById(R.id.nameTakeAttendance);
        batchTextView=(TextView)findViewById(R.id.batchTakeAttendance);
        collegeTextView=(TextView)findViewById(R.id.collegeTakeAttendance);
        timeTextView=(TextView)findViewById(R.id.timeTakeAttendance);
        counter=(TextView)findViewById(R.id.takeAttendanceCounter);
        progressBar=(ProgressBar) findViewById(R.id.takeAttendanceProgressBar);
        imageView=(ImageView) findViewById(R.id.dpTakeAttendance);
        qrCodeReaderView=(QRCodeReaderView)findViewById(R.id.qrTakeAttendance);
        enroll.requestFocus();
        changeBatch=(TextView)findViewById(R.id.changeBatch);
        getDateTime =new GetDateTime();
        DAY=getDateTime.getDate();
        YEAR=getDateTime.getYear();
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(1000L);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {
                enroll.setText(text);
                Toast.makeText(TakeAttendance.this, ""+text, Toast.LENGTH_SHORT).show();
            }
        });
        enroll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length()==15 || charSequence.toString().length()==19|| charSequence.toString().length()==28) {
                    getDateTime = new GetDateTime();
                    TIME = getDateTime.getTime();
                    DAY = getDateTime.getDate();
                    final String firebaseEnrollment = charSequence.toString().replace("/", ":");
                    Query query= databaseReference.child("attendance").child(DAY).orderByChild("enrollment").startAt(firebaseEnrollment).endAt(firebaseEnrollment+"\uf8ff");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.getChildren().iterator().hasNext()) {
                                    for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                                        timeTextView.setText(nameSnapshot.child("time").getValue(String.class));
                                        enroll.setText("");
                                        try{
                                            Uri ringtone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            Ringtone r=RingtoneManager.getRingtone(TakeAttendance.this,ringtone);
                                            r.play();
                                            Vibrator vibrator=(Vibrator)TakeAttendance.this.getSystemService(Context.VIBRATOR_SERVICE);
                                            assert vibrator != null;
                                            vibrator.vibrate(500);
                                        }catch (Exception e){
                                            Log.d(TAG, "onDataChange: "+e.getMessage());
                                        }
                                    }
                                } else {
                                    Query query = databaseReference.child("users").orderByChild("enrollment").startAt(firebaseEnrollment).endAt(firebaseEnrollment + "\uf8ff");
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.getChildren().iterator().hasNext()) {
                                                    for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                                                        String uid = nameSnapshot.child("uid").getValue(String.class);
                                                        String dp = nameSnapshot.child("dp").getValue(String.class);
                                                        nameTextView.setText(nameSnapshot.child("name").getValue(String.class));
                                                        String batch = nameSnapshot.child("batch").getValue(String.class);
                                                        collegeTextView.setText(nameSnapshot.child("college").getValue(String.class));
                                                        timeTextView.setText(nameSnapshot.child("time").getValue(String.class));
                                                        Glide.with(TakeAttendance.this).load(dp).asBitmap().into(imageView);
                                                        batchTextView.setText(batch);
                                                        databaseReference.child("attendance").child(DAY).child(firebaseEnrollment).setValue(new TakeAttendanceFiles(firebaseEnrollment,firebaseUser.getUid(),TIME,DAY));
                                                        assert uid != null;
                                                        databaseReference.child("users").child(uid).child("attendance").child(DAY).setValue(firebaseUser.getUid());
                                                        databaseReference.child("batchAttendance").child(batch+YEAR).child(DAY).setValue(firebaseEnrollment);
                                                        enroll.setText("");
                                                        enroll.requestFocus();
                                                    }
                                                } else {
                                                    Toast.makeText(TakeAttendance.this, "Data not found", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                Log.d(TAG, "onDataChange: "+e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                                        }
                                    });
                                }
                            }catch (Exception e){
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                        }
                    });


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    progressBar.setVisibility(View.GONE);
                    String count= String.valueOf(dataSnapshot.child("attendance").child(DAY).getChildrenCount());
                    counter.setText(count);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });


        changeBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (batch){
                    enroll.setText(String.format("SPI/ST/%s/", YEAR));
                    enroll.requestFocus();
                    changeBatch.setText("VT");
                    batch=false;
                }else {
                    enroll.setText(String.format("SPI/VT/%s/", YEAR));
                    enroll.requestFocus();
                    changeBatch.setText("ST");
                    batch=true;
                }
            }
        });
        counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TakeAttendance.this,MyAttendance.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }


}
