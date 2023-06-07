package com.example.logingps.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.logingps.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class NotificationBuilder {
    private static final String CHANNEL_ID = "e_bileco";
    private static final String CHANNEL_NAME = "E-BILECO";
    private static final String CHANNEL_DESC = "E-BILECO Notifications";

    public static Notification createAndroidNotification(Context context, QueryDocumentSnapshot document) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // pending implicit intent to view url
        //Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        //resultIntent.setData(Uri.parse(url));

        //PendingIntent pending = PendingIntent.getActivity(context,
        //        (int) System.currentTimeMillis(), resultIntent, 0);

        String description = document.getString("description");
        Map<String, Object> map = (Map<String, Object>) document.get("member_consumer_owner");
        String name = (String) map.get("name");
        String address = (String) map.get("address");
        String contactNumber = (String) map.get("contact_number");

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
        Intent intentMap = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intentMap.setPackage("com.google.android.apps.maps");

        Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber));

        Intent intentMessage = new Intent(Intent.ACTION_SENDTO);
        intentMessage.setData(Uri.parse("smsto:" + contactNumber));

        PendingIntent pendingIntentMap = PendingIntent.getActivity(context, 1, intentMap, 0);
        PendingIntent pendingIntentCall = PendingIntent.getActivity(context, 1, intentCall, 0);
        PendingIntent pendingIntentMessage = PendingIntent.getActivity(context, 1, intentMessage, 0);

        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                        .setContentTitle(name)
                        .setContentText(description)
                        //.setContentIntent(pending)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_action_place_black, "Directions", pendingIntentMap)
                        .addAction(R.drawable.ic_action_call_black, "Call", pendingIntentCall)
                        .addAction(R.drawable.ic_action_message_black, "Message", pendingIntentMessage);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        return mBuilder.build();
    }
}
