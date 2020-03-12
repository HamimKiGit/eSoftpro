package com.esoftproindia.esoftpro.ehisab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.EhisabFile;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddWithdrawOnEmp extends AppCompatActivity {

    public static final String TAG="AddWithdrawOnEmp";

    private DatabaseReference databaseReference;

    private ArrayList<String> categoryArray = new ArrayList<>();

    private EditText subjectET;
    private EditText moneyET;
    private EditText descriptionET;
    private AutoCompleteTextView categoryAC;
    private String uid,name,subject,money,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_withdraw_on_emp);
        uid=getIntent().getStringExtra("uid");
        name=getIntent().getStringExtra("name");
        databaseReference= FirebaseDatabase.getInstance().getReference();
        EditText paytoET = (EditText) findViewById(R.id.addWithdrawOnEmpPayToET);
        subjectET=(EditText)findViewById(R.id.addWithdrawOnEmpSubjectET);
        moneyET=(EditText)findViewById(R.id.addWithdrawOnEmpMoneyET);
        descriptionET=(EditText)findViewById(R.id.addWithdrawOnEmpDescriptionET);
        categoryAC=(AutoCompleteTextView) findViewById(R.id.addWithdrawOnEmpCategoryAC);
        paytoET.setText(name);

        databaseReference.child("spentCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        categoryArray.add(key.child("name").getValue(String.class));
                    }
                    ArrayAdapter yearAdapter=new ArrayAdapter(AddWithdrawOnEmp.this,android.R.layout.simple_list_item_1,categoryArray);
                    categoryAC.setAdapter(yearAdapter);
                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void makePayment(View view) {
        GetDateTime getDateTime=new GetDateTime();
        final String TODAY=getDateTime.getDate();
        final String TIME=getDateTime.getTime();
        final String MONTH=getDateTime.getMonth();
        final String YEAR=getDateTime.getYear();
        subject = subjectET.getText().toString().trim();
        money = moneyET.getText().toString().trim();
        description = descriptionET.getText().toString().trim();
        final String categoryStr = categoryAC.getText().toString();
        final String pushKey = databaseReference.child("eHisab").child(uid).child("withdraw").push().getKey();
        if (checkPayment()) {
            Snackbar.make(view, "Pay Rs " + money + " To "+name+" ?", Snackbar.LENGTH_LONG)
                    .setAction("Pay Now", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            databaseReference.child("eHisab").child(uid).child("withdraw").child(pushKey)
                                    .setValue(new EhisabFile(subject, money, description, null, TIME, "new",pushKey,TODAY+categoryStr,MONTH+":"+YEAR+categoryStr,YEAR+categoryStr,categoryStr));
                            databaseReference.child("eHisab").child(uid).child("withdraw").child(pushKey).child("date").setValue(TODAY);
                            databaseReference.child("users").child(uid).child("alert").child("ehisabWithdraw").setValue("True");
                            databaseReference.child("spentCategory").child(categoryStr).child("name").setValue(categoryStr);
                            onBackPressed();
                        }

                    }).show();
        }
    }

    private boolean checkPayment() {
        boolean isTrue=true;
        if (subject.isEmpty()){
            subjectET.setError("Empty");
            subjectET.requestFocus();
            isTrue=false;
        }else if (money.isEmpty()){
            moneyET.setError("Empty");
            moneyET.requestFocus();
            isTrue=false;
        }else if (description.isEmpty()){
            descriptionET.setError("Empty");
            descriptionET.requestFocus();
            isTrue=false;
        }
        return isTrue;
    }
}
