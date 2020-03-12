package com.esoftproindia.esoftpro.profiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esoftproindia.esoftpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileEmployee extends AppCompatActivity {

    public static final String TAG="ProfileEmployee";

    private ImageView imageView;
    private TextView name,designation,college,tvCounter;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private long totalAtt,markAtt,preAtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_employee);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfileEmployee);
        setSupportActionBar(toolbar);

        imageView=(ImageView)findViewById(R.id.profileEmployeeDP);
        name=(TextView)findViewById(R.id.tvNameProfileEmployee);
        designation=(TextView)findViewById(R.id.tvDesignationProfileEmployee);
        college=(TextView)findViewById(R.id.tvCollegeProfileEmployee);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabProfileEmployee);
        progressBar=(ProgressBar)findViewById(R.id.progressAttendanceProfileEmployee);
        tvCounter=(TextView) findViewById(R.id.counterAttendanceProfileEmployee);
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        toolbar.setTitle(sharedPreferences.getString("name","Edit Your Profile"));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Are You Sure ?", Snackbar.LENGTH_LONG).setAction("Edit", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfileEmployee.this,EditProfileEmployee.class));
                    }
                }).show();
            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    name.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("name").getValue(String.class));
                    String dp =dataSnapshot.child("users").child(firebaseUser.getUid()).child("dp").getValue(String.class);
                    Glide.with(ProfileEmployee.this).load(dp).asBitmap().into(imageView);
                    designation.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("designation").getValue(String.class));
                    college.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("college").getValue(String.class));

                    try {
                        markAtt = dataSnapshot.child("users").child(firebaseUser.getUid()).child("attendance").getChildrenCount();
                        totalAtt = dataSnapshot.child("attendance").getChildrenCount();
                        preAtt = (markAtt * 100) / totalAtt;
                        progressBar.setProgress((int) preAtt);
                        tvCounter.setText(String.valueOf(preAtt).concat("%"));
                    }catch (Exception e){
                        progressBar.setProgress(0);
                        tvCounter.setText("0%");
                        Log.d(TAG, "onDataChange: "+e.getMessage());
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
