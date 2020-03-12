package com.esoftproindia.esoftpro;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = "MapsActivity";

    private GoogleMap mMap;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildren().iterator().hasNext()) {

                        try {
                            for (DataSnapshot uid : dataSnapshot.getChildren()) {
                                final String title = uid.child("name").getValue(String.class);
//                                final String lastLocation = uid.child("lastLocation").getValue(String.class);
                                String dp = uid.child("dp").getValue(String.class);
                                double latitude = Double.parseDouble(uid.child("latitude").getValue(String.class));
                                double longitude = Double.parseDouble(uid.child("longitude").getValue(String.class));
                                final LatLng latLng = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(title).concat(",")));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));

//                                Glide.with(MapsActivity.this).load(dp).asBitmap().dontTransform().into(new SimpleTarget<Bitmap>() {
//                                    @Override
//                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                        float scale =getApplicationContext().getResources().getDisplayMetrics().density;
//                                        int pixels=(int)(50*scale+0.5f);
//                                        Bitmap bitmap=Bitmap.createScaledBitmap(resource,pixels,pixels,true);
//                                        mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(title).concat(",")).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
//                                    }
//
//                                    @Override
//                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                        mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(title).concat(",")));
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
//                                        Log.d(TAG, "onLoadFailed: "+e);
//                                    }
//                                });

                                Log.d(TAG, "allRecords: " + title + " " + latitude + "," + longitude);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onMapReady: " + e.getMessage());
        }
    }

}
