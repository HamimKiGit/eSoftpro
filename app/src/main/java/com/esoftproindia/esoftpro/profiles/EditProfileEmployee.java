package com.esoftproindia.esoftpro.profiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.EmpPostFile;
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
import java.util.Objects;

public class EditProfileEmployee extends AppCompatActivity {

    public static final String TAG="EditProfileEmployee";

    private ImageView imageView;
    private EditText name,enrollment;
    private AutoCompleteTextView college;
    private Button btnSave;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int ChooseImg=10;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_employee);
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        imageView=(ImageView) findViewById(R.id.imgEditProfileEmployee);
        name=(EditText) findViewById(R.id.etNameEditProfileEmployee);
        enrollment=(EditText) findViewById(R.id.etEnrollmentEditProfileEmployee);
//        designation=(AutoCompleteTextView) findViewById(R.id.autoDesignationEditProfileEmployee);
        college=(AutoCompleteTextView) findViewById(R.id.autoCollegeEditProfileEmployee);
        btnSave=(Button)findViewById(R.id.btnSaveEditProfileEmployee);
        Button btnPost = (Button) findViewById(R.id.btnAddPostEditProfileEmployee);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.popup_write_subject_msg);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final String designationString = sharedPreferences.getString("designation", "");
        if (designationString.equals("Director") || designationString.equals("CEO")){
            btnPost.setVisibility(View.VISIBLE);
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Glide.with(EditProfileEmployee.this).load(dataSnapshot.child("users").child(firebaseUser.getUid()).child("dp").getValue(String.class)).asBitmap().into(imageView);
                    name.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("name").getValue(String.class));
//                    designation.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("designation").getValue(String.class));
                    college.setText(dataSnapshot.child("users").child(firebaseUser.getUid()).child("college").getValue(String.class));
                    String mEnrollment = dataSnapshot.child("users").child(firebaseUser.getUid()).child("enrollment").getValue(String.class);
                    if(mEnrollment != null) {
                        enrollment.setText(mEnrollment.replace(":", "/"));
                    }else {
                        enrollment.setText(firebaseUser.getUid());
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
                    ArrayAdapter<String> collegeAdapter = new ArrayAdapter<String>(EditProfileEmployee.this, android.R.layout.simple_list_item_1, areas);
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
//        databaseReference.child("designation").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    final List<String> batches = new ArrayList<String>();
//                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
//                        String batchName = areaSnapshot.child("name").getValue(String.class);
//                        batches.add(batchName);
//                    }
//                    ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(EditProfileEmployee.this, android.R.layout.simple_list_item_1, batches);
//                    batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    designation.setAdapter(batchAdapter);
//                }catch (Exception e){
//                    Log.d(TAG, "onDataChange: "+e.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
//            }
//        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String mEnrollment = enrollment.getText().toString().replace("/", ":");
                    databaseReference.child("users").child(firebaseUser.getUid()).child("name").setValue(name.getText().toString());
                    databaseReference.child("users").child(firebaseUser.getUid()).child("enrollment").setValue(mEnrollment);
//                    databaseReference.child("users").child(firebaseUser.getUid()).child("designation").setValue(designation.getText().toString());
                    databaseReference.child("users").child(firebaseUser.getUid()).child("college").setValue(college.getText().toString());
                    Toast.makeText(EditProfileEmployee.this, "Changed", Toast.LENGTH_SHORT).show();
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
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final String pushKey = databaseReference.child("empPost").push().getKey();
                    final EditText subjectET = (EditText) dialog.findViewById(R.id.popupWSMSubject);
                    final EditText descriptionET = (EditText) dialog.findViewById(R.id.popupWSMDescription);
                    dialog.show();
                    Button button = (Button) dialog.findViewById(R.id.popupWSMBtn);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String subject = subjectET.getText().toString();
                            String description = descriptionET.getText().toString();
                            databaseReference.child("empPost").child(pushKey).setValue(new EmpPostFile(subject, description, designationString, pushKey));
                            subjectET.setText("");
                            descriptionET.setText("");
                            dialog.dismiss();
                        }
                    });
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
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
                Toast.makeText(EditProfileEmployee.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
