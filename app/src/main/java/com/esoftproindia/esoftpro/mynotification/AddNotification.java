package com.esoftproindia.esoftpro.mynotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.NotificationFile;
import com.esoftproindia.esoftpro.profiles.EditProfileStudent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;

public class AddNotification extends AppCompatActivity {
    public static final String TAG="AddNotification";

    private ArrayList<String> mUid =new ArrayList<>();

    private EditText eSubject, eMessage;
    private String pushKey,TIME,DATE;
    private TextView count;
    private CardView cardView;
    private ProgressBar progressBar;
    private static final int ChooseImg = 10;
    private static final int ChooseFile = 11;
    private String downloadUrl = null;
    private ImageView attechImg, checkImg, myImg;
    FloatingActionButton floatingActionButton;
    private Dialog dialog;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notification);
        try {
            eSubject = (EditText) findViewById(R.id.subjectWriteNoticeEditText);
            eMessage = (EditText) findViewById(R.id.textWriteNoticeEditText);
            count = (TextView) findViewById(R.id.countWriteNoticeTextView);
            progressBar = (ProgressBar) findViewById(R.id.progressWriteNoticePBar);
            attechImg = (ImageView) findViewById(R.id.attachmentWriteNoticeImg);
            checkImg = (ImageView) findViewById(R.id.checkWriteNoticeImg);
            myImg = (ImageView) findViewById(R.id.imageWriteNoticeImageView);
            cardView = (CardView) findViewById(R.id.cardWriteNoticeCardView);
            floatingActionButton = (FloatingActionButton) findViewById(R.id.fabWriteNoticeFloatBtn);
            storageReference = FirebaseStorage.getInstance().getReference();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            pushKey = databaseReference.push().getKey();
            GetDateTime getDateTime=new GetDateTime();
            TIME=getDateTime.getTime();
            DATE=getDateTime.getDate();

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
            attechImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog = new Dialog(AddNotification.this);
                        dialog.setContentView(R.layout.popup_write_notice_option);

                        Button imgBtn = (Button) dialog.findViewById(R.id.imageNoticePoppup);
                        Button fileBtn = (Button) dialog.findViewById(R.id.fileNoticePopup);
                        Button closeBtn = (Button) dialog.findViewById(R.id.closeNoticePopup);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        imgBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select a img"), ChooseImg);
                                dialog.dismiss();
                            }
                        });
                        fileBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("application/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select a file"), ChooseFile);
                                dialog.dismiss();
                            }
                        });
                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }
            });

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (check()) {
                            databaseReference.child("notification").child(pushKey).setValue(
                                    new NotificationFile(firebaseUser.getUid(), pushKey, eSubject.getText().toString(), eMessage.getText().toString(),
                                            TIME,DATE, downloadUrl));
                            for (int i=0; i<mUid.size();i++){
                                databaseReference.child("users").child(mUid.get(i)).child("alert").child("notification").setValue("True");
                                databaseReference.child("alertMsg").child("notification").setValue(eMessage.getText().toString());
                            }
                            onBackPressed();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooseImg && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                myImg.setImageBitmap(bitmap);
                setMyFilePath(uri);
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }

    private void setMyFilePath(final Uri uri) {
        final StorageReference filepath=storageReference.child("notifications/"+new GetDateTime().getDay()).child(uri.getLastPathSegment());
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
                                    attechImg.setVisibility(View.GONE);
                                    checkImg.setVisibility(View.VISIBLE);
                                    count.setVisibility(View.GONE);
                                    cardView.setVisibility(View.VISIBLE);
                                    floatingActionButton.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNotification.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                attechImg.setVisibility(View.VISIBLE);
                checkImg.setVisibility(View.GONE);
                count.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                count.setText(((int) progress) + " %");
                attechImg.setVisibility(View.GONE);
                checkImg.setVisibility(View.GONE);
                count.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.GONE);
            }
        });
    }


    private boolean check() {
        boolean x = true;
        if (eSubject.getText().toString().isEmpty()) {
            eSubject.setError("Empty !");
            eSubject.requestFocus();
            x = false;
        } else if (eMessage.getText().toString().isEmpty()) {
            eMessage.setError("Empty !");
            eMessage.requestFocus();
            x = false;
        }
        return x;
    }
}
