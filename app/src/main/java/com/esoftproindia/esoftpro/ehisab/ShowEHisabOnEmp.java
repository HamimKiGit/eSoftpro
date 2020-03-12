package com.esoftproindia.esoftpro.ehisab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowEHisabOnEmp extends AppCompatActivity {

    private TextView subjectET,moneyET,descriptionET,timeET,categoryTV;
    private ImageView imageView;

    public static final String TAG="ShowEHisabOnEmp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ehisab_on_emp);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        subjectET=(TextView)findViewById(R.id.showHisabOnEmpSubjectTV);
        moneyET=(TextView)findViewById(R.id.showHisabOnEmpMoneyTV);
        timeET=(TextView)findViewById(R.id.showHisabOnEmpTimeTV);
        categoryTV=(TextView)findViewById(R.id.showHisabOnEmpCategoryTV);
        descriptionET=(TextView)findViewById(R.id.showHisabOnEmpDescriptionTV);
        imageView=(ImageView) findViewById(R.id.showHisabOnEmpImgeView);
        String pushKey = getIntent().getStringExtra("pushKey");
        String action = getIntent().getStringExtra("action");
        String uid = getIntent().getStringExtra("uid");
        databaseReference.child("eHisab").child(uid).child(action).child(pushKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String receipt = dataSnapshot.child("receipt").getValue(String.class);
                    subjectET.setText(dataSnapshot.child("subject").getValue(String.class));
                    moneyET.setText(dataSnapshot.child("money").getValue(String.class));
                    descriptionET.setText(dataSnapshot.child("description").getValue(String.class));
                    categoryTV.setText(dataSnapshot.child("category").getValue(String.class));
                    String time = dataSnapshot.child("time").getValue(String.class);
                    String date = dataSnapshot.child("date").getValue(String.class);
                    timeET.setText(String.format("%s,%s", time, date));
                    Glide.with(ShowEHisabOnEmp.this).load(receipt).asBitmap().into(imageView);
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
