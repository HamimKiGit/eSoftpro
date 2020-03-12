package com.esoftproindia.esoftpro.myclassroom;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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
import com.esoftproindia.esoftpro.myfiles.QuestionRomFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class QuestionRoom extends AppCompatActivity {

    public static final String TAG="QuestionRoom";

    private DatabaseReference databaseReference;

    private String className;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_room);
        ListView listView = (ListView) findViewById(R.id.listViewQuestionRoom);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabQuestionRoom);
        className=getIntent().getStringExtra("className");
        Objects.requireNonNull(getSupportActionBar()).setTitle(className);

        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        databaseReference.child("users").child(firebaseUser.getUid()).child("alert").child("classroom").setValue("False");
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("classroomAlert","False");
        editor.apply();

        if ("Consultant".equals(sharedPreferences.getString("designation",""))){
            fab.setVisibility(View.VISIBLE);
        }

        FirebaseListAdapter<QuestionRomFile> adapter = new FirebaseListAdapter<QuestionRomFile>(
                this,
                QuestionRomFile.class,
                R.layout.layout_news_feed,
                databaseReference.child("questionRoom").child(className)
        ) {
            @Override
            protected void populateView(final View v, final QuestionRomFile model, int position) {
                try {

                    databaseReference.child("users").child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                ((TextView) v.findViewById(R.id.nfNameLay)).setText(dataSnapshot.child("name").getValue(String.class));
                                Glide.with(QuestionRoom.this).load(dataSnapshot.child("dp").getValue(String.class)).asBitmap().into((ImageView) v.findViewById(R.id.nfDpLay));

                            } catch (Exception e) {
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                        }
                    });
                    ((TextView) v.findViewById(R.id.nfDateLay)).setText(model.getDate());
                    ((TextView) v.findViewById(R.id.nfTextViewLay)).setText(model.getDescription());
                    Glide.with(QuestionRoom.this).load(model.getFile()).asBitmap().into((ImageView) v.findViewById(R.id.nfImageViewLay));
                    ((TextView) v.findViewById(R.id.nfPushKeyLay)).setText(model.getPushKey());
                    ((TextView) v.findViewById(R.id.nfIdLay)).setText(model.getUid());
                    ((TextView) v.findViewById(R.id.nfFileUrlLay)).setText(model.getFile());

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
                String pushKey = ((TextView)view.findViewById(R.id.nfPushKeyLay)).getText().toString();
                String fileUrl = ((TextView) view.findViewById(R.id.nfFileUrlLay)).getText().toString();
                startActivity(new Intent(QuestionRoom.this,AnswerRoom.class)
                        .putExtra("pushKey",pushKey).putExtra("fileUrl",fileUrl).putExtra("className",className));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String fileUrl = ((TextView) view.findViewById(R.id.nfFileUrlLay)).getText().toString();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl)));
                }catch (Exception e){
                    Log.d(TAG, "onItemLongClick: "+e.getMessage());
                }

                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuestionRoom.this,AddQuestionRoom.class).putExtra("className",className));
            }
        });
    }
}
