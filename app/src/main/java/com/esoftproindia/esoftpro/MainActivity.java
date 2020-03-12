package com.esoftproindia.esoftpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.esoftproindia.esoftpro.panel.EmployeePanel;
import com.esoftproindia.esoftpro.panel.StudentPanel;
import com.esoftproindia.esoftpro.panel.UnknownPanel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final String TAG="MainActivity";

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private Button button;
    private String type;

    private LocationManager locationManager;
    private  double latitude,longitude;
    private static final int LOCATION_REQUEST = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        progressBar=(ProgressBar)findViewById(R.id.progressBarMainActivity);
        button=(Button)findViewById(R.id.btnMainActivity);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        progressBar.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        try {
            Intent serviceIntent = new Intent(this, ExampleService.class);
            serviceIntent.putExtra("inputExtra", "Called by MainaActivity");
            startService(serviceIntent);
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    editor.putString("batch", dataSnapshot.child("users").child(firebaseUser.getUid()).child("batch").getValue(String.class));
                    editor.putString("college", dataSnapshot.child("users").child(firebaseUser.getUid()).child("college").getValue(String.class));
                    editor.putString("enrollment", dataSnapshot.child("users").child(firebaseUser.getUid()).child("enrollment").getValue(String.class));
                    editor.putString("designation", dataSnapshot.child("users").child(firebaseUser.getUid()).child("designation").getValue(String.class));
                    editor.putString("name", dataSnapshot.child("users").child(firebaseUser.getUid()).child("name").getValue(String.class));
                    editor.putString("uid", dataSnapshot.child("users").child(firebaseUser.getUid()).child("uid").getValue(String.class));
                    editor.putString("joinDate", dataSnapshot.child("users").child(firebaseUser.getUid()).child("joinDate").getValue(String.class));
//                    editor.putString("exitDate", dataSnapshot.child("users").child(firebaseUser.getUid()).child("exitDate").getValue(String.class));

                    editor.putString("notificationAlert", dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("notification").getValue(String.class));
                    editor.putString("classroomAlert", dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("classroom").getValue(String.class));
                    editor.putString("ehisabWithdrawAlert", dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("ehisabWithdraw").getValue(String.class));
                    editor.putString("analysisAlert", dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("analysis").getValue(String.class));
                    editor.putString("employeeAlert", dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("employee").getValue(String.class));
                    editor.putString("dailyReportAlert", dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("dailyReport").getValue(String.class));
                    type = dataSnapshot.child("users").child(firebaseUser.getUid()).child("type").getValue(String.class);
                    editor.putString("type", type);
                    editor.apply();
                    progressBar.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline() && isLocation()) {
                    try {
                        switch (type) {
                            case "student":
                            case "employee":
                                startActivity(new Intent(MainActivity.this, UnknownPanel.class));
                                break;
                            case "studentSpi":
                                startActivity(new Intent(MainActivity.this, StudentPanel.class));
                                break;
                            case "employeeSpi":
                            case "admin":
                                startActivity(new Intent(MainActivity.this, EmployeePanel.class));
                                break;
                        }
                        ActivityCompat.finishAffinity(MainActivity.this);
                    } catch (Exception e) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        Toast.makeText(MainActivity.this, "Login First", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isLocation() {
        boolean x;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            x=false;
        } else {
            try {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager != null ? locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) : null;
                if (location != null) {
                    onLocationChanged(location);
                }
            } catch (Exception e) {
                Log.d(TAG, "isLocation: " + e.getMessage());
            }
            x=true;
        }
    return x;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
        }
    }


    protected boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onStart() {
        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        super.onStart();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude= location.getLongitude();
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList;
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            databaseReference.child("users").child(firebaseUser.getUid()).child("location").setValue(addressList.get(0).getLocality());
            databaseReference.child("users").child(firebaseUser.getUid()).child("country").setValue(addressList.get(0).getCountryName());
            databaseReference.child("users").child(firebaseUser.getUid()).child("latitude").setValue(String.valueOf(latitude));
            databaseReference.child("users").child(firebaseUser.getUid()).child("longitude").setValue(String.valueOf(longitude));
            databaseReference.child("users").child(firebaseUser.getUid()).child("lastLocation").setValue(new GetDateTime().getTimeDate());
        } catch (Exception e) {
            Log.d(TAG, "onLocationChanged: "+e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}