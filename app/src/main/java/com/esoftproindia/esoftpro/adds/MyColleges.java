package com.esoftproindia.esoftpro.adds;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.MyCollegesStudent;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.AddBatchFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyColleges extends AppCompatActivity {

    public static final String TAG="MyColleges";

    private EditText collegeName;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_colleges);
        ListView listView = (ListView) findViewById(R.id.collegeList);
        collegeName=(EditText)findViewById(R.id.etCollegeName);
        ImageView imageViewAdd = (ImageView) findViewById(R.id.btnAddCollege);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=collegeName.getText().toString().trim();
                if (!collegeName.getText().toString().isEmpty()){
                    try {
                        databaseReference.child("colleges").child(name).child("name").setValue(name);
                        Toast.makeText(MyColleges.this, "Done", Toast.LENGTH_SHORT).show();
                        collegeName.setText("");
                    }catch (Exception e){
                        Toast.makeText(MyColleges.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }else{
                    Toast.makeText(MyColleges.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FirebaseListAdapter<AddBatchFile> adapter = new FirebaseListAdapter<AddBatchFile>(
                this,
                AddBatchFile.class,
                R.layout.layout_single_text,
                databaseReference.child("colleges")
        ) {
            @Override
            protected void populateView(View v, AddBatchFile model, int position) {
                ((TextView) v.findViewById(R.id.nameSingleTextLay)).setText(model.getName());
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value=((TextView)view.findViewById(R.id.nameSingleTextLay)).getText().toString();
                startActivity(new Intent(MyColleges.this, MyCollegesStudent.class).putExtra("college",value));
            }
        });
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
