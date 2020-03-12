package com.esoftproindia.esoftpro.myholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.esoftproindia.esoftpro.R;


public class NewsFeedViewHolder extends RecyclerView.ViewHolder{

    public ImageView circleImageView;
    public TextView myId,name,date,textView;
    public ImageView option,imageView;
    public CardView cardView;
    public NewsFeedViewHolder(View itemView) {
        super(itemView);

        circleImageView=(ImageView) itemView.findViewById(R.id.nfDpLay);
        myId=(TextView)itemView.findViewById(R.id.nfIdLay);
        name=(TextView)itemView.findViewById(R.id.nfNameLay);
        date=(TextView)itemView.findViewById(R.id.nfDateLay);
        textView=(TextView)itemView.findViewById(R.id.nfTextViewLay);
        imageView=(ImageView)itemView.findViewById(R.id.nfImageViewLay);
        cardView=(CardView)itemView.findViewById(R.id.nfCardLay);
    }
}
