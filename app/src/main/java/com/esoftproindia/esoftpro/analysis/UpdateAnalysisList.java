package com.esoftproindia.esoftpro.analysis;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.AnalysisListFile;
import com.esoftproindia.esoftpro.myfiles.QuestionFile;
import com.esoftproindia.esoftpro.question_analysis.AddNewQuestionAnalysis;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateAnalysisList extends AppCompatActivity {

    public static final String TAG="UpdateAnalysisList";

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private EditText testName,question,timeET;
    private String pushKey;
    private int questionListNo,questionNo;
    private AlertDialog.Builder ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_analysis_list);
        testName=(EditText)findViewById(R.id.tNameUpdateAnalysisList);
        question=(EditText)findViewById(R.id.qNumberUpdateAnalysisList);
        timeET=(EditText)findViewById(R.id.timeUpdateAnalysisList);
        ListView listView = (ListView) findViewById(R.id.listViewUpdateAnalysisList);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        pushKey=getIntent().getStringExtra("pushKey");
        ad = new AlertDialog.Builder(UpdateAnalysisList.this);



        databaseReference.child("analysis").child(pushKey).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    testName.setText(dataSnapshot.child("name").getValue(String.class));
                    timeET.setText(dataSnapshot.child("time").getValue(String.class));
                    questionNo = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("qNum").getValue(String.class)));
                    question.setText(String.valueOf(questionNo));
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("question").child(pushKey).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    questionListNo = (int) dataSnapshot.getChildrenCount();
                    Objects.requireNonNull(getSupportActionBar()).setTitle(String.valueOf(questionListNo).concat("/").concat(String.valueOf(questionNo).concat(" Question Left")));
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+ databaseError.getDetails());
            }
        });

        FirebaseListAdapter<QuestionFile> adapter = new FirebaseListAdapter<QuestionFile>(
                this,
                QuestionFile.class,
                R.layout.layout_question_list,
                databaseReference.child("question").child(pushKey)

        ) {
            @Override
            protected void populateView(View v, QuestionFile model, int position) {
                ((TextView) v.findViewById(R.id.questionQuestionListLay)).setText(model.getQuestion());
                Glide.with(UpdateAnalysisList.this).load(model.getImg()).asBitmap().into((ImageView) v.findViewById(R.id.imgQuestionListLay));
                ((TextView) v.findViewById(R.id.option1QuestionListLay)).setText(model.getOption1());
                ((TextView) v.findViewById(R.id.option2QuestionListLay)).setText(model.getOption2());
                ((TextView) v.findViewById(R.id.option3QuestionListLay)).setText(model.getOption3());
                ((TextView) v.findViewById(R.id.option4QuestionListLay)).setText(model.getOption4());
                ((TextView) v.findViewById(R.id.answerQuestionListLay)).setText(model.getAnswer());
                ((TextView) v.findViewById(R.id.pushKeyQuestionListLay)).setText(model.getPushKey());
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String mPushKey = ((TextView)view.findViewById(R.id.pushKeyQuestionListLay)).getText().toString();
                final String qName = ((TextView)view.findViewById(R.id.questionQuestionListLay)).getText().toString();
                ad.setTitle("Are you sure ?");
                ad.setIcon(R.drawable.ic_delete_red_24dp);
                ad.setMessage("Click DELETE to remove " + qName + " or click cancel");
                ad.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("question").child(pushKey).child(mPushKey).removeValue();
                    }
                });
                ad.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                ad.setCancelable(false);
                ad.show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_analysis_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.menuLockOutlineUpdateAnalysisList){
            try {
                databaseReference.child("analysis").child(pushKey).child("enable").setValue("False");
                onBackPressed();
                Toast.makeText(UpdateAnalysisList.this, "Disabled", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
            }
        }else if (id == R.id.menuLockOpenUpdateAnalysisList){
            try {
                databaseReference.child("analysis").child(pushKey).child("enable").setValue("True");
                onBackPressed();
                Toast.makeText(UpdateAnalysisList.this, "Enable", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
            }
        }else if (id == R.id.menuUpdateUpdateAnalysisList){
            try {
                String name = testName.getText().toString().trim();
                String qNum = question.getText().toString().trim();
                String time = timeET.getText().toString().trim();
                if (testName.getText().toString().isEmpty()) {
                    testName.setError("Empty");
                    testName.requestFocus();
                } else {
                    if (question.getText().toString().isEmpty()) {
                        question.setError("Empty");
                        question.requestFocus();
                    } else {
                        databaseReference.child("analysis").child(pushKey).setValue(new AnalysisListFile(name, qNum, firebaseUser.getUid(), pushKey, "False", time));
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }catch (Exception e){
                Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
            }
        }else if (id == R.id.menuAddUpdateAnalysisList){
            try {
                if (questionListNo < questionNo) {
                    startActivity(new Intent(UpdateAnalysisList.this, AddNewQuestionAnalysis.class)
                            .putExtra("pushKey", pushKey).putExtra("qList", questionListNo).putExtra("qNo", questionNo));
                } else {
                    Toast.makeText(this, "Question Full", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
