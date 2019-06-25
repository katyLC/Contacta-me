package com.example.contactame.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.contactame.R;
import com.example.contactame.activities.ChatActivity;
import com.example.contactame.activities.MainActivity;
import com.example.contactame.activities.RatingActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        //        String sound = message.getNotification().getSound();

        int id = 0;
        Object obj = message.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
        }

        this.sendNotification(new NotificationData(id, title, text), message.getData(), message.getNotification().getClickAction());
    }

    private void sendNotification(NotificationData notificationData, Map<String, String> data, String clickAction) {
        Intent intent = null;
        if (clickAction.equals("ChatActivity")) {
            intent = new Intent(getApplicationContext(), ChatActivity.class);
        } else if(clickAction.equals("RatingActivity")) {
            intent = new Intent(getApplicationContext(), RatingActivity.class);
            intent.putExtra("proveedor_uid", data.get("proveedor_uid"));
        }

        intent.putExtra("usuario_nombre", data.get("usuario_nombre"));
        intent.putExtra("deID", data.get("deID"));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = null;
        try {

            notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(URLDecoder.decode(notificationData.getTitle(), "UTF-8"))
                .setContentText(URLDecoder.decode(notificationData.getTextMessage(), "UTF-8"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "No se puede crear la notificacion");
        }
    }

}
