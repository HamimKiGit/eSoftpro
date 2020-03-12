package com.esoftproindia.esoftpro.certificates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CertificatesActivity extends AppCompatActivity {

    public static final String TAG="CertificatesActivity";

    private EditText enrollmentET;
    private TextView searchTV,nameTV,gradeTV,dateTV,courseTV,technologyTV,certificateTV,collegeTV;
    private ImageView resultImg,dpImg;
    private LinearLayout linearLayout;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificates);

        enrollmentET=(EditText)findViewById(R.id.enrollmentETCertificates);
        searchTV = (TextView)findViewById(R.id.searchTVCertificates);

        nameTV = (TextView)findViewById(R.id.nameCertificates);
        gradeTV = (TextView)findViewById(R.id.gradeCertificates);
        dateTV = (TextView)findViewById(R.id.dateCertificates);
        courseTV = (TextView)findViewById(R.id.courseCertificates);
        technologyTV = (TextView)findViewById(R.id.technologyCertificates);
        certificateTV = (TextView)findViewById(R.id.certificateCertificates);
        collegeTV = (TextView)findViewById(R.id.collegeCertificates);

        resultImg=(ImageView)findViewById(R.id.resultImgCertificates);
        dpImg=(ImageView)findViewById(R.id.dpCertificates);

        linearLayout=(LinearLayout)findViewById(R.id.linearCertificates);

        databaseReference= FirebaseDatabase.getInstance().getReference();

        try {
            String enrollment = getIntent().getStringExtra("enrollment");
            assert enrollment != null;
            setCertificates(enrollment);
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=enrollmentET.getText().toString();
                if (name.isEmpty()){
                    enrollmentET.setError("Empty");
                    enrollmentET.requestFocus();
                }else {
                    setCertificates(name);
                }
            }
        });
    }

    private void setCertificates(String name) {
        Query query = databaseReference.child("users").orderByChild("enrollment").equalTo(name.replace("/", ":"));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        resultImg.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                            String dp = nameSnapshot.child("dp").getValue(String.class);
                            nameTV.setText(nameSnapshot.child("name").getValue(String.class));
                            gradeTV.setText(nameSnapshot.child("grade").getValue(String.class));
                            String join = nameSnapshot.child("joinDate").getValue(String.class);
                            String exit = nameSnapshot.child("exitDate").getValue(String.class);
                            courseTV.setText(nameSnapshot.child("course").getValue(String.class));
                            technologyTV.setText(nameSnapshot.child("technology").getValue(String.class));
                            certificateTV.setText(nameSnapshot.child("certificate").getValue(String.class));
                            collegeTV.setText(nameSnapshot.child("college").getValue(String.class));

                            dateTV.setText(String.format("%s - %s", join, exit));
                            Glide.with(CertificatesActivity.this).load(dp).asBitmap().into(dpImg);
                        }


                    } else {
                        resultImg.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);

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
    }
}
