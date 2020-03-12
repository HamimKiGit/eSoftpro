package com.esoftproindia.esoftpro.panel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.InfoActivity;
import com.esoftproindia.esoftpro.LoginActivity;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.analysis.AnalysisList;
import com.esoftproindia.esoftpro.myatnds.MyAttendance;
import com.esoftproindia.esoftpro.myclassroom.ClassRoomDashboard;
import com.esoftproindia.esoftpro.myclassroom.QuestionRoom;
import com.esoftproindia.esoftpro.myemp.MyEmployeeTab;
import com.esoftproindia.esoftpro.mynotification.MyNotification;
import com.esoftproindia.esoftpro.profiles.ProfileStudent;
import com.esoftproindia.esoftpro.status.ViewStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class StudentPanel extends AppCompatActivity {

    public static final String TAG="StudentPanel";

    private Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private String enrollment;
    private QRGEncoder qrgEncoder;
    private View attendanceV;
    private View notificationV;
    private View classroomV;
    private View analysisV;
    private View employeeV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_panel);

        attendanceV=(View)findViewById(R.id.attendanceViewStdPanel);
        notificationV = (View) findViewById(R.id.notificationViewStdPanel);
        classroomV = (View) findViewById(R.id.classroomViewStdPanel);
        analysisV = (View) findViewById(R.id.analysisViewStdPanel);
        employeeV = (View) findViewById(R.id.teamViewStdPanel);

        sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        enrollment=sharedPreferences.getString("enrollment", "Invalid");
        qrgEncoder = new QRGEncoder(enrollment, null, QRGContents.Type.TEXT, 600);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    dialog = new Dialog(StudentPanel.this);
                    dialog.setContentView(R.layout.popup_image_view);
                    ImageView qrImageView = (ImageView) dialog.findViewById(R.id.imagePopupImageView);
                    String firebaseEnrollment = dataSnapshot.child("attendance").child(new GetDateTime().getDate()).child(enrollment).child("enrollment").getValue(String.class);
                    if (firebaseEnrollment != null) {
                        qrImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_100dp));
                        attendanceV.setVisibility(View.GONE);

                    }else {
                        try {
                            qrImageView.setImageBitmap(qrgEncoder.encodeAsBitmap());
                            attendanceV.setVisibility(View.VISIBLE);
                        } catch (WriterException e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                    }
                    setNotification();

                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });

    }

    private void setNotification() {
        if (sharedPreferences.getString("notificationAlert","").equals("True")){
            notificationV.setVisibility(View.VISIBLE);
        }else {
            notificationV.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString("classroomAlert","").equals("True")){
            classroomV.setVisibility(View.VISIBLE);
        }else {
            classroomV.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString("analysisAlert","").equals("True")){
            analysisV.setVisibility(View.VISIBLE);
        }else {
            analysisV.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString("employeeAlert","").equals("True")){
            employeeV.setVisibility(View.VISIBLE);
        }else {
            employeeV.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuProfileStudent) {
            startActivity(new Intent(StudentPanel.this, ProfileStudent.class));
        }else if (id == R.id.menuLogoutStudent) {
            firebaseAuth.signOut();
            startActivity(new Intent(StudentPanel.this, LoginActivity.class));
            ActivityCompat.finishAffinity(StudentPanel.this);
        }else if (id == R.id.menuInfoStudent) {
            firebaseAuth.signOut();
            startActivity(new Intent(StudentPanel.this, InfoActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void AttendanceStdPanel(View view) {
        dialog.show();
    }

    public void analysisStdPanel(View view) {
        startActivity(new Intent(StudentPanel.this, AnalysisList.class));
    }

    public void classroomStdPanel(View view) {
        startActivity(new Intent(StudentPanel.this, QuestionRoom.class).putExtra("className",sharedPreferences.getString("batch","")));
    }

    public void facebookStdPanel(View view) {
        startActivity(new Intent(StudentPanel.this, ViewStatus.class));
    }

    public void employeeStdPanel(View view) {
        startActivity(new Intent(StudentPanel.this, MyEmployeeTab.class));
    }

    public void notificationStdPanel(View view) {
        startActivity(new Intent(StudentPanel.this, MyNotification.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotification();
    }
}