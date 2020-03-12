package com.esoftproindia.esoftpro.mynotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esoftproindia.esoftpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewNotification extends AppCompatActivity {

    public static final String TAG="ViewNotification";

    private String mUid, mPushKey, mImg;
    private DatabaseReference databaseReference;
    String type;
    FirebaseUser firebaseUser;
    private AlertDialog.Builder ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);


        ad=new AlertDialog.Builder(this);
        final ImageView userDp = (ImageView) findViewById(R.id.dpViewNotification);
        try {
            final TextView userName = (TextView) findViewById(R.id.nameViewNotification);
            final TextView subject = (TextView) findViewById(R.id.subjectViewNotification);
            final TextView message = (TextView) findViewById(R.id.textViewNotification);
            final TextView time = (TextView) findViewById(R.id.timeViewNotification);
            final TextView date = (TextView) findViewById(R.id.dateViewNotification);
            final ImageView imageView = (ImageView) findViewById(R.id.imageViewNotification);
            final ImageView deleteImg = (ImageView) findViewById(R.id.deleteViewNotification);
            final ImageView downloadImg = (ImageView) findViewById(R.id.downloadViewNotification);
            SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = getIntent();
            mUid = intent.getStringExtra("mUid");
            mPushKey = intent.getStringExtra("mPushKey");
            type = sharedPreferences.getString("type", "");

            if (firebaseUser.getUid().equals(mUid) || type.equals("admin")) {
                deleteImg.setVisibility(View.VISIBLE);
            }
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String mDp = dataSnapshot.child("users").child(mUid).child("dp").getValue(String.class);
                        String mName = dataSnapshot.child("users").child(mUid).child("name").getValue(String.class);
                        String mSubject = dataSnapshot.child("notification").child(mPushKey).child("subject").getValue(String.class);
                        String mText = dataSnapshot.child("notification").child(mPushKey).child("text").getValue(String.class);
                        String mTime = dataSnapshot.child("notification").child(mPushKey).child("time").getValue(String.class);
                        String mDate = dataSnapshot.child("notification").child(mPushKey).child("date").getValue(String.class);
                        mImg = dataSnapshot.child("notification").child(mPushKey).child("img").getValue(String.class);

                        try {
                            Glide.with(ViewNotification.this).load(mDp).asBitmap().into(userDp);
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                        userName.setText(mName);
                        subject.setText(mSubject);
                        message.setText(mText);
                        time.setText(mTime);
                        date.setText(mDate);
                        Glide.with(ViewNotification.this).load(mImg).asBitmap().into(imageView);
                        if (mImg != null) {
                            downloadImg.setVisibility(View.VISIBLE);
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


            if (sharedPreferences.getString("uid", "").equals(mUid)) {
                deleteImg.setVisibility(View.VISIBLE);
            }
            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ad.setTitle("Are you sure ?");
                        ad.setIcon(R.drawable.delete);
                        ad.setMessage("Click DELETE to remove notification or click cancel");
                        ad.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child("notification").child(mPushKey).removeValue();
                                finish();
                                onBackPressed();
                            }
                        });
                        ad.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });
                        ad.setCancelable(false);
                        ad.show();

                    } catch (Exception e) {
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }
            });

            downloadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mImg)));
                    }catch (Exception e){
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
    }
}