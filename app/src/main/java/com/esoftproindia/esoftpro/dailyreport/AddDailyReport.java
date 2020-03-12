package com.esoftproindia.esoftpro.dailyreport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.DailyReportFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddDailyReport extends AppCompatActivity {
    public static final String TAG="AddDailyReport";

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private EditText addDailyReportET;

    private ArrayList<String> mUid =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_report);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        addDailyReportET=(EditText)findViewById(R.id.addDailyReportET);

        databaseReference.child("users").orderByChild("type").equalTo("employeeSpi").addListenerForSingleValueEvent(new ValueEventListener() {
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


    public void reportNow(View view) {
        String report=addDailyReportET.getText().toString();
        String pushKey=databaseReference.child("dailyReport").push().getKey();
        GetDateTime getDateTime=new GetDateTime();
        String TIME=getDateTime.getTime();
        String DATE=getDateTime.getDate();

        if (report.length()>10){
            try {
                databaseReference.child("dailyReport").child(pushKey).setValue(new DailyReportFile(firebaseUser.getUid(), report, null, TIME, DATE,pushKey));
                Toast.makeText(this, "Report Submitted", Toast.LENGTH_SHORT).show();
                for (int i=0; i<mUid.size();i++){
                    databaseReference.child("users").child(mUid.get(i)).child("alert").child("dailyReport").setValue("True");
                    databaseReference.child("alertMsg").child("dailyReport").setValue(report);
                }
                onBackPressed();
            }catch (Exception e){
                Log.d(TAG, "reportNow: "+e.getMessage());
            }
        }else {
            addDailyReportET.setError("WEEK");
        }
    }
}
