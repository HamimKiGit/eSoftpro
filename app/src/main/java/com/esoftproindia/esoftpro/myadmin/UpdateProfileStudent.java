package com.esoftproindia.esoftpro.myadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateProfileStudent extends AppCompatActivity {

    public static final String TAG="UpdateProfileStudent";

    private ImageView imageView;
    private EditText name, enrollment;
    private AutoCompleteTextView collegeAC,batchAC,technologyAC,certificateAC,gradeAC,courseAC;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String uid,DATE;
    private TextView textViewType,attendanceTV;
    private long totalAtt,markAtt,preAtt;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_student);
        imageView = (ImageView) findViewById(R.id.imgUpdateStudentProfile);
        name = (EditText) findViewById(R.id.etNameUpdateStudentProfile);
        enrollment = (EditText) findViewById(R.id.etEnrollmentUpdateStudentProfile);
        batchAC = (AutoCompleteTextView) findViewById(R.id.autoBatchUpdateStudentProfile);
        collegeAC = (AutoCompleteTextView) findViewById(R.id.autoCollegeUpdateStudentProfile);
        technologyAC = (AutoCompleteTextView) findViewById(R.id.autoTechnologyUpdateStudentProfile);
        certificateAC = (AutoCompleteTextView) findViewById(R.id.autoCertificateUpdateStudentProfile);
        gradeAC = (AutoCompleteTextView) findViewById(R.id.autoGradeUpdateStudentProfile);
        courseAC = (AutoCompleteTextView) findViewById(R.id.autoCourseUpdateStudentProfile);
        textViewType=(TextView)findViewById(R.id.typeUpdateStudentProfile);
        attendanceTV=(TextView)findViewById(R.id.tvAttendanceUpdateStudentProfile);
        uid=getIntent().getStringExtra("uid");
        DATE=new GetDateTime().getDate();
        enrollment.setEnabled(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences=getSharedPreferences("spi",MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Glide.with(UpdateProfileStudent.this).load(dataSnapshot.child("users").child(uid).child("dp").getValue(String.class)).asBitmap().into(imageView);
                    name.setText(dataSnapshot.child("users").child(uid).child("name").getValue(String.class));
                    collegeAC.setText(dataSnapshot.child("users").child(uid).child("college").getValue(String.class));
                    batchAC.setText(dataSnapshot.child("users").child(uid).child("batch").getValue(String.class));
                    technologyAC.setText(dataSnapshot.child("users").child(uid).child("technology").getValue(String.class));
                    certificateAC.setText(dataSnapshot.child("users").child(uid).child("certificate").getValue(String.class));
                    gradeAC.setText(dataSnapshot.child("users").child(uid).child("grade").getValue(String.class));
                    courseAC.setText(dataSnapshot.child("users").child(uid).child("course").getValue(String.class));
                    textViewType.setText(dataSnapshot.child("users").child(uid).child("type").getValue(String.class));
                    String mEnrollment = dataSnapshot.child("users").child(uid).child("enrollment").getValue(String.class);
                    assert mEnrollment != null;
                    enrollment.setText(mEnrollment.replace(":", "/"));

                    try {
                        String joinDate=dataSnapshot.child("users").child(uid).child("joinDate").getValue(String.class);

                        markAtt = dataSnapshot.child("users").child(uid).child("attendance").getChildrenCount();
                        totalAtt = dataSnapshot.child("batchAttendance").child(batchAC.getText().toString()+joinDate).getChildrenCount();
                        preAtt = (markAtt * 100) / totalAtt;
                        attendanceTV.setText(String.valueOf(preAtt).concat("%"));
                    }catch (Exception e){
                        attendanceTV.setText("0%");
                        Log.d(TAG, "onDataChange: "+e.getMessage());
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
        databaseReference.child("colleges").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final List<String> collegeArray = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String collegeStr = areaSnapshot.child("name").getValue(String.class);
                        collegeArray.add(collegeStr);
                    }
                    ArrayAdapter<String> collegeAdapter = new ArrayAdapter<String>(UpdateProfileStudent.this, android.R.layout.simple_list_item_1, collegeArray);
                    collegeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    collegeAC.setAdapter(collegeAdapter);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        databaseReference.child("batch").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final List<String> batches = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String batchName = areaSnapshot.child("name").getValue(String.class);
                        batches.add(batchName);
                    }
                    ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(UpdateProfileStudent.this, android.R.layout.simple_list_item_1, batches);
                    batchAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    batchAC.setAdapter(batchAdapter);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        databaseReference.child("technology").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final List<String> technologyArray = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String technologyStr = areaSnapshot.child("name").getValue(String.class);
                        technologyArray.add(technologyStr);
                    }
                    ArrayAdapter<String> technologyAdapter = new ArrayAdapter<String>(UpdateProfileStudent.this, android.R.layout.simple_list_item_1, technologyArray);
                    technologyAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    technologyAC.setAdapter(technologyAdapter);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        databaseReference.child("course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                final List<String> courseArray = new ArrayList<String>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String courseStr = areaSnapshot.child("name").getValue(String.class);
                    courseArray.add(courseStr);
                }
                ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(UpdateProfileStudent.this, android.R.layout.simple_list_item_1, courseArray);
                courseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                courseAC.setAdapter(courseAdapter);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == R.id.menuEditProfileReject) {
                if (sharedPreferences.getString("type", "").equals("admin")) {
                    if (textViewType.getText().toString().equals("studentSpi")) {
                        Toast.makeText(this, "Already accepted", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child("users").child(uid).child("type").setValue("");
                        finish();
                        onBackPressed();
                    }
                } else {
                    Toast.makeText(this, "Not allow", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.menuEditProfileAccept) {
                if (sharedPreferences.getString("type", "").equals("admin")) {
                    if (textViewType.getText().toString().equals("studentSpi")) {
                        Toast.makeText(this, "Already accepted", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child("users").child(uid).child("type").setValue("studentSpi");
                        databaseReference.child("users").child(uid).child("joinDate").setValue(DATE);
                        finish();
                        onBackPressed();
                    }
                } else {
                    Toast.makeText(this, "Not allow", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.menuEditProfileSave) {
                if (sharedPreferences.getString("designation", "").equals("HR")) {
                    String mEnrollment = enrollment.getText().toString().replace("/", ":");
                    databaseReference.child("users").child(uid).child("name").setValue(name.getText().toString());
                    databaseReference.child("users").child(uid).child("enrollment").setValue(mEnrollment);
                    databaseReference.child("users").child(uid).child("batch").setValue(batchAC.getText().toString());
                    databaseReference.child("users").child(uid).child("college").setValue(collegeAC.getText().toString());
                    databaseReference.child("users").child(uid).child("technology").setValue(technologyAC.getText().toString());
                    databaseReference.child("users").child(uid).child("certificate").setValue(certificateAC.getText().toString());
                    databaseReference.child("users").child(uid).child("grade").setValue(gradeAC.getText().toString());
                    databaseReference.child("users").child(uid).child("course").setValue(courseAC.getText().toString());
                    databaseReference.child("users").child(uid).child("updateBy").setValue(firebaseUser.getUid());

                    databaseReference.child("colleges").child(collegeAC.getText().toString()).child("name").setValue(collegeAC.getText().toString());
                    databaseReference.child("technology").child(technologyAC.getText().toString()).child("name").setValue(technologyAC.getText().toString());
                    databaseReference.child("course").child(courseAC.getText().toString()).child("name").setValue(technologyAC.getText().toString());
                    Toast.makeText(UpdateProfileStudent.this, "Changed", Toast.LENGTH_SHORT).show();
                    if (!gradeAC.getText().toString().isEmpty()) {
                        databaseReference.child("users").child(uid).child("exitDate").setValue(DATE);
                    }
                    finish();
                    onBackPressed();
                } else {
                    Toast.makeText(this, "Not allow", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }
}
