package com.esoftproindia.esoftpro.analysis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.AnalysisListFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddAnalysisList extends AppCompatActivity {

    public static final String TAG="AddAnalysisList";

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private EditText testNameET,questionET,timeET;
    private ArrayList<String> mUid =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_analysis_list);
        testNameET=(EditText)findViewById(R.id.tNameAddAnalysisList);
        questionET=(EditText)findViewById(R.id.qNumberAddAnalysisList);
        timeET=(EditText)findViewById(R.id.timeAddAnalysisList);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();

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
    }

    public void createNewAnalysis(View view) {
        String name=testNameET.getText().toString().trim();
        String question=questionET.getText().toString().trim();
        String time=timeET.getText().toString().trim();
        String key=databaseReference.child("analysis").push().getKey();
        if (validate(name,question,time)){
                databaseReference.child("analysis").child(key).setValue(new AnalysisListFile(name,question,firebaseUser.getUid(),key,"False",time));
                Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
            for (int i=0; i<mUid.size();i++){
                databaseReference.child("users").child(mUid.get(i)).child("alert").child("analysis").setValue("True");
            }
                onBackPressed();
        }
    }

    private boolean validate(String name, String question, String time) {
        boolean isTrue=true;
        if (name.isEmpty()){
            testNameET.setError("Empty");
            testNameET.requestFocus();
            isTrue=false;
        }else if (question.isEmpty()){
            questionET.setError("Empty");
            questionET.requestFocus();
            isTrue=false;
        }else if (time.isEmpty()){
            timeET.setError("Empty");
            timeET.requestFocus();
            isTrue=false;
        }
        return isTrue;
    }
}
