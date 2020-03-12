package com.esoftproindia.esoftpro.myatnds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.certificates.CertificatesActivity;
import com.esoftproindia.esoftpro.myfiles.TakeAttendanceFiles;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MyAttendance extends AppCompatActivity {
    public static final String TAG="MyAttendance";

    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mBatch = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> oName = new ArrayList<>();
    private ArrayList<String> oBatch = new ArrayList<>();
    private ArrayList<String> oTime = new ArrayList<>();
    private ArrayList<String> oDate = new ArrayList<>();

    private ListView listView;
    private TextView dateTV;
    private TextView error;

    private DatabaseReference databaseReference;

    private int toDay,toMonth,toYear;
    private String DAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attendance);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        listView = (ListView) findViewById(R.id.listViewMyAttendance);
        dateTV = (TextView) findViewById(R.id.dateTVMyAttendance);
        ImageView oneDownloadImg = (ImageView) findViewById(R.id.downloadImgMyAttendance);
        ImageView allDownloadImg = (ImageView) findViewById(R.id.downloadAllImgMyAttendance);
        error=(TextView)findViewById(R.id.errorTvMyAttendance);

        GetDateTime getDateTime=new GetDateTime();
        DAY=getDateTime.getDate();
        toYear= Integer.parseInt(getDateTime.getYear());
        toMonth= Integer.parseInt(getDateTime.getMonth())-1;
        toDay= Integer.parseInt(getDateTime.getDay());
        setPresentList(DAY);
        dateTV.setText(DAY);
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MyAttendance.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            toYear = i;
                            toMonth = i1 + 1;
                            toDay = i2;
                            String sDay = String.valueOf(toDay);
                            String sMonth = String.valueOf(toMonth);
                            if (sDay.length() == 1) {
                                DAY = "0" + sDay + ":" + sMonth + ":" + toYear;
                            } else if (sMonth.length() == 1) {
                                DAY = sDay + ":0" + sMonth + ":" + toYear;
                            } else if (sDay.length() == 1 && sMonth.length() == 1) {
                                DAY = "0" + sDay + ":0" + sMonth + ":" + toYear;
                            } else if (sDay.length() == 2 && sMonth.length() == 2) {
                                DAY = sDay + ":" + sMonth + ":" + toYear;
                            }
                            dateTV.setText(DAY);
                            setPresentList(DAY);
                        }
                    }, toYear, toMonth, toDay);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        allDownloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    StringBuilder data = new StringBuilder();
                    data.append("Sr. No.,Name,Batch,Time,Date");
                    for (int i = 0; i < mTime.size(); i++) {
                        data.append("\n").append(i + 1).append(",").append(mName.get(i)).append(",").append(mBatch.get(i)).append(",").append(mTime.get(i)).append(",").append(mDate.get(i));
                    }

                    File root=Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        File fileDir =new File(root.getAbsolutePath()+"/eSoftpro/attendance");
                        fileDir.mkdirs();

                        File file=new File(fileDir,"allAttendance.csv");
                        FileWriter fileWriter=new FileWriter(file);
                        BufferedWriter out=new BufferedWriter(fileWriter);
                        out.write(String.valueOf(data));
                        out.close();
                        Toast.makeText(MyAttendance.this, "Saved", Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        oneDownloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String date=dateTV.getText().toString();
                    StringBuilder data = new StringBuilder();
                    data.append("Sr. No.,Name,Batch,Time,Date");
                    for (int i = 0; i < oTime.size(); i++) {
                        data.append("\n").append(i + 1).append(",").append(oName.get(i)).append(",").append(oBatch.get(i)).append(",").append(oTime.get(i)).append(",").append(oDate.get(i));
                    }

                    File root=Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        File fileDir =new File(root.getAbsolutePath()+"/eSoftpro/attendance/"+date);
                        fileDir.mkdirs();

                        File file=new File(fileDir,date.replace(":","_")+".csv");
                        FileWriter fileWriter=new FileWriter(file);
                        BufferedWriter out=new BufferedWriter(fileWriter);
                        out.write(String.valueOf(data));
                        out.close();
                        Toast.makeText(MyAttendance.this, "Saved", Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        databaseReference.child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot myDay : dataSnapshot.getChildren()) {
                        for (DataSnapshot myEnrollment : myDay.getChildren()) {
                            String getEnroll = myEnrollment.child("enrollment").getValue(String.class);
                            Query query = databaseReference.child("users").orderByChild("enrollment").startAt(getEnroll).endAt(getEnroll + "\uf8ff");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                                            String name = nameSnapshot.child("name").getValue(String.class);
                                            mBatch.add(nameSnapshot.child("batch").getValue(String.class));
                                            mName.add(name);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, "onDataChange: "+e.getMessage());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                                }
                            });
                            mTime.add(myEnrollment.child("time").getValue(String.class));
                            mDate.add(myEnrollment.child("date").getValue(String.class));

                            Log.d(TAG, "onDataChange: " + myEnrollment.child("time").getValue(String.class));
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }
    private void setPresentList(String day) {
        oName.clear();
        oBatch.clear();
        oTime.clear();
        oDate.clear();
        FirebaseListAdapter<TakeAttendanceFiles> adapter = new FirebaseListAdapter<TakeAttendanceFiles>(
                MyAttendance.this,
                TakeAttendanceFiles.class,
                R.layout.layout_marks,
                databaseReference.child("attendance").child(day)
        ) {
            @Override
            protected void populateView(final View v, final TakeAttendanceFiles model, int position) {

                Query query = databaseReference.child("users").orderByChild("enrollment").startAt(model.getEnrollment()).endAt(model.getEnrollment() + "\uf8ff");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildren().iterator().hasNext()) {
                            try {
                                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                                    String name = nameSnapshot.child("name").getValue(String.class);
                                    String batch = nameSnapshot.child("batch").getValue(String.class);
                                    ((TextView) v.findViewById(R.id.enrollmentMarksLay)).setText(model.getEnrollment());
                                    ((TextView) v.findViewById(R.id.nameMarksLay)).setText(name);
                                    ((TextView) v.findViewById(R.id.marksMarksLay)).setText(batch);
                                    ((TextView) v.findViewById(R.id.attendanceMarksLay)).setText(model.getTime());
                                    oName.add(name);
                                    oBatch.add(batch);
                                    oTime.add(model.getTime());
                                    oDate.add(model.getDate());
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }
                        } else {
                            error.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                    }
                });
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String enrollment=((TextView) view.findViewById(R.id.enrollmentMarksLay)).getText().toString();
                startActivity(new Intent(MyAttendance.this, CertificatesActivity.class).putExtra("enrollment",enrollment));

            }
        });
    }
}
