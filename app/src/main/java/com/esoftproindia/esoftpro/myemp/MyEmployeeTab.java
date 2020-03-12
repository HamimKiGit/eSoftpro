package com.esoftproindia.esoftpro.myemp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myadapter.DynamicFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MyEmployeeTab extends AppCompatActivity {

    public static final String TAG="MyEmployeeTab";

    private ViewPager viewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_employee_tab);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("spi", MODE_PRIVATE);
        assert firebaseUser != null;
        databaseReference.child("users").child(firebaseUser.getUid()).child("alert").child("employee").setValue("False");
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("employeeAlert","False");
        editor.apply();


        viewPager = findViewById(R.id.viewpager);
        mTabLayout =  findViewById(R.id.tabs);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setDynamicFragmentToTabLayout();

    }

    private void setDynamicFragmentToTabLayout() {
        for (int i = 0; i < 12; i++) {
            switch (i){
                case 0:{
                    mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.director)));
                    break;
                }case 1:{
                    mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.ceo)));
                    break;
                }case 2:{
                    mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.consultant)));
                    break;
                }case 3:{
                    mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.employee)));
                    break;
                }
            }
        }
        DynamicFragmentAdapter mDynamicFragmentAdapter = new DynamicFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        viewPager.setAdapter(mDynamicFragmentAdapter);
        viewPager.setCurrentItem(0);
    }

}
