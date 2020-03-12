package com.esoftproindia.esoftpro.enquiry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.EnquiryFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEnquiry extends AppCompatActivity {

    public static final String TAG="AddEnquiry";

    private EditText emailET,mobileET,noteET;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private String TIME,DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enquiry);

        emailET=(EditText)findViewById(R.id.emailETAddEnquiry);
        mobileET=(EditText)findViewById(R.id.mobileETAddEnquiry);
        noteET=(EditText)findViewById(R.id.noteETAddEnquiry);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        GetDateTime getDateTime = new GetDateTime();
        TIME=getDateTime.getTime();
        DATE = getDateTime.getDate();
    }

    public void submitEnquiry(View view) {
        String email=emailET.getText().toString();
        String mobile=mobileET.getText().toString();
        String note=noteET.getText().toString();

        if (isTrue(email,mobile,note)){
            try {
                String pushKey = databaseReference.child("enquiry").push().getKey();
                databaseReference.child("enquiry").child(pushKey).setValue(new EnquiryFile(firebaseUser.getUid(), email, mobile, note, pushKey, TIME, DATE));
                Toast.makeText(this, "Enquiry Submitted", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }catch (Exception e){
                Log.d(TAG, "submitEnquiry: "+e.getMessage());
            }
        }
    }

    private boolean isTrue( String email, String mobile, String note) {
        boolean isTrues=true;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Invalid");
            emailET.requestFocus();
            isTrues=false;
        }else if (!Patterns.PHONE.matcher(mobile).matches() && mobile.length() != 10){
            mobileET.setError("Invalid");
            mobileET.requestFocus();
            isTrues=false;
        }else if (note.isEmpty()){
            noteET.setError("Empty");
            noteET.requestFocus();
            isTrues=false;
        }
        return isTrues;
    }
}
