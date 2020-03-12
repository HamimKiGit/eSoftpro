package com.esoftproindia.esoftpro.dailyreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewDailyReport extends AppCompatActivity {
    public static final String TAG="ViewDailyReport";

    private DatabaseReference databaseReference;

    private String mUid,mPushKey,mImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_daily_report);

        try {
            final ImageView userDp = (ImageView) findViewById(R.id.dpViewDailyReport);
            final TextView userName = (TextView) findViewById(R.id.nameViewDailyReport);
            final TextView description = (TextView) findViewById(R.id.textViewDailyReport);
            final TextView time = (TextView) findViewById(R.id.timeViewDailyReport);
            final TextView date = (TextView) findViewById(R.id.dateViewDailyReport);
            final ImageView imageView = (ImageView) findViewById(R.id.imageViewDailyReport);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            Intent intent = getIntent();
            mUid = intent.getStringExtra("mUid");
            mPushKey = intent.getStringExtra("mPushKey");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String mDp = dataSnapshot.child("users").child(mUid).child("dp").getValue(String.class);
                        userName.setText(dataSnapshot.child("users").child(mUid).child("name").getValue(String.class));
                        description.setText( dataSnapshot.child("dailyReport").child(mPushKey).child("description").getValue(String.class));
                        time.setText(dataSnapshot.child("dailyReport").child(mPushKey).child("time").getValue(String.class));
                        date.setText(dataSnapshot.child("dailyReport").child(mPushKey).child("date").getValue(String.class));
//                        mImg = dataSnapshot.child("dailyReport").child(mPushKey).child("img").getValue(String.class);

                        try {
                            Glide.with(ViewDailyReport.this).load(mDp).asBitmap().into(userDp);
                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: " + e.getMessage());
                        }
//                        Glide.with(ViewDailyReport.this).load(mImg).asBitmap().into(imageView);

                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
    }
}
