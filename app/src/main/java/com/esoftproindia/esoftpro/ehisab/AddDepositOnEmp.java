package com.esoftproindia.esoftpro.ehisab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.myfiles.EhisabFile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esoftproindia.esoftpro.R;
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

public class AddDepositOnEmp extends AppCompatActivity {
    public static final String TAG="AddDepositOnEmp";
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private ArrayList<String> categoryArray = new ArrayList<>();

    private EditText subjectET,moneyET,descriptionET;
    private AutoCompleteTextView categorySpentOnEmpMoneyAC;
    private ProgressBar progressBar;
    private ImageView check,imageView;
    private Toolbar toolbar;

    private static final int ChooseImg = 10;
    private String downloadUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spent_on_emp);
        toolbar = (Toolbar) findViewById(R.id.toolbarAddSpentOnEmp);
        setSupportActionBar(toolbar);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fabSend = (FloatingActionButton) findViewById(R.id.fabSendAddSpentOnEmp);
        subjectET=(EditText)findViewById(R.id.addSpentOnEmpSubjectET);
        moneyET=(EditText)findViewById(R.id.addSpentOnEmpMoneyET);
        descriptionET=(EditText)findViewById(R.id.addSpentOnEmpDescriptionET);
        categorySpentOnEmpMoneyAC=(AutoCompleteTextView) findViewById(R.id.categorySpentOnEmpMoneyAC);
        progressBar=(ProgressBar)findViewById(R.id.progressSpentOnEmpSubjectPB);
        check=(ImageView)findViewById(R.id.checkSpentOnEmpSubjectImg);
        imageView=(ImageView)findViewById(R.id.imageViewAddSpentOnEmp);
        try {
            toolbar.setTitle(new GetDateTime().getDate().replace(":", "/"));
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDateTime getDateTime=new GetDateTime();
                final String TIME=getDateTime.getTime();
                final String TODAY=getDateTime.getDate();
                final String MONTH=getDateTime.getMonth();
                final String YEAR=getDateTime.getYear();
                final String subject=subjectET.getText().toString().trim();
                final String money=moneyET.getText().toString().trim();
                final String description=descriptionET.getText().toString().trim();
                final String category=categorySpentOnEmpMoneyAC.getText().toString().trim();
                final String pushKey=databaseReference.child("eHisab").child(firebaseUser.getUid()).child("deposit").push().getKey();
                if (downloadUrl.equals("")) {
                    Snackbar.make(view, "Upload receipt first", Snackbar.LENGTH_LONG)
                            .setAction("Upload", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select a img"), ChooseImg);
                                }
                            }).show();
                }else if (checkPayment()){
                    Snackbar.make(view, "Submit Rs " + money + " ?", Snackbar.LENGTH_LONG)
                            .setAction("Submit", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    databaseReference.child("eHisab").child(firebaseUser.getUid()).child("deposit").child(pushKey)
                                            .setValue(new EhisabFile(subject, money, description, downloadUrl, TIME, "new",pushKey,TODAY+category,MONTH+":"+YEAR+category,YEAR+category,category));
                                    databaseReference.child("eHisab").child(firebaseUser.getUid()).child("deposit").child(pushKey).child("date").setValue(TODAY);
                                    databaseReference.child("eHisab").child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());
                                    databaseReference.child("spentCategory").child(category).child("name").setValue(category);
                                    databaseReference.child("users").child(firebaseUser.getUid()).child("alert").child("ehisabDeposit").setValue("True");
                                    onBackPressed();
                                }
                            }).show();

                }
            }
        });
        databaseReference.child("spentCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        categoryArray.add(key.child("name").getValue(String.class));
                    }
                    ArrayAdapter yearAdapter=new ArrayAdapter(AddDepositOnEmp.this,android.R.layout.simple_list_item_1,categoryArray);
                    categorySpentOnEmpMoneyAC.setAdapter(yearAdapter);
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean checkPayment() {
        boolean isTrue=true;
        if (subjectET.getText().toString().isEmpty()){
            subjectET.setError("Empty");
            subjectET.requestFocus();
            isTrue=false;
        }else if (moneyET.getText().toString().isEmpty()){
            moneyET.setError("Empty");
            moneyET.requestFocus();
            isTrue=false;
        }else if (descriptionET.getText().toString().isEmpty()){
            descriptionET.setError("Empty");
            descriptionET.requestFocus();
            isTrue=false;
        }else if (categorySpentOnEmpMoneyAC.getText().toString().isEmpty()){
            categorySpentOnEmpMoneyAC.setError("Empty");
            categorySpentOnEmpMoneyAC.requestFocus();
            isTrue=false;
        }
        return isTrue;
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
        final StorageReference filepath=storageReference.child("ehisab/"+new GetDateTime().getDay()).child(uri.getLastPathSegment());
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
                                    downloadUrl= String.valueOf(uri);
                                    progressBar.setVisibility(View.GONE);
                                    check.setVisibility(View.VISIBLE);
                                    toolbar.setTitle("Uploaded");
                                }
                            });
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDepositOnEmp.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                check.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                toolbar.setTitle(String.valueOf((int) progress).concat("%"));
                check.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
