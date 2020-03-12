package com.esoftproindia.esoftpro.mynotification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.NotificationFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyNotification extends AppCompatActivity {

    public static final String TAG="MyNotification";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notification);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabMyNotification);
        ListView listView = (ListView) findViewById(R.id.listViewMyNotification);
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String type= sharedPreferences.getString("type", "");

        if (type.equals("employeeSpi") || type.equals("admin")) {
            fab.setVisibility(View.VISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyNotification.this,AddNotification.class));
            }
        });
        assert firebaseUser != null;
        databaseReference.child("users").child(firebaseUser.getUid()).child("alert").child("notification").setValue("False");
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("notificationAlert","False");
        editor.apply();

        FirebaseListAdapter<NotificationFile> adapter = new FirebaseListAdapter<NotificationFile>(
                this,
                NotificationFile.class,
                R.layout.layout_gmail_list_view,
                databaseReference.child("notification")
        ) {
            @Override
            protected void populateView(View itemView, NotificationFile model, int position) {
                try {
                    ((TextView) itemView.findViewById(R.id.gmailSubjectLay)).setText(model.getSubject());
                    ((TextView) itemView.findViewById(R.id.gmailTitleLay)).setText(model.getSubject());
                    ((TextView) itemView.findViewById(R.id.gmailTextLay)).setText(model.getText());
                    ((TextView) itemView.findViewById(R.id.gmailTimeLay)).setText(model.getTime());
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
                startActivity(new Intent(MyNotification.this, ViewNotification.class)
                        .putExtra("mUid", uid).putExtra("mPushKey",key));

            }
        });

    }
}