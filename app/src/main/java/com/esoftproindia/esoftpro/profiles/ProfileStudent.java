package com.esoftproindia.esoftpro.profiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.myfiles.AnalysisListFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileStudent extends AppCompatActivity {

    public static final String TAG="ProfileStudent";

    private ImageView imageView;
    private TextView nameTV,enrollmentTV,batchTV,courseTV,collegeTV,counterTV,locationTV;
    private FloatingActionButton fab;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private long totalAtt,markAtt,preAtt;
    private String joinDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_student);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfileStudent);
        setSupportActionBar(toolbar);

        imageView=(ImageView)findViewById(R.id.profileStudentDP);
        nameTV=(TextView)findViewById(R.id.tvNameStudentProfile);
        enrollmentTV=(TextView)findViewById(R.id.tvEnrollmentStudentProfile);
        batchTV=(TextView)findViewById(R.id.tvBatchStudentProfile);
        courseTV=(TextView)findViewById(R.id.tvCourseStudentProfile);
        collegeTV=(TextView)findViewById(R.id.tvCollegeStudentProfile);
        locationTV=(TextView)findViewById(R.id.tvLocationStudentProfile);
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        toolbar.setTitle(sharedPreferences.getString("name","Edit Your Profile"));
        joinDate=sharedPreferences.getString("joinDate",new GetDateTime().getYear());
        fab=(FloatingActionButton) findViewById(R.id.fabProfileStudent);
        progressBar=(ProgressBar)findViewById(R.id.progressAttendanceStudentProfile);
        counterTV=(TextView) findViewById(R.id.counterAttendanceStudentProfile);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Are You Sure ?", Snackbar.LENGTH_LONG).setAction("Edit", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfileStudent.this,EditProfileStudent.class));
                    }
                }).show();
            }
        });


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    nameTV.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("name").getValue(String.class));
                    courseTV.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("course").getValue(String.class));
                    String dp =dataSnapshot.child("users").child(firebaseUser.getUid()).child("dp").getValue(String.class);
                    String mEnrollment=dataSnapshot.child("users").child(firebaseUser.getUid()).child("enrollment").getValue(String.class);
                    Glide.with(ProfileStudent.this).load(dp).asBitmap().into(imageView);
                    assert mEnrollment != null;
                    enrollmentTV.setText(mEnrollment.replace(":","/"));
                    batchTV.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("batch").getValue(String.class));
                    collegeTV.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("college").getValue(String.class));
                    locationTV.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("location").getValue(String.class));
                    if (dp.isEmpty() || nameTV.getText().toString().isEmpty() || enrollmentTV.getText().toString().isEmpty() || batchTV.getText().toString().isEmpty() || collegeTV.getText().toString().isEmpty() || courseTV.getText().toString().isEmpty()){
                        fab.setVisibility(View.VISIBLE);
                    }else {
                        fab.setVisibility(View.INVISIBLE);
                    }
                    try {
                        markAtt = dataSnapshot.child("users").child(firebaseUser.getUid()).child("attendance").getChildrenCount();
                        totalAtt = dataSnapshot.child("batchAttendance").child(batchTV.getText().toString()+joinDate).getChildrenCount();
                        preAtt = (markAtt * 100) / totalAtt;
                        progressBar.setProgress((int) preAtt);
                        counterTV.setText(String.valueOf(preAtt).concat("%"));
                    }catch (Exception e){
                        progressBar.setProgress(0);
                        counterTV.setText("0%");
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
