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

public class UpdateProfileEmployee extends AppCompatActivity {

    public static final String TAG="UpdateProfileEmployee";

    private ImageView imageView;
    private EditText name;
    private AutoCompleteTextView designation, college;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String uid;
    private TextView textViewType,attendanceTV;
    private long totalAtt,markAtt,preAtt;


    private ArrayList<String> mUid =new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_employee);
        imageView = (ImageView) findViewById(R.id.imgUpdateProfileEmployee);
        name = (EditText) findViewById(R.id.etNameUpdateProfileEmployee);
        designation = (AutoCompleteTextView) findViewById(R.id.autoDesignationUpdateProfileEmployee);
        college = (AutoCompleteTextView) findViewById(R.id.autoCollegeUpdateProfileEmployee);
        textViewType=(TextView)findViewById(R.id.typeUpdateProfileEmployee);
        attendanceTV=(TextView)findViewById(R.id.tvAttendanceUpdateProfileEmployee);
        uid=getIntent().getStringExtra("uid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences=getSharedPreferences("spi",MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        mUid.add(keys.child("uid").getValue(String.class));
                    }
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+ databaseError.getDetails());
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Glide.with(UpdateProfileEmployee.this).load(dataSnapshot.child("users").child(uid).child("dp").getValue(String.class)).asBitmap().into(imageView);
                    name.setText(dataSnapshot.child("users").child(uid).child("name").getValue(String.class));
                    designation.setText(dataSnapshot.child("users").child(uid).child("designation").getValue(String.class));
                    college.setText(dataSnapshot.child("users").child(uid).child("college").getValue(String.class));
                    textViewType.setText(dataSnapshot.child("users").child(uid).child("type").getValue(String.class));
                    try {
                        markAtt = dataSnapshot.child("users").child(uid).child("attendance").getChildrenCount();
                        totalAtt = dataSnapshot.child("attendance").getChildrenCount();
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
                    final List<String> areas = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String areaName = areaSnapshot.child("name").getValue(String.class);
                        areas.add(areaName);
                    }
                    ArrayAdapter<String> collegeAdapter = new ArrayAdapter<String>(UpdateProfileEmployee.this, android.R.layout.simple_list_item_1, areas);
                    collegeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    college.setAdapter(collegeAdapter);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        databaseReference.child("designation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final List<String> batches = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String batchName = areaSnapshot.child("name").getValue(String.class);
                        batches.add(batchName);
                    }
                    ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(UpdateProfileEmployee.this, android.R.layout.simple_list_item_1, batches);
                    batchAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    designation.setAdapter(batchAdapter);
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
                try {
                    if (sharedPreferences.getString("type", "").equals("admin")) {
                        databaseReference.child("users").child(uid).child("type").setValue("");
                        databaseReference.child("users").child(uid).push().child("leaveDate").setValue(new GetDateTime().getDate());
                        for (int i = 0; i < mUid.size(); i++) {
                            databaseReference.child("users").child(mUid.get(i)).child("alert").child("employee").setValue("True");
                        }
                        finish();
                        onBackPressed();
                    } else {
                        Toast.makeText(this, "Not allow", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
                }
            } else if (id == R.id.menuEditProfileAccept) {
                try {
                    if (sharedPreferences.getString("type", "").equals("admin")) {
                        if (textViewType.getText().toString().equals("employeeSpi")) {
                            Toast.makeText(this, "Already accepted", Toast.LENGTH_SHORT).show();
                        } else {

                            databaseReference.child("users").child(uid).child("type").setValue("employeeSpi");
                            databaseReference.child("users").child(uid).push().child("joinDate").setValue(new GetDateTime().getDate());
                            for (int i = 0; i < mUid.size(); i++) {
                                databaseReference.child("users").child(mUid.get(i)).child("alert").child("employee").setValue("True");
                            }
                            finish();
                            onBackPressed();
                        }

                    } else {
                        Toast.makeText(this, "Not allow", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
                }
            } else if (id == R.id.menuEditProfileSave) {
                try {
                    if (sharedPreferences.getString("designation", "").equals("HR")) {
                        databaseReference.child("users").child(uid).child("name").setValue(name.getText().toString());
                        databaseReference.child("users").child(uid).child("designation").setValue(designation.getText().toString());
                        databaseReference.child("users").child(uid).child("college").setValue(college.getText().toString());
                        databaseReference.child("users").child(uid).child("updateBy").setValue(firebaseUser.getUid());
                        Toast.makeText(UpdateProfileEmployee.this, "Changed", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Not allow", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
                }
            }
        }catch (Exception e){
            Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }
}
