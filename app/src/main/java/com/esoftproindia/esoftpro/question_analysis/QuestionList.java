package com.esoftproindia.esoftpro.question_analysis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.QuestionFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionList extends AppCompatActivity {

    public static final String TAG="QuestionList";
    private DatabaseReference databaseReference;
    private String pushKey;
    private AlertDialog.Builder ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        ListView listView = (ListView) findViewById(R.id.listViewQuestionList);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        pushKey=getIntent().getStringExtra("pushKey");
        ad = new AlertDialog.Builder(QuestionList.this);


        FirebaseListAdapter<QuestionFile> adapter = new FirebaseListAdapter<QuestionFile>(
                this,
                QuestionFile.class,
                R.layout.layout_question_list,
                databaseReference.child("question").child(pushKey)

        ) {
            @Override
            protected void populateView(View v, QuestionFile model, int position) {
                try {
                    ((TextView) v.findViewById(R.id.questionQuestionListLay)).setText(model.getQuestion());
                    Glide.with(QuestionList.this).load(model.getImg()).asBitmap().into((ImageView) v.findViewById(R.id.imgQuestionListLay));
                    ((TextView) v.findViewById(R.id.option1QuestionListLay)).setText(model.getOption1());
                    ((TextView) v.findViewById(R.id.option2QuestionListLay)).setText(model.getOption2());
                    ((TextView) v.findViewById(R.id.option3QuestionListLay)).setText(model.getOption3());
                    ((TextView) v.findViewById(R.id.option4QuestionListLay)).setText(model.getOption4());
                    ((TextView) v.findViewById(R.id.answerQuestionListLay)).setText(model.getAnswer());
                    ((TextView) v.findViewById(R.id.pushKeyQuestionListLay)).setText(model.getPushKey());
                } catch (Exception e) {
                    Log.d(TAG, "populateView: " + e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String key = ((TextView)view.findViewById(R.id.pushKeyQuestionListLay)).getText().toString();
                final String value = ((TextView)view.findViewById(R.id.questionQuestionListLay)).getText().toString();
                    ad.setTitle("Are you sure ?");
                    ad.setIcon(R.drawable.ic_delete_red_24dp);
                    ad.setMessage("Click DELETE to remove " + value + " or click cancel");
                    ad.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child("question").child(pushKey).child(key).removeValue();
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
                return true;
            }
        });

    }
}
