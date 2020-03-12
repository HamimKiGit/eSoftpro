package com.esoftproindia.esoftpro.question_analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.QuestionFile;
import com.esoftproindia.esoftpro.profiles.EditProfileStudent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddNewQuestionAnalysis extends AppCompatActivity {

    public static final String TAG="AddNewQuestionAnalysis";

    private String key,pushKey;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private EditText questionET,op1ET,op2ET,op3ET,op4ET;
    private Button button;
    private TextView textView;
    private Spinner spinner;
    private String []ansOp={"SELECT","1","2","3","4"};
    private String question,op1,op2,op3,op4,ans,downloadUrl=null;
    private ImageView imageView;
    private static final int ChooseImg=10;
    private int qList,qNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_question_analysis);
        key=getIntent().getStringExtra("pushKey");
        qList=getIntent().getIntExtra("qList",0);
        qNo=getIntent().getIntExtra("qNo",0);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        questionET=(EditText)findViewById(R.id.questionAddQAnalysis);
        op1ET=(EditText)findViewById(R.id.qNumber1AddAnalysisList);
        op2ET=(EditText)findViewById(R.id.qNumber2AddAnalysisList);
        op3ET=(EditText)findViewById(R.id.qNumber3AddAnalysisList);
        op4ET=(EditText)findViewById(R.id.qNumber4AddAnalysisList);
        spinner=(Spinner)findViewById(R.id.answerAddAnalysisList);
        imageView=(ImageView) findViewById(R.id.imageAddQAnalysis);
        textView=(TextView) findViewById(R.id.imageAddQAnalysisTV);
        button=(Button) findViewById(R.id.buttonAddQAnalysis);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,ansOp);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ans=spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,ChooseImg);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qList<qNo) {
                    qList++;
                    question = questionET.getText().toString();
                    op1 = op1ET.getText().toString().trim();
                    op2 = op2ET.getText().toString().trim();
                    op3 = op3ET.getText().toString().trim();
                    op4 = op4ET.getText().toString().trim();

                    if (check()) {
                        try {
                            pushKey = databaseReference.child("question").child(key).push().getKey();
                            databaseReference.child("question").child(key).child(pushKey).setValue(new QuestionFile(question, downloadUrl, op1, op2, op3, op4, ans, new GetDateTime().getTimeDate(), firebaseUser.getUid(), pushKey));
                            questionET.setText("");
                            op1ET.setText("");
                            op2ET.setText("");
                            op3ET.setText("");
                            op4ET.setText("");
                            questionET.requestFocus();
                            questionET.setHint(String.valueOf(qList).concat("/").concat(String.valueOf(qNo)));
                        } catch (Exception e) {
                            Log.d(TAG, "onClick: "+e.getMessage());
                        }
                    }
                }else {
                    Toast.makeText(AddNewQuestionAnalysis.this, "Full Number of Question", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooseImg && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                setMyFilePath(uri);
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }

    private void setMyFilePath(final Uri uri) {
        final StorageReference filepath=storageReference.child("analysis/"+pushKey+"/").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl= String.valueOf(uri);
                                    button.setClickable(true);

                                }
                            });
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewQuestionAnalysis.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                textView.setText("Uploaded");
                button.setClickable(false);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double pro=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                textView.setText(String.valueOf(pro).concat("%"));
                button.setClickable(false);

            }
        });
    }

    private boolean check() {
        boolean isTrue=true;
        if (question.isEmpty()){
            questionET.setError("Empty");
            questionET.requestFocus();
            isTrue=false;
        }else if (op1.isEmpty()){
            op1ET.setError("Empty");
            op1ET.requestFocus();
            isTrue=false;
        }else if (op2.isEmpty()){
            op2ET.setError("Empty");
            op2ET.requestFocus();
            isTrue=false;
        }else if (op3.isEmpty()){
            op3ET.setError("Empty");
            op3ET.requestFocus();
            isTrue=false;
        }else if (op4.isEmpty()){
            op4ET.setError("Empty");
            op4ET.requestFocus();
            isTrue=false;
        }else if (ans.equals("SELECT")){
            Toast.makeText(this, "Select Option", Toast.LENGTH_SHORT).show();
            isTrue=false;
        }
        return isTrue;
    }
}
