package com.shuvro.barcodescanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import static android.R.attr.key;
import static android.R.attr.value;
import static android.R.id.message;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

/**
 * Created by Admin on 2/2/2018.
 */

public class firebaseReceiveMessage extends FirebaseMessagingService
{
    private static final String TAG ="tinkerbell" ;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Random r = new Random();
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }


        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        String click_action = remoteMessage.getData().get("click_action");

        String[] split = message.split(",");
        message = split[0];


        Intent intent = new Intent(click_action);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("key",split[1]);


        PendingIntent pendingIntent = PendingIntent.getActivity(this,  (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setColor(getResources().getColor(R.color.btnColor));


        Uri uri= RingtoneManager.getDefaultUri(R.raw.doorbell);
        notificationBuilder.setSound(uri);
        notificationBuilder.setOnlyAlertOnce(false);
        notificationBuilder.setOngoing(true);

        notificationBuilder.setShowWhen(true);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource( getResources(), R.drawable.bell2));
        notificationBuilder.setSmallIcon(R.drawable.bell2);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify((r.nextInt(80 - 65) + 65), notificationBuilder.build());





    }


}
