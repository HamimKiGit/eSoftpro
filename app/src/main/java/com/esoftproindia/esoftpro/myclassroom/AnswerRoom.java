package com.esoftproindia.esoftpro.myclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.QuestionRomFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AnswerRoom extends AppCompatActivity {
    public static final String TAG="AnswerRoom";

    private EditText editText;
    private ImageView downloadImg,sendImg;
    private Dialog dialog;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String fileUrl="",pushKey,className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_room);
        ListView listView = (ListView) findViewById(R.id.listViewAnswerRoom);
        editText=(EditText)findViewById(R.id.answerETAnswerRoom);
        downloadImg=(ImageView)findViewById(R.id.downloadAnswerRoom);
        sendImg=(ImageView)findViewById(R.id.sendAnswerRoom);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        fileUrl=getIntent().getStringExtra("fileUrl");
        pushKey=getIntent().getStringExtra("pushKey");
        className=getIntent().getStringExtra("className");
        dialog =new Dialog(this);
        dialog.setContentView(R.layout.layout_profile_view);

        FirebaseListAdapter<QuestionRomFile> adapter = new FirebaseListAdapter<QuestionRomFile>(
                this,
                QuestionRomFile.class,
                R.layout.layout_chat_bubble,
                databaseReference.child("answerRoom").child(className).child(pushKey)
        ) {
            @Override
            protected void populateView(final View v, final QuestionRomFile model, int position) {
                try {
                    ((TextView) v.findViewById(R.id.messageIncomingChatBubble)).setText(model.getDescription());
                    ((TextView) v.findViewById(R.id.uidIncomingChatBubble)).setText(model.getUid());
                    ((TextView) v.findViewById(R.id.timeIncomingChatBubble)).setText(model.getDate());
                } catch (Exception e) {
                    Log.d(TAG, "populateView: "+e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String uid = ((TextView) view.findViewById(R.id.uidIncomingChatBubble)).getText().toString();
                String date = ((TextView) view.findViewById(R.id.timeIncomingChatBubble)).getText().toString();
                ((TextView) dialog.findViewById(R.id.profileViewTimeLay)).setText(date);
                databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {

                            TextView nameTV = (TextView) dialog.findViewById(R.id.profileViewNameLay);
                            TextView collegeTv = (TextView) dialog.findViewById(R.id.profileViewCollegeLay);
                            ImageView imgDp = (ImageView) dialog.findViewById(R.id.profileViewDpLay);
                            nameTV.setText(dataSnapshot.child("name").getValue(String.class));
                            collegeTv.setText(dataSnapshot.child("college").getValue(String.class));
                            Glide.with(AnswerRoom.this).load(dataSnapshot.child("dp").getValue(String.class)).asBitmap().into(imgDp);

                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                    }
                });

                dialog.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                return true;
            }
        });
        if (fileUrl.length() >10){
            sendImg.setVisibility(View.GONE);
            downloadImg.setVisibility(View.VISIBLE);
        }else {
            downloadImg.setVisibility(View.GONE);
            sendImg.setVisibility(View.VISIBLE);
        }
        downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl)));
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reply=editText.getText().toString().trim();
                if (reply.isEmpty()){
                    editText.setError("Empty");
                    editText.requestFocus();
                }else {

                    String mPushKey = databaseReference.child("answerRoom").push().getKey();
                    databaseReference.child("answerRoom").child(className).child(pushKey).child(mPushKey).setValue(new QuestionRomFile(firebaseUser.getUid(),new GetDateTime().getTimeDate(),reply,mPushKey));
                    editText.setText("");
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().toString().length()>0){
                    sendImg.setVisibility(View.VISIBLE);
                    downloadImg.setVisibility(View.GONE);
                }else {
                    if (fileUrl.length() >10){
                        sendImg.setVisibility(View.GONE);
                        downloadImg.setVisibility(View.VISIBLE);
                    }else {
                        downloadImg.setVisibility(View.GONE);
                        sendImg.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

}
