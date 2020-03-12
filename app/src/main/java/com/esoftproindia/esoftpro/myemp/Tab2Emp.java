package com.esoftproindia.esoftpro.myemp;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.myfiles.EmpPostFile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Tab2Emp extends Fragment {
    public static final String TAG="Tab2Emp";

    private Dialog dialog;

    public static Tab2Emp newInstance() {
        return new Tab2Emp();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_employee_tab2, container, false);

        final ImageView imageView=(ImageView)view.findViewById(R.id.dpFTab2);
        final TextView name=(TextView)view.findViewById(R.id.nameFTab2);
        ListView listView = (ListView) view.findViewById(R.id.listViewFTab2);

        dialog=new Dialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(R.layout.popup_image_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        Query query=databaseReference.child("users").orderByChild("designation").equalTo("CEO");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        Glide.with(getActivity()).load(key.child("dp").getValue(String.class)).asBitmap().into(imageView);
                        name.setText(key.child("name").getValue(String.class));
                    }
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        FirebaseListAdapter<EmpPostFile> adapter = new FirebaseListAdapter<EmpPostFile>(
                getActivity(),
                EmpPostFile.class,
                R.layout.layout_employee_post,
                databaseReference.child("empPost").orderByChild("designation").equalTo("CEO")
        ) {
            @Override
            protected void populateView(View v, EmpPostFile model, int position) {
                try {
                    ((TextView) v.findViewById(R.id.subjectEmpPostLay)).setText(model.getSubject());
                    ImageView imageView = (ImageView) v.findViewById(R.id.imgEmpPost);
                    Glide.with(getActivity()).load(model.getImg()).asBitmap().into(imageView);
                    ((TextView) v.findViewById(R.id.descriptionEmpPostLay)).setText(model.getDescription());
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
