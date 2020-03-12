package com.esoftproindia.esoftpro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.esoftproindia.esoftpro.App.CHANNEL_ID;

public class ExampleService extends Service {

    public static final String TAG="ExampleService";

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        String notificationAlert = dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("notification").getValue(String.class);
                        String ehisabWithdrawAlert = dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("ehisabWithdraw").getValue(String.class);
                        String dailyReportAlert = dataSnapshot.child("users").child(firebaseUser.getUid()).child("alert").child("dailyReport").getValue(String.class);
                        String notificationMsg = dataSnapshot.child("alertMsg").child("notification").getValue(String.class);
                        String dailyReportMsg= dataSnapshot.child("alertMsg").child("dailyReport").getValue(String.class);
                        if (notificationAlert.equals("True")) {
                            setMyNotification("Notification", notificationMsg, 2);
                        }
                        if (ehisabWithdrawAlert.equals("True")) {
                            setMyNotification("eHisab", "Check your account", 3);
                        }
                        if (dailyReportAlert.equals("True")) {
                            setMyNotification("Daily Report", dailyReportMsg, 4);
                        }
                    }catch (Exception e){
                        Log.d(TAG, "onDataChange: "+e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//            Intent notificationIntent = new Intent(this, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Softpro India")
                    .setContentText("The Largest Learning Center")
                    .setSmallIcon(R.drawable.spilogo)
//                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            startForeground(1, notification);

        }catch (Exception e){
            Log.d(TAG, "onStartCommand: "+e.getMessage());
        }
        return START_NOT_STICKY;


    }

    private void setMyNotification(String title,String message,int id) {
        try {
            NotificationCompat.Builder b = new NotificationCompat.Builder(this);
            b.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.spilogo)
                    .setContentTitle(title)
                    .setContentText(message);

            NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            nm.notify(id, b.build());
        }catch (Exception e){
            Log.d(TAG, "setMyNotification: "+e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
