package com.esoftproindia.esoftpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.myadmin.UpdateProfileEmployee;
import com.esoftproindia.esoftpro.myadmin.UpdateProfileStudent;
import com.esoftproindia.esoftpro.myfiles.ProfileFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyCollegesStudent extends AppCompatActivity {

    public static final String TAG="MyCollegesStudent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_colleges_student);
        ListView listView = (ListView) findViewById(R.id.listViewCollegeStudent);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String s=getIntent().getStringExtra("college");
        Query firebaseSearchQuery = databaseReference.child("users").orderByChild("college").startAt(s).endAt(s + "\uf8ff");
        FirebaseListAdapter<ProfileFile> adapter = new FirebaseListAdapter<ProfileFile>(
                this,
                ProfileFile.class,
                R.layout.layout_list_item,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateView(View v, ProfileFile model, int position) {
                try {
                    ImageView dp = (ImageView) v.findViewById(R.id.attendanceDpLay);
                    ((TextView) v.findViewById(R.id.attendanceIdLay)).setText(model.getUid());
                    ((TextView) v.findViewById(R.id.attendanceNameLay)).setText(model.getName());
                    ((TextView) v.findViewById(R.id.attendanceStatusLay)).setText(model.getBatch());
                    ((TextView) v.findViewById(R.id.attendanceTypeLay)).setText(model.getType());

                    Glide.with(MyCollegesStudent.this).load(model.getDp()).into(dp);
                } catch (Exception e) {
                    Log.d(TAG, "populateView: "+e.getMessage());
                }

            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sId = ((TextView) view.findViewById(R.id.attendanceIdLay)).getText().toString();
                String sType = ((TextView) view.findViewById(R.id.attendanceTypeLay)).getText().toString();
                if (sType.equals("student") || sType.equals("studentSpi")){
                    startActivity(new Intent(MyCollegesStudent.this, UpdateProfileStudent.class).putExtra("uid",sId));
                }else if (sType.equals("employee") || sType.equals("employeeSpi")){
                    startActivity(new Intent(MyCollegesStudent.this, UpdateProfileEmployee.class).putExtra("uid",sId));
                }
            }
        });
    }
}
