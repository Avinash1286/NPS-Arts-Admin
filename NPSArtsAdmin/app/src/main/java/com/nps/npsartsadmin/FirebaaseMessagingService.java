package com.nps.npsartsadmin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(notificationTitle, notificationBody);
    }
    private void sendNotification(String notificationTitle, String notificationBody) {
        int ids=0;
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,new Intent(this,ShowPost.class),0);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap picture= BitmapFactory.decodeResource(getResources(),R.drawable.mininotify);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(FirebaaseMessagingService.this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.mininotify));
        builder.setSmallIcon(R.drawable.mininotify);
        builder.setAutoCancel(true);
        builder.setContentTitle(notificationTitle);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody));
        Uri path= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(path);
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId="Your Channel ID";
            NotificationChannel channel=new NotificationChannel(channelId,"channel human redable title",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        notificationManager.notify(ids,builder.build());
    }
}
