package com.esoftproindia.esoftpro.panel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.esoftproindia.esoftpro.AboutUsActivity;
import com.esoftproindia.esoftpro.InfoActivity;
import com.esoftproindia.esoftpro.LoginActivity;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.certificates.CertificatesActivity;
import com.esoftproindia.esoftpro.enquiry.AddEnquiry;
import com.esoftproindia.esoftpro.myemp.MyEmployeeTab;
import com.esoftproindia.esoftpro.profiles.ProfileEmployee;
import com.esoftproindia.esoftpro.profiles.ProfileStudent;
import com.esoftproindia.esoftpro.status.ViewStatus;
import com.google.firebase.auth.FirebaseAuth;

public class UnknownPanel extends AppCompatActivity {

    public static final String TAG="UnknownPanel";

    private FirebaseAuth firebaseAuth;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unknown_panel);

        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        type = sharedPreferences.getString("type", "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_unknwon,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.menuProfileUnknown){
            if (type.equals("student")){
                startActivity(new Intent(UnknownPanel.this, ProfileStudent.class));

            }else if ((type.equals("employee"))){
                startActivity(new Intent(UnknownPanel.this, ProfileEmployee.class));
            }
        }
        else if (id == R.id.menuLogoutUnknown){
            Toast.makeText(this, "LogOut", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            startActivity(new Intent(UnknownPanel.this, LoginActivity.class));
            ActivityCompat.finishAffinity(UnknownPanel.this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void teamSoftproUnkPanel(View view) {
        startActivity(new Intent(UnknownPanel.this, MyEmployeeTab.class));
    }

    public void facebookUnkPanel(View view) {
        startActivity(new Intent(UnknownPanel.this, ViewStatus.class));
    }

    public void enquiryUnkPanel(View view) {
        startActivity(new Intent(UnknownPanel.this, AddEnquiry.class));
    }

    public void certificatesUnkPanel(View view) {
        startActivity(new Intent(UnknownPanel.this, CertificatesActivity.class));
    }

    public void infoUnkPanel(View view) {
        startActivity(new Intent(UnknownPanel.this, InfoActivity.class));
    }

    public void aboutusUnkPanel(View view) {
        startActivity(new Intent(UnknownPanel.this, AboutUsActivity.class));
    }
}
