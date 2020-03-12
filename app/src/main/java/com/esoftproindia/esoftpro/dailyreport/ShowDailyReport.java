package com.esoftproindia.esoftpro.dailyreport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myatnds.MyAttendance;
import com.esoftproindia.esoftpro.myfiles.DailyReportFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ShowDailyReport extends AppCompatActivity {

    public static final String TAG="ShowDailyReport";

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseListAdapter<DailyReportFile> adapter;

    private SharedPreferences sharedPreferences;

    private ImageView backImg,searchImg,addImg;
    private TextView dateTV;
    private EditText searchET;
    private ListView listView;

    private String DAY,DESIGNATION,TYPE;
    private int toDay,toMonth,toYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_daily_report);
        backImg=(ImageView)findViewById(R.id.showDailyBackArrowImg);
        searchImg=(ImageView)findViewById(R.id.showDailySearchImg);
        addImg=(ImageView)findViewById(R.id.showDailyAddImg);
        dateTV=(TextView)findViewById(R.id.showDailyDateTV);
        searchET=(EditText) findViewById(R.id.showDailySearchET);
        searchET=(EditText) findViewById(R.id.showDailySearchET);
        listView=(ListView) findViewById(R.id.listViewShowDailyReport);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        sharedPreferences=getSharedPreferences("spi",MODE_PRIVATE);
        DESIGNATION=sharedPreferences.getString("designation","");
        TYPE=sharedPreferences.getString("type","");
        GetDateTime getDateTime=new GetDateTime();
        DAY=getDateTime.getDate();
        toYear= Integer.parseInt(getDateTime.getYear());
        toMonth= Integer.parseInt(getDateTime.getMonth())-1;
        toDay= Integer.parseInt(getDateTime.getDay());
        setMyListByDate(DAY);
        dateTV.setText(DAY.replace(":","/"));
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backImg.setVisibility(View.GONE);
                searchET.setVisibility(View.GONE);
                dateTV.setVisibility(View.VISIBLE);
            }
        });
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backImg.setVisibility(View.VISIBLE);
                searchET.setVisibility(View.VISIBLE);
                dateTV.setVisibility(View.GONE);
            }
        });
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowDailyReport.this,AddDailyReport.class));
            }
        });
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ShowDailyReport.this, new DatePickerDialog.OnDateSetListener() {
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
                            setMyListByDate(DAY);
                        }
                    }, toYear, toMonth, toDay);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    if (charSequence.length()>2) {
                        databaseReference.child("users").orderByChild("name").startAt(charSequence.toString()).endAt(charSequence.toString() + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                                        for (DataSnapshot uid : dataSnapshot.getChildren()) {
                                            String userId = uid.child("uid").getValue(String.class);
                                            String userName = uid.child("name").getValue(String.class);
                                            setMyListByUid(userId, userName);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "onDataChange: " + e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setMyListByDate(String date) {
        adapter=new FirebaseListAdapter<DailyReportFile>(
                this,
                DailyReportFile.class,
                R.layout.layout_gmail_list_view,
                databaseReference.child("dailyReport").orderByChild("date").equalTo(date)
        ) {
            @Override
            protected void populateView(final View itemView, final DailyReportFile model, int position) {
                try {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try{
                            String userName=dataSnapshot.child(model.getUid()).child("name").getValue(String.class);
                            ((TextView) itemView.findViewById(R.id.gmailSubjectLay)).setText(userName);
                            ((TextView) itemView.findViewById(R.id.gmailTitleLay)).setText(userName);
                            ((TextView) itemView.findViewById(R.id.gmailTextLay)).setText(model.getDescription());
                            ((TextView) itemView.findViewById(R.id.gmailTimeLay)).setText(model.getTime());
                            ((TextView) itemView.findViewById(R.id.gmailUidLay)).setText(model.getUid());
                            ((TextView) itemView.findViewById(R.id.gmailKeyLay)).setText(model.getPushKey());
                            }catch (Exception e){
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){
                    Log.d(TAG, "populateView: "+e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String uid = ((TextView) view.findViewById(R.id.gmailUidLay)).getText().toString();
                String key = ((TextView) view.findViewById(R.id.gmailKeyLay)).getText().toString();
                if (DESIGNATION.equals("CEO") || DESIGNATION.equals("Consultant") || TYPE.equals("admin") || firebaseUser.getUid().equals(uid)) {
                    startActivity(new Intent(ShowDailyReport.this,ViewDailyReport.class)
                            .putExtra("mUid", uid).putExtra("mPushKey", key));
                }
            }
        });
    }

    private void setMyListByUid(String userId, final String userName) {
        adapter=new FirebaseListAdapter<DailyReportFile>(
                this,
                DailyReportFile.class,
                R.layout.layout_gmail_list_view,
                databaseReference.child("dailyReport").orderByChild("uid").equalTo(userId)
        ) {
            @Override
            protected void populateView(View itemView, DailyReportFile model, int position) {
                try {
                    ((TextView) itemView.findViewById(R.id.gmailSubjectLay)).setText(userName);
                    ((TextView) itemView.findViewById(R.id.gmailTitleLay)).setText(userName);
                    ((TextView) itemView.findViewById(R.id.gmailTextLay)).setText(model.getDescription());
                    ((TextView) itemView.findViewById(R.id.gmailTimeLay)).setText(model.getTime());
                    ((TextView) itemView.findViewById(R.id.gmailDateLay)).setText(model.getDate());
                    ((TextView) itemView.findViewById(R.id.gmailUidLay)).setText(model.getUid());
                    ((TextView) itemView.findViewById(R.id.gmailKeyLay)).setText(model.getPushKey());
                }catch (Exception e){
                    Log.d(TAG, "populateView: "+e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String uid = ((TextView) view.findViewById(R.id.gmailUidLay)).getText().toString();
                String key = ((TextView) view.findViewById(R.id.gmailKeyLay)).getText().toString();
                if (DESIGNATION.equals("CEO") || DESIGNATION.equals("Consultant") || TYPE.equals("admin") || firebaseUser.getUid().equals(uid)) {
                    startActivity(new Intent(ShowDailyReport.this,ViewDailyReport.class)
                            .putExtra("mUid", uid).putExtra("mPushKey", key));
                }
            }
        });
    }
}
