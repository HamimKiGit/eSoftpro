package com.esoftproindia.esoftpro.adds;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.MyBatchStudent;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.AddBatchFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyBatches extends AppCompatActivity {

    public static final String TAG="MyBatches";

    private EditText batchName;
    private DatabaseReference databaseReference;
    private AlertDialog.Builder ad;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_batches);

        ListView listView = (ListView) findViewById(R.id.batchList);
        batchName=(EditText)findViewById(R.id.etBatchName);
        ImageView imageViewAdd = (ImageView) findViewById(R.id.btnAddBatch);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        sharedPreferences=getSharedPreferences("spi",MODE_PRIVATE);
        ad = new AlertDialog.Builder(MyBatches.this);

        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=batchName.getText().toString().trim();
                if (!batchName.getText().toString().isEmpty()){
                    try {
                        databaseReference.child("batch").child(name).child("name").setValue(name);
                        Toast.makeText(MyBatches.this, "Done", Toast.LENGTH_SHORT).show();
                        batchName.setText("");
                    }catch (Exception e){
                        Toast.makeText(MyBatches.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }else{
                    Toast.makeText(MyBatches.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseListAdapter<AddBatchFile> adapter = new FirebaseListAdapter<AddBatchFile>(
                this,
                AddBatchFile.class,
                R.layout.layout_single_text,
                databaseReference.child("batch")
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
                startActivity(new Intent(MyBatches.this, MyBatchStudent.class).putExtra("batch",value));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ("HR".equals(sharedPreferences.getString("designation","")) && "admin".equals(sharedPreferences.getString("type",""))) {
                    final String value = ((TextView) view.findViewById(R.id.nameSingleTextLay)).getText().toString();
                    ad.setTitle("Are you sure ?");
                    ad.setIcon(R.drawable.ic_delete_red_24dp);
                    ad.setMessage("Click DELETE to remove " + value + " or click cancel");
                    ad.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child("batch").child(value).removeValue();
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
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
