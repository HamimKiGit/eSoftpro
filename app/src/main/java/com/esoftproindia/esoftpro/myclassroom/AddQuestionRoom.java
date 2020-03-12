package com.esoftproindia.esoftpro.myclassroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.ehisab.AddDepositOnEmp;
import com.esoftproindia.esoftpro.myfiles.QuestionRomFile;
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

public class AddQuestionRoom extends AppCompatActivity {

    public static final String TAG="AddQuestionRoom";

    private ArrayList<String> mUid =new ArrayList<>();

    private EditText descriptionET;
    private TextView uploadTV;

    private String downloadUrl=null;
    private boolean fileBoolean = true;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question_room);

        className=getIntent().getStringExtra("className");
        descriptionET=(EditText)findViewById(R.id.descriptionETAddQuestionRoom);
        uploadTV=(TextView)findViewById(R.id.fileTVAddQuestionRoom);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference=FirebaseStorage.getInstance().getReference();

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
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        uploadTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 1);
            }
        });
    }

    public void addQuestion(View view) {
        String description=descriptionET.getText().toString();
        if (check(description)){
            if (fileBoolean) {
                String pushKey = databaseReference.child("questionRoom").push().getKey();
                databaseReference.child("questionRoom").child(className).child(pushKey).setValue(new QuestionRomFile(firebaseUser.getUid(), new GetDateTime().getDate(), description, downloadUrl, pushKey));
                for (int i=0; i<mUid.size();i++){
                    databaseReference.child("users").child(mUid.get(i)).child("alert").child("classroom").setValue("True");
                }
                finish();
                onBackPressed();
            }else {
                Toast.makeText(this, "Wait", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean check( String description) {
        boolean isTrue = true;
        if (description.isEmpty()) {
            descriptionET.setError("Empty");
            descriptionET.requestFocus();
            isTrue = false;
        }
        return isTrue;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            final Uri uri = data.getData();
            try {
                final StorageReference filepath=storageReference.child("classroom/"+new GetDateTime().getDay()).child(uri.getLastPathSegment());
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
                                            uploadTV.setText("Uploaded");
                                            fileBoolean=true;
                                        }
                                    });
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuestionRoom.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        uploadTV.setText("Failed");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        uploadTV.setText(String.valueOf((int) progress).concat("%"));
                        fileBoolean=false;
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }
}
