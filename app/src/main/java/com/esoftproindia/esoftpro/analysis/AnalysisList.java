package com.esoftproindia.esoftpro.analysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.AnalysisListFile;
import com.esoftproindia.esoftpro.question_analysis.MarksList;
import com.esoftproindia.esoftpro.question_analysis.TakeExamQuetion;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnalysisList extends AppCompatActivity {

    public static final String TAG ="AnalysisList";

    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_list);

        ListView listView = (ListView) findViewById(R.id.listViewAnalysisList);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabAnalysisList);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences=getSharedPreferences("spi",MODE_PRIVATE);
        designation=sharedPreferences.getString("designation","");
        if (designation.equals("Consultant") || designation.equals("admin")){
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnalysisList.this,AddAnalysisList.class));
            }
        });
        databaseReference.child("users").child(firebaseUser.getUid()).child("alert").child("analysis").setValue("False");
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("analysisAlert","False");
        editor.apply();

        FirebaseListAdapter<AnalysisListFile> adapter = new FirebaseListAdapter<AnalysisListFile>(
                this,
                AnalysisListFile.class,
                R.layout.layout_analysis_list,
                databaseReference.child("analysis")
        ) {
            @Override
            protected void populateView(final View v, final AnalysisListFile model, int position) {
                ((TextView) v.findViewById(R.id.nameAnalysisLay)).setText(model.getName());
                ((TextView) v.findViewById(R.id.pushKeyAnalysisLay)).setText(model.getPushKey());
                ((TextView) v.findViewById(R.id.enableAnalysisLay)).setText(model.getEnable());
                ((TextView) v.findViewById(R.id.timeAnalysisLay)).setText(model.getTime());
                try {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                ((TextView) v.findViewById(R.id.byAnalysisLay)).setText(dataSnapshot.child("users").child(model.getUid()).child("name").getValue(String.class));
                            } catch (Exception e) {
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "populateView: "+e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String enable = ((TextView) view.findViewById(R.id.enableAnalysisLay)).getText().toString();
                    String examName = ((TextView) view.findViewById(R.id.nameAnalysisLay)).getText().toString();
                    String pushKey = ((TextView) view.findViewById(R.id.pushKeyAnalysisLay)).getText().toString();
                    int time = Integer.parseInt(((TextView) view.findViewById(R.id.timeAnalysisLay)).getText().toString());

                    if (enable.equals("True") ) {
                        if (sharedPreferences.getBoolean(pushKey,true)) {
                            startActivity(new Intent(AnalysisList.this, TakeExamQuetion.class)
                                    .putExtra("pushKey", pushKey).putExtra("time", (long) time));
                            finish();
                        }else {
                            startActivity(new Intent(AnalysisList.this, MarksList.class)
                                    .putExtra("pushKey",pushKey).putExtra("examName",examName));

                        }
                    } else {
                        Toast.makeText(AnalysisList.this, "Disabled ", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(AnalysisList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pushKey = ((TextView)view.findViewById(R.id.pushKeyAnalysisLay)).getText().toString();
                String enable = ((TextView)view.findViewById(R.id.enableAnalysisLay)).getText().toString();

                if (designation.equals("Consultant") || "admin".equals(sharedPreferences.getString("type",""))){
                    startActivity(new Intent(AnalysisList.this,UpdateAnalysisList.class)
                            .putExtra("pushKey",pushKey).putExtra("enable",enable));
                }
                return true;
            }
        });
    }
}
