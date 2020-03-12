package com.esoftproindia.esoftpro.myadapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.esoftproindia.esoftpro.myemp.Tab1Emp;
import com.esoftproindia.esoftpro.myemp.Tab2Emp;
import com.esoftproindia.esoftpro.myemp.Tab3Emp;
import com.esoftproindia.esoftpro.myemp.Tab4Emp;


public class DynamicFragmentAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public DynamicFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Bundle b1 = new Bundle();
                b1.putInt("position", position);
                Tab1Emp tab1Emp= Tab1Emp.newInstance();
                tab1Emp.setArguments(b1);
                return tab1Emp;
            case 1:
                Bundle b2 = new Bundle();
                b2.putInt("position", position);
                Tab2Emp tab2Emp= Tab2Emp.newInstance();
                tab2Emp.setArguments(b2);
                return tab2Emp;
            case 2:
                Bundle b3 = new Bundle();
                b3.putInt("position", position);
                Tab3Emp tab3Emp= Tab3Emp.newInstance();
                tab3Emp.setArguments(b3);
                return tab3Emp;
            case 3:
                Bundle b4 = new Bundle();
                b4.putInt("position", position);
                Tab4Emp tab4Emp= Tab4Emp.newInstance();
                tab4Emp.setArguments(b4);
                return tab4Emp;
        }
        return null;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}