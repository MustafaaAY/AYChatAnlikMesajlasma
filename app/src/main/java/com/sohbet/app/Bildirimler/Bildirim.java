package com.sohbet.app.Bildirimler;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.sohbet.app.R;
//Karşı tarafa bildirim göndermek için gerekli class
public class Bildirim extends ContextWrapper {

    private static final String CHANNEL_ID = "com.sohbet.app";
    private static final String CHANNEL_NAME = "app";

    private NotificationManager bildirimYoneticisi;

    public Bildirim(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if (bildirimYoneticisi == null){
            bildirimYoneticisi = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return  bildirimYoneticisi;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public  Notification.Builder getOreoNotification(String title, String body,
                                                     PendingIntent pendingIntent, Uri soundUri, String icon){
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_bildirim_ikon)//Integer.parseInt(icon) bildirim ikonu özelleştirme
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
