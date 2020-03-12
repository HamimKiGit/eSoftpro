package com.esoftproindia.esoftpro.panel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.esoftproindia.esoftpro.GetDateTime;
import com.esoftproindia.esoftpro.InfoActivity;
import com.esoftproindia.esoftpro.LoginActivity;
import com.esoftproindia.esoftpro.MapsActivity;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.adds.MyBatches;
import com.esoftproindia.esoftpro.adds.MyColleges;
import com.esoftproindia.esoftpro.analysis.AnalysisList;
import com.esoftproindia.esoftpro.certificates.CertificatesActivity;
import com.esoftproindia.esoftpro.dailyreport.AddDailyReport;
import com.esoftproindia.esoftpro.dailyreport.ShowDailyReport;
import com.esoftproindia.esoftpro.ehisab.DashboardEHisab;
import com.esoftproindia.esoftpro.ehisab.ShowDepositListOnEmp;
import com.esoftproindia.esoftpro.enquiry.EnquiryList;
import com.esoftproindia.esoftpro.myadmin.MySearchList;
import com.esoftproindia.esoftpro.myatnds.TakeAttendance;
import com.esoftproindia.esoftpro.myclassroom.ClassRoomDashboard;
import com.esoftproindia.esoftpro.myemp.MyEmployeeTab;
import com.esoftproindia.esoftpro.mynotification.MyNotification;
import com.esoftproindia.esoftpro.profiles.ProfileEmployee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class EmployeePanel extends AppCompatActivity {

    public static final String TAG="EmployeePanel";

    private Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private QRGEncoder qrgEncoder;
    private String enrollment,designation,type;
    private ImageView qrImageView;
    private View attendanceV;
    private View notificationV;
    private View classroomV;
    private View ehisabV;
    private View analysisV;
    private View dailyReportV;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_panel);
        attendanceV=(View)findViewById(R.id.attendanceViewEmpPanel);
        notificationV = (View) findViewById(R.id.notificationViewEmpPanel);
        classroomV = (View) findViewById(R.id.classroomViewEmpPanel);
        ehisabV = (View) findViewById(R.id.ehisabViewEmpPanel);
        analysisV = (View) findViewById(R.id.analysisViewEmpPanel);
        dailyReportV = (View) findViewById(R.id.dailyReportViewEmpPanel);
        sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        enrollment= sharedPreferences.getString("enrollment", "");
        designation= sharedPreferences.getString("designation", "admin");
        type= sharedPreferences.getString("type", "");
        qrgEncoder = new QRGEncoder(enrollment, null, QRGContents.Type.TEXT, 600);
        dialog = new Dialog(EmployeePanel.this);
        dialog.setContentView(R.layout.popup_image_view);
        qrImageView = (ImageView) dialog.findViewById(R.id.imagePopupImageView);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String firebaseEnrollment = dataSnapshot.child("attendance").child(new GetDateTime().getDate()).child(enrollment).child("enrollment").getValue(String.class);
                    if (firebaseEnrollment != null) {
                        qrImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_green_100dp));
                        attendanceV.setVisibility(View.GONE);
                    }else {
                        try {
                            qrImageView.setImageBitmap(qrgEncoder.encodeAsBitmap());
                            attendanceV.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
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
        qrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (designation.equals("HR") || designation.equals("CEO") || designation.equals("Consultant") || type.equals("admin")){
                    startActivity(new Intent(EmployeePanel.this,TakeAttendance.class));
                }
            }
        });

        setNotification();

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
        if (sharedPreferences.getString("ehisabWithdrawAlert","").equals("True")){
            ehisabV.setVisibility(View.VISIBLE);
        }else {
            ehisabV.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString("analysisAlert","").equals("True")){
            analysisV.setVisibility(View.VISIBLE);
        }else {
            analysisV.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString("dailyReportAlert","").equals("True")){
            dailyReportV.setVisibility(View.VISIBLE);
        }else {
            dailyReportV.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employee,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.menuProfileEmployee) {
            startActivity(new Intent(EmployeePanel.this, ProfileEmployee.class));
        }

        else if (id == R.id.menuSearchEmployee) {
            startActivity(new Intent(EmployeePanel.this, MySearchList.class));
        }

        else if (id == R.id.menuBatchEmployee) {
            startActivity(new Intent(EmployeePanel.this, MyBatches.class));
        }
        else if (id == R.id.menuCollegeEmployee) {
            startActivity(new Intent(EmployeePanel.this, MyColleges.class));
        }
        else if (id == R.id.menuEnquiryEmployee) {
            if (designation.equals("HR") || designation.equals("CEO") || type.equals("admin")){
                startActivity(new Intent(EmployeePanel.this,EnquiryList.class));
            }
        }
        else if (id == R.id.menuLocationEmployee) {
            if (designation.equals("HR") || designation.equals("CEO") || type.equals("admin")){
                startActivity(new Intent(EmployeePanel.this, MapsActivity.class));
            }else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.menuCertificatesEmployee) {
            startActivity(new Intent(EmployeePanel.this, CertificatesActivity.class));
        }
        else if (id == R.id.menuLogoutEmployee){
            firebaseAuth.signOut();
            startActivity(new Intent(EmployeePanel.this, LoginActivity.class));
            ActivityCompat.finishAffinity(EmployeePanel.this);
        }
        else if (id == R.id.menuInfoEmployee){
            firebaseAuth.signOut();
            startActivity(new Intent(EmployeePanel.this, InfoActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void AttendanceEmpPanel(View view) {
        dialog.show();
    }

    public void notificationEmpPanel(View view) {
        startActivity(new Intent(EmployeePanel.this, MyNotification.class));
    }

    public void classroomEmpPanel(View view) {
        startActivity(new Intent(EmployeePanel.this,ClassRoomDashboard.class));
    }

    public void eHisabEmpPanel(View view) {
        if (type.equals("admin") || designation.equals("CEO")) {
            startActivity(new Intent(EmployeePanel.this, DashboardEHisab.class));
        } else {
            startActivity(new Intent(EmployeePanel.this, ShowDepositListOnEmp.class).putExtra("uid",firebaseUser.getUid()));
        }
    }

    public void analysisEmpPanel(View view) {
        startActivity(new Intent(EmployeePanel.this, AnalysisList.class));
    }

    public void employeeEmpPanel(View view) {
        startActivity(new Intent(EmployeePanel.this, MyEmployeeTab.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotification();
    }

    public void dailyReportEmpPanel(View view) {
        startActivity(new Intent(EmployeePanel.this, ShowDailyReport.class));
    }
}
