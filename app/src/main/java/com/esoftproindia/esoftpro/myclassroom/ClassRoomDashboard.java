package com.esoftproindia.esoftpro.myclassroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.AddBatchFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ClassRoomDashboard extends AppCompatActivity {

    public static final String TAG="ClassRoomDashboard";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_room_dashboard);

        ListView listView = (ListView) findViewById(R.id.listViewClassRoomDashboard);
        databaseReference= FirebaseDatabase.getInstance().getReference();

        FirebaseListAdapter<AddBatchFile> adapter = new FirebaseListAdapter<AddBatchFile>(
                this,
                AddBatchFile.class,
                R.layout.layout_batch_dashboard,
                databaseReference.child("batch")
        ) {
            @Override
            protected void populateView(final View v, final AddBatchFile model, int position) {
                try {
                    ((TextView) v.findViewById(R.id.nameTvBatchDashboardLay)).setText(model.getName());

                    Query firebaseSearchQuery = databaseReference.child("users").orderByChild("batch").startAt(model.getName()).endAt(model.getName() + "\uf8ff");
                    firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                ((TextView) v.findViewById(R.id.countTvBatchDashboardLay)).setText(String.valueOf(dataSnapshot.getChildrenCount()));
                            }catch (Exception e){
                                Log.d(TAG, "onDataChange: "+e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: "+databaseError.getMessage());
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
                String className = ((TextView)view.findViewById(R.id.nameTvBatchDashboardLay)).getText().toString();
                startActivity(new Intent(ClassRoomDashboard.this,QuestionRoom.class).putExtra("className",className));
            }
        });

    }
}
