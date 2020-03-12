package com.esoftproindia.esoftpro.ehisab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myadapter.DpNameStatusAdapter;
import com.esoftproindia.esoftpro.myadmin.MySearchList;
import com.esoftproindia.esoftpro.myadmin.UpdateProfileEmployee;
import com.esoftproindia.esoftpro.myadmin.UpdateProfileStudent;
import com.esoftproindia.esoftpro.myfiles.ProfileFile;
import com.esoftproindia.esoftpro.mynotification.AddNotification;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardEHisab extends AppCompatActivity {

    public static final String TAG="DashboardEHisab";

    private ArrayList<String> mUid=new ArrayList<>();
    private ArrayList<String> mName=new ArrayList<>();
    private ArrayList<String> mDp=new ArrayList<>();
    private ArrayList<String> mStatus=new ArrayList<>();

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_ehisab);

        recyclerView=(RecyclerView)findViewById(R.id.recycleViewDashBoardEHisab);
        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(1, RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);




        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("eHisab").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        final String uid = key.child("uid").getValue(String.class);
                        databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    mUid.add(uid);
                                    mName.add(dataSnapshot.child("name").getValue(String.class));
                                    mDp.add(dataSnapshot.child("dp").getValue(String.class));
                                    mStatus.add(dataSnapshot.child("alert").child("ehisabDeposit").getValue(String.class));
                                    DpNameStatusAdapter dpNameStatusAdapter = new DpNameStatusAdapter(DashboardEHisab.this, mUid, mName, mDp, mStatus);
                                    recyclerView.setAdapter(dpNameStatusAdapter);
                                }catch (Exception e){
                                    Log.d(TAG, "onDataChange: "+e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

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

}
