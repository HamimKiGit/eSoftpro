package com.esoftproindia.esoftpro.myadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.ProfileFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MySearchList extends AppCompatActivity {

    public static final String TAG="MySearchList";

    private ListView listView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_search_list);
        EditText friendName = (EditText) findViewById(R.id.searchFriendName);
        listView = (ListView) findViewById(R.id.searchList);
        databaseReference= FirebaseDatabase.getInstance().getReference();

        friendName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                displayChatMessage(v.getText().toString());
                return true;
            }
        });

        friendName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                displayChatMessage(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void displayChatMessage(String s) {
        try {
            Query firebaseSearchQuery = databaseReference.child("users").orderByChild("name").startAt(s).endAt(s + "\uf8ff");
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

                        Glide.with(MySearchList.this).load(model.getDp()).into(dp);

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
                        startActivity(new Intent(MySearchList.this, UpdateProfileStudent.class).putExtra("uid",sId));
                    }else if (sType.equals("employee") || sType.equals("employeeSpi")){
                        startActivity(new Intent(MySearchList.this, UpdateProfileEmployee.class).putExtra("uid",sId));
                    }
                }
            });
        }catch (Exception e){
            Log.d(TAG, "displayChatMessage: "+e.getMessage());
        }
    }

}
