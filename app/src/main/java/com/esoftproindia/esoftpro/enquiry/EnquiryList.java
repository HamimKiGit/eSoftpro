package com.esoftproindia.esoftpro.enquiry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.EnquiryFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnquiryList extends AppCompatActivity {

    public static final String TAG="EnquiryList";
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_list);


        final ListView listView=(ListView)findViewById(R.id.listViewEnquiryList);
        final FirebaseListAdapter<EnquiryFile> adapter;

        databaseReference= FirebaseDatabase.getInstance().getReference();
        adapter=new FirebaseListAdapter<EnquiryFile>(
                this,
                EnquiryFile.class,
                R.layout.layout_gmail_list_view,
                databaseReference.child("enquiry")
        ) {
            @Override
            protected void populateView(final View itemView, final EnquiryFile model, int position) {
                databaseReference.child("users").child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            ((TextView) itemView.findViewById(R.id.gmailSubjectLay)).setText(dataSnapshot.child("name").getValue(String.class));
                            ((TextView) itemView.findViewById(R.id.gmailTitleLay)).setText(model.getUid());
                            ((TextView) itemView.findViewById(R.id.gmailTextLay)).setText(model.getNote());
                            ((TextView) itemView.findViewById(R.id.gmailTimeLay)).setText(model.getTime());
                            ((TextView) itemView.findViewById(R.id.gmailUidLay)).setText(model.getUid());
                            ((TextView) itemView.findViewById(R.id.gmailKeyLay)).setText(model.getPushKey());
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e.getMessage());
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
                String pushKey=((TextView) view.findViewById(R.id.gmailKeyLay)).getText().toString();
                String uid=((TextView) view.findViewById(R.id.gmailUidLay)).getText().toString();
                startActivity(new Intent(EnquiryList.this,ViewEnquiry.class)
                        .putExtra("pushKey",pushKey).putExtra("uid",uid));
            }
        });

    }

}
