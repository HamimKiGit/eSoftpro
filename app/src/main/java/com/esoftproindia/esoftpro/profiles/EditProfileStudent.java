package com.esoftproindia.esoftpro.profiles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.mynotification.AddNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class EditProfileStudent extends AppCompatActivity {

    public static final String TAG="EditProfileStudent";
    private ImageView imageView;
    private EditText name,enrollment;
    private Spinner batchSpn,collegeSpn,courseSpn;
    private Button btnSave;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private StorageReference storageReference;
    private static final int ChooseImg=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_student);

        imageView=(ImageView) findViewById(R.id.imgEditProfileStudent);
        name=(EditText) findViewById(R.id.etNameEditProfileStudent);
        enrollment=(EditText) findViewById(R.id.etEnrollmentEditProfileStudent);
        batchSpn=(Spinner) findViewById(R.id.spinnerBatchEditProfileStudent);
        collegeSpn=(Spinner) findViewById(R.id.spinnerCollegeEditProfileStudent);
        courseSpn=(Spinner) findViewById(R.id.spinnerCourseEditProfileStudent);
        btnSave=(Button)findViewById(R.id.btnSaveEditProfileStudent);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        if (sharedPreferences.getString("type","").equals("employeeSpi")){
            enrollment.setEnabled(false);
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Glide.with(EditProfileStudent.this).load(dataSnapshot.child("users").child(firebaseUser.getUid()).child("dp").getValue(String.class)).asBitmap().into(imageView);
                    name.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("name").getValue(String.class));
                    String mEnrollment = dataSnapshot.child("users").child(firebaseUser.getUid()).child("enrollment").getValue(String.class);
                    if(mEnrollment != null) {
                        enrollment.setText(mEnrollment.replace(":", "/"));
                    }else {
                        enrollment.setText("");
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
        databaseReference.child("colleges").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    final List<String> areas = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String areaName = areaSnapshot.child("name").getValue(String.class);
                        areas.add(areaName);
                    }
                    ArrayAdapter<String> collegeAdapter = new ArrayAdapter<String>(EditProfileStudent.this, android.R.layout.simple_spinner_item, areas);
                    collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    collegeSpn.setAdapter(collegeAdapter);
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
                    ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(EditProfileStudent.this, android.R.layout.simple_spinner_item, batches);
                    batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    batchSpn.setAdapter(batchAdapter);
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
                try {
                    final List<String> courseArray = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String courseName = areaSnapshot.child("name").getValue(String.class);
                        courseArray.add(courseName);
                    }
                    ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(EditProfileStudent.this, android.R.layout.simple_spinner_item, courseArray);
                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    courseSpn.setAdapter(courseAdapter);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String mEnrollment = enrollment.getText().toString().replace("/", ":");
                    databaseReference.child("users").child(firebaseUser.getUid()).child("name").setValue(name.getText().toString());
                    databaseReference.child("users").child(firebaseUser.getUid()).child("enrollment").setValue(mEnrollment);
                    databaseReference.child("users").child(firebaseUser.getUid()).child("batch").setValue(batchSpn.getSelectedItem().toString());
                    databaseReference.child("users").child(firebaseUser.getUid()).child("college").setValue(collegeSpn.getSelectedItem().toString());
                    databaseReference.child("users").child(firebaseUser.getUid()).child("course").setValue(courseSpn.getSelectedItem().toString());
                    Toast.makeText(EditProfileStudent.this, "Changed", Toast.LENGTH_SHORT).show();
                    finish();
                    onBackPressed();
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,ChooseImg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooseImg && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                setMyFilePath(uri);
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }

    private void setMyFilePath(final Uri uri) {
        final StorageReference filepath=storageReference.child("users/"+firebaseUser.getUid()+"/").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    databaseReference.child("users").child(firebaseUser.getUid()).child("dp").setValue(String.valueOf(uri));
                                    btnSave.setClickable(true);
                                    btnSave.setText("Save");
                                }
                            });
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileStudent.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                btnSave.setClickable(true);
                btnSave.setText("Save");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double pro=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                btnSave.setText(String.valueOf(pro));
                btnSave.setClickable(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
