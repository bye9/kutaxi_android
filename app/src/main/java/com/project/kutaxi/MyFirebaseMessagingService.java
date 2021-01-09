package com.project.kutaxi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Boolean checkAlarm;
    String send_number, receive_number, host, hostgender, room_guest1, room_guest2, room_host, nickname;

    @Override
    public void onNewToken(String token) {
        //sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

    }

    /**
     * 푸시 알림 정보를 받아서 메소드로 넘긴다.
     *
     *
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        //최초 알림 수신
        SharedPreferences alarmInformation = getSharedPreferences("alarm", MODE_PRIVATE);
        checkAlarm = alarmInformation.getBoolean("check", true);

        //해당 대화방 내에선 알림 안뜨게
        send_number = remoteMessage.getData().get("send_number");
        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE);
        receive_number = chatInformation.getString("receive_number", "null");

        //+대화 클릭 시 해당 대화방으로 보내줄 때 필요한 정보
        SharedPreferences loginInformation = getSharedPreferences("setting", MODE_PRIVATE);
        host = loginInformation.getString("host", "200000000");
        hostgender = loginInformation.getString("hostgender", "성별");
        nickname = loginInformation.getString("nickname", "닉네임");

        room_guest1 = remoteMessage.getData().get("guest1");
        room_guest2 = remoteMessage.getData().get("guest2");
        room_host = remoteMessage.getData().get("room_host");

        if (checkAlarm && !send_number.equals(receive_number))
            sendNotification(remoteMessage.getData().get("nickname"), remoteMessage.getData().get("message"), send_number, host, hostgender, room_guest1, room_guest2, room_host, nickname);
    }


    /**
     * 푸시 알림 정보를 받아서 앱에서 알림을 띄워준다.
     * 포어그라운
     *
     */
    private void sendNotification(String title, String messageBody, String send_number, String host, String hostgender,
                                  String room_guest1, String room_guest2, String room_host, String nickname) {
        Intent intent = new Intent(this, ChatCarpool.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("room_number", send_number);
        intent.putExtra("host", host);
        intent.putExtra("nickname", nickname);
        intent.putExtra("hostgender", hostgender);
        intent.putExtra("guest1", room_guest1);
        intent.putExtra("guest2", room_guest2);
        intent.putExtra("room_host", room_host);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "kutaxi";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}