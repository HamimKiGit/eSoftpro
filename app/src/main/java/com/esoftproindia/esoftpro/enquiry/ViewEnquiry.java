package com.esoftproindia.esoftpro.enquiry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewEnquiry extends AppCompatActivity {

    public static final String TAG ="ViewEnquiry";

    private ImageView dpImg;
    private TextView nameTV,timeTV,emailTV,mobileTV,descriptionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_enquiry);

        final String pushKey=getIntent().getStringExtra("pushKey");
        final String uid=getIntent().getStringExtra("uid");
        dpImg=(ImageView)findViewById(R.id.viewEnquiryDpImg);
        nameTV=(TextView)findViewById(R.id.viewEnquiryNameTV);
        timeTV=(TextView)findViewById(R.id.viewEnquiryTimeTV);
        emailTV=(TextView)findViewById(R.id.viewEnquiryEmailTV);
        mobileTV=(TextView)findViewById(R.id.viewEnquiryMobileTV);
        descriptionTV=(TextView)findViewById(R.id.viewEnquiryDescriptionTV);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        assert pushKey != null;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    assert uid != null;
                    Glide.with(ViewEnquiry.this).load(dataSnapshot.child("users").child(uid).child("dp").getValue(String.class)).asBitmap().into(dpImg);
                    nameTV.setText(dataSnapshot.child("users").child(uid).child("name").getValue(String.class));
                    emailTV.setText(dataSnapshot.child("enquiry").child(pushKey).child("email").getValue(String.class));
                    mobileTV.setText(dataSnapshot.child("enquiry").child(pushKey).child("mobile").getValue(String.class));
                    descriptionTV.setText(dataSnapshot.child("enquiry").child(pushKey).child("note").getValue(String.class));
                    String time = dataSnapshot.child("enquiry").child(pushKey).child("time").getValue(String.class);
                    String date = dataSnapshot.child("enquiry").child(pushKey).child("date").getValue(String.class);
                    timeTV.setText(String.format("%s - %s", time, date));
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });

        emailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+emailTV.getText().toString()));
                    intent.putExtra(Intent.EXTRA_SUBJECT,descriptionTV.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT,"From Softpro India :");
                    startActivity(intent);
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        mobileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mobileTV.getText().toString()));
                startActivity(intent);
            }
        });

    }
}
