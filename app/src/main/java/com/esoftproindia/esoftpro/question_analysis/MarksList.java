package com.esoftproindia.esoftpro.question_analysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myatnds.MyAttendance;
import com.esoftproindia.esoftpro.myfiles.MarksListFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MarksList extends AppCompatActivity {
    public static final String TAG="MarksList";

    ArrayList<String> mName=new ArrayList<>();
    ArrayList<String> mCollege = new ArrayList<>();
    ArrayList<String> mMarks = new ArrayList<>();
    ArrayList<String> mAttendance= new ArrayList<>();
    private DatabaseReference databaseReference;

    private String pushKey,examName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks_list);

        ListView listView = (ListView) findViewById(R.id.listViewMarksList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabMarksList);
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        pushKey=getIntent().getStringExtra("pushKey");
        examName=getIntent().getStringExtra("examName");
        databaseReference= FirebaseDatabase.getInstance().getReference();

        if (sharedPreferences.getString("designation","").equals("HR")){
            fab.setVisibility(View.VISIBLE);
        }

        FirebaseListAdapter<MarksListFile> adapter = new FirebaseListAdapter<MarksListFile>(
                this,
                MarksListFile.class,
                R.layout.layout_marks,
                databaseReference.child("marksList").child(new GetDateTime().getYear()).child(pushKey).orderByChild("marks")
        ) {
            @Override
            protected void populateView(final View v, final MarksListFile model, int position) {
                try {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                String name = dataSnapshot.child("users").child(model.getUid()).child("name").getValue(String.class);
                                String college = dataSnapshot.child("users").child(model.getUid()).child("college").getValue(String.class);
                                String qNum = dataSnapshot.child("analysis").child(pushKey).child("qNum").getValue(String.class);
                                long markAtt = dataSnapshot.child("users").child(model.getUid()).child("attendance").getChildrenCount();
                                long totalAtt = dataSnapshot.child("attendance").getChildrenCount();
                                long preAtt = (markAtt * 100) / totalAtt;
                                ((TextView) v.findViewById(R.id.nameMarksLay)).setText(name);
                                ((TextView) v.findViewById(R.id.attendanceMarksLay)).setText(String.valueOf(preAtt).concat("%"));
                                ((TextView) v.findViewById(R.id.marksMarksLay)).setText(String.format("%s/%s", model.getMarks(), qNum));
                                mName.add(name);
                                mCollege.add(college);
                                mMarks.add(model.getMarks() + "/" + qNum);
                                mAttendance.add(String.valueOf(preAtt).concat("%"));

                            } catch (Exception e) {
                                Log.d(TAG, "onDataChange: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "populateView: " + e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(MarksList.this, ""+mName.size(), Toast.LENGTH_SHORT).show();
                    StringBuilder data = new StringBuilder();
                    data.append("Sr. No.,Name,College Name,Marks,Attendance");
                    for (int i = 0; i < mName.size(); i++) {
                        data.append("\n").append(i + 1).append(",").append(mName.get(i)).append(",").append(mCollege.get(i)).append(",").append(mMarks.get(i)).append(",").append(mAttendance.get(i));
                    }

                    File root= Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        File fileDir =new File(root.getAbsolutePath()+"/eSoftpro/marksheet/");
                        fileDir.mkdirs();

                        File file=new File(fileDir,examName+".csv");
                        FileWriter fileWriter=new FileWriter(file);
                        BufferedWriter out=new BufferedWriter(fileWriter);
                        out.write(String.valueOf(data));
                        out.close();
                        Toast.makeText(MarksList.this, "Saved", Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
    }
}
