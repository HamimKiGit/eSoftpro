package com.esoftproindia.esoftpro.ehisab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.EhisabFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowDepositListOnEmp extends AppCompatActivity {

    public static final String TAG="ShowDepositListOnEmp";
    private ArrayList<String> categoryArray = new ArrayList<>();

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private int depositMoney=0,withdrawMoney=0,pendingMoney=0;

    private ListView listView;
    private TextView depositTV,withdrawTV,pendingTV,todayTV,monthTV,yearTV;
    private Spinner categorySpinner;

    private String uid,DAY,MONTH,YEAR,CATEGORY;
    private int toDay,toMonth,toYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_spent_on_emp);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        uid=getIntent().getStringExtra("uid");
        databaseReference= FirebaseDatabase.getInstance().getReference();
        listView=(ListView)findViewById(R.id.listViewShowSpentOnEmp);
        depositTV=(TextView)findViewById(R.id.depositShowSpentOnEmpTV);
        withdrawTV=(TextView)findViewById(R.id.withdrawShowSpentOnEmpTV);
        pendingTV=(TextView)findViewById(R.id.pendingShowSpentOnEmpTV);
        todayTV=(TextView)findViewById(R.id.todayTVShowSpentOnEmp);
        monthTV=(TextView)findViewById(R.id.monthTVShowSpentOnEmp);
        yearTV=(TextView)findViewById(R.id.yearTVShowSpentOnEmp);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabShowSpentOnEmp);

        categorySpinner=(Spinner)findViewById(R.id.categorySpinnerShowSpentOnEmp);

        if (uid.equals(firebaseUser.getUid())){
            fab.setVisibility(View.VISIBLE);
        }
        GetDateTime getDateTime=new GetDateTime();
        DAY=getDateTime.getDay();
        MONTH=getDateTime.getMonth();
        YEAR=getDateTime.getYear();
        toYear= Integer.parseInt(getDateTime.getYear());
        toMonth= Integer.parseInt(getDateTime.getMonth())-1;
        toDay= Integer.parseInt(getDateTime.getDay());
        todayTV.setText(DAY);
        monthTV.setText(MONTH);
        yearTV.setText(YEAR);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    CATEGORY="";
                }else {
                    CATEGORY = adapterView.getSelectedItem().toString();
                }
                Query query1 = databaseReference.child("eHisab").child(uid).child("deposit").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                Query query2 = databaseReference.child("eHisab").child(uid).child("withdraw").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                setMyListView(query1,query2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Query query1 = databaseReference.child("eHisab").child(uid).child("deposit").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
        Query query2 = databaseReference.child("eHisab").child(uid).child("withdraw").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
        setMyListView(query1,query2);

        categorySpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ShowDepositListOnEmp.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            toYear = i;
                            toMonth = i1 + 1;
                            toDay = i2;
                            if (toDay<10){
                                DAY="0"+toDay;
                            }else {
                                DAY= String.valueOf(toDay);
                            }
                            if (toMonth<10){
                                MONTH="0"+toMonth;
                            }else {
                                MONTH= String.valueOf(toMonth);
                            }
                            YEAR= String.valueOf(toYear);

                            todayTV.setText(DAY);
                            monthTV.setText(MONTH);
                            yearTV.setText(YEAR);

                            Query query1 = databaseReference.child("eHisab").child(uid).child("deposit").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                            Query query2 = databaseReference.child("eHisab").child(uid).child("withdraw").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                            setMyListView(query1,query2);
                        }
                    }, toYear, toMonth, toDay);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Toast.makeText(ShowDepositListOnEmp.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        todayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query1 = databaseReference.child("eHisab").child(uid).child("deposit").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                Query query2 = databaseReference.child("eHisab").child(uid).child("withdraw").orderByChild("searchDate").startAt(DAY+":"+MONTH+":"+YEAR+CATEGORY).endAt(DAY+":"+MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                setMyListView(query1,query2);
            }
        });
        monthTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query1 = databaseReference.child("eHisab").child(uid).child("deposit").orderByChild("searchMonth").startAt(MONTH+":"+YEAR+CATEGORY).endAt(MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                Query query2 = databaseReference.child("eHisab").child(uid).child("withdraw").orderByChild("searchMonth").startAt(MONTH+":"+YEAR+CATEGORY).endAt(MONTH+":"+YEAR+CATEGORY+"\uf8ff");
                setMyListView(query1,query2);
            }
        });
        yearTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query1 = databaseReference.child("eHisab").child(uid).child("deposit").orderByChild("searchYear").startAt(YEAR+CATEGORY).endAt(YEAR+CATEGORY+"\uf8ff");
                Query query2 = databaseReference.child("eHisab").child(uid).child("withdraw").orderByChild("searchYear").startAt(YEAR+CATEGORY).endAt(YEAR+CATEGORY+"\uf8ff");
                setMyListView(query1,query2);
            }
        });

        pendingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pendingMoney=depositMoney-withdrawMoney;
                pendingTV.setText(String.valueOf(pendingMoney));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowDepositListOnEmp.this,AddDepositOnEmp.class));
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(ShowDepositListOnEmp.this,ShowWithdrawListOnEmp.class).putExtra("uid",uid));
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pushKey=((TextView) view.findViewById(R.id.keyEhisabListLay)).getText().toString();
                if (!uid.equals(firebaseUser.getUid())){
                    try{
                        databaseReference.child("eHisab").child(uid).child("deposit").child(pushKey).child("status").removeValue();
                    }catch (Exception e){
                        Log.d(TAG, "onItemClick: "+e.getMessage());
                    }
                }
                startActivity(new Intent(ShowDepositListOnEmp.this,ShowEHisabOnEmp.class)
                        .putExtra("uid",uid).putExtra("pushKey",pushKey).putExtra("action","deposit"));

            }
        });

        databaseReference.child("spentCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    categoryArray.add("");
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        categoryArray.add(key.child("name").getValue(String.class));
                    }
                    ArrayAdapter yearAdapter=new ArrayAdapter(ShowDepositListOnEmp.this,android.R.layout.simple_list_item_1,categoryArray);
                    categorySpinner.setAdapter(yearAdapter);
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(firebaseUser.getUid()).child("alert").child("ehisab").setValue("False");

    }

    private void setMyListView(Query query1,Query query2) {
        //                    if (model.getStatus() != null) {
        //                        ((TextView) v.findViewById(R.id.statusHisabLay)).setText(model.getStatus());
        //                    }else {
        //                        ((TextView) v.findViewById(R.id.statusHisabLay)).setVisibility(View.GONE);
        //                    }
        FirebaseListAdapter<EhisabFile> adapter = new FirebaseListAdapter<EhisabFile>(
                this,
                EhisabFile.class,
                R.layout.layout_ehisab_list,
                query1
        ) {
            @Override
            protected void populateView(View v, EhisabFile model, int position) {
                try {
                    ((TextView) v.findViewById(R.id.subjectEhisabListLay)).setText(model.getSubject());
                    ((TextView) v.findViewById(R.id.descriptionEhisabListLay)).setText(model.getDescription());
                    ((TextView) v.findViewById(R.id.rupeesEhisabListLay)).setText(model.getMoney());
                    ((TextView) v.findViewById(R.id.categoryEhisabListLay)).setText(model.getCategory());
                    ((TextView) v.findViewById(R.id.timeEhisabListLay)).setText(String.format("%s,%s", model.getTime(), model.getDate()));
                    ((TextView) v.findViewById(R.id.keyEhisabListLay)).setText(model.getPushKey());
//                    if (model.getStatus() != null) {
//                        ((TextView) v.findViewById(R.id.statusHisabLay)).setText(model.getStatus());
//                    }else {
//                        ((TextView) v.findViewById(R.id.statusHisabLay)).setVisibility(View.GONE);
//                    }
                } catch (Exception e) {
                    Log.d(TAG, "populateView: " + e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        setMyValue(query1,query2);
    }

    private void setMyValue(Query query1,Query query2) {
        depositMoney=0;
        withdrawMoney=0;
        pendingMoney=0;
        pendingTV.setText("?");
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        for (DataSnapshot key : dataSnapshot.getChildren()) {
                            String with = key.child("money").getValue(String.class);
                            assert with != null;
                            withdrawMoney += Integer.parseInt(with);
                            withdrawTV.setText(String.valueOf(withdrawMoney));
                        }
                    }else {
                        withdrawTV.setText(String.valueOf(withdrawMoney));
                    }
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        for (DataSnapshot key : dataSnapshot.getChildren()) {
                            String with = key.child("money").getValue(String.class);
                            assert with != null;
                            depositMoney += Integer.parseInt(with);
                            depositTV.setText(String.valueOf(depositMoney));
                        }
                    }else {
                        depositTV.setText(String.valueOf(depositMoney));
                    }
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
