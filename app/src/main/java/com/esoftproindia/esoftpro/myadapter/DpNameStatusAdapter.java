package com.esoftproindia.esoftpro.myadapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.esoftproindia.esoftpro.R;
import com.esoftproindia.esoftpro.ehisab.AddWithdrawOnEmp;
import com.esoftproindia.esoftpro.ehisab.ShowDepositListOnEmp;
import com.esoftproindia.esoftpro.ehisab.ShowWithdrawListOnEmp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class DpNameStatusAdapter extends RecyclerView.Adapter<DpNameStatusAdapter.ViewHolder> {

    private static final String TAG="DpNameStatusAdapter";
    private Context mContext;
    private ArrayList<String> mUid=new ArrayList<>();
    private ArrayList<String> mName=new ArrayList<>();
    private ArrayList<String> mDp=new ArrayList<>();
    private ArrayList<String> mStatus=new ArrayList<>();

    private DatabaseReference databaseReference;


    public DpNameStatusAdapter(Context mContext, ArrayList<String> mUid, ArrayList<String> mName, ArrayList<String> mDp, ArrayList<String> mStatus) {
        this.mContext = mContext;
        this.mUid = mUid;
        this.mName = mName;
        this.mDp = mDp;
        this.mStatus = mStatus;
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dp_name_status,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(mContext).load(mDp.get(position)).asBitmap().into(holder.dpImg);
        holder.name.setText(mName.get(position));
        try {
            if (mStatus.get(position).equals("True")) {
                holder.status.setVisibility(View.VISIBLE);
            } else {
                holder.status.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Log.d(TAG, "onBindViewHolder: try "+e.getMessage());
        }
        holder.deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    databaseReference.child("users").child(mUid.get(position)).child("alert").child("ehisabDeposit").setValue("False");
                    mContext.startActivity(new Intent(mContext, ShowDepositListOnEmp.class).putExtra("uid", mUid.get(position)));
                }catch (Exception e){
                    Log.d(TAG, "onClick: "+e.getMessage());
                }
            }
        });
        holder.withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ShowWithdrawListOnEmp.class).putExtra("uid",mUid.get(position)));

            }
        });
        holder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, AddWithdrawOnEmp.class).putExtra("uid",mUid.get(position)).putExtra("name",mName.get(position)));
            }
        });

        Log.d(TAG, "onBindViewHolder: uid "+mUid.get(position)+" name "+mName.get(position));
    }

    @Override
    public int getItemCount() {
        return mName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView dpImg;
        public TextView name,deposit,withdraw,pay;
        public View status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dpImg=(ImageView)itemView.findViewById(R.id.dpDpNameStatusLay);
            name=(TextView)itemView.findViewById(R.id.nameDpNameStatusLay);
            status=(View)itemView.findViewById(R.id.statusDpNameStatusLay);
            deposit=(TextView) itemView.findViewById(R.id.depositDpNameStatusLay);
            withdraw=(TextView)itemView.findViewById(R.id.withdrawDpNameStatusLay);
            pay=(TextView)itemView.findViewById(R.id.payDpNameStatusLay);

        }
    }
}
