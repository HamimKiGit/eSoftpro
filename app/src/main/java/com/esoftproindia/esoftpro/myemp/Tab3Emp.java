package com.esoftproindia.esoftpro.myemp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.ProfileFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Tab3Emp extends Fragment {
    public static final String TAG="Tab3Emp";


    public static Tab3Emp newInstance() {
        return new Tab3Emp();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_employee_tab3, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listViewFTab3);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("users").orderByChild("designation").equalTo("Consultant");
        FirebaseListAdapter<ProfileFile> adapter = new FirebaseListAdapter<ProfileFile>(
                getActivity(),
                ProfileFile.class,
                R.layout.layout_employee_info,
                query
        ) {
            @Override
            protected void populateView(View v, ProfileFile model, int position) {
                try {
                    ImageView imageView = (ImageView) v.findViewById(R.id.dpEmployeeInfoLay);
                    Glide.with(getActivity()).load(model.getDp()).asBitmap().into(imageView);
                    ((TextView) v.findViewById(R.id.nameEmployeeInfoLay)).setText(model.getName());
                    ((TextView) v.findViewById(R.id.designationEmployeeInfoLay)).setText(model.getDesignation());
                }catch (Exception e){
                    Log.d(TAG, "populateView: "+e.getMessage());
                }
            }
        };
        adapter.startListening();
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
