package com.laioffer.eventreporter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
  private static final String TAG = "MyFirebaseMsgService";

  @Override
  public void onNewToken(String s) {
    super.onNewToken(s);
    Log.d(TAG, s);
  }

  /**
   * Called when message is received.
   */
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Log.d(TAG, "From: " + remoteMessage.getFrom());

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data payload: " + remoteMessage.getData());

      if (/* Check if data needs to be processed by long running job */ true) {
        // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
        //scheduleJob();
      } else {
        // Handle message within 10 seconds
        handleNow();
      }
    }

    // Create notification channel
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

      // Configure the notification channel.
      notificationChannel.setDescription("Channel description");
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.RED);
      notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
      notificationChannel.enableVibration(true);
      notificationManager.createNotificationChannel(notificationChannel);
    }

    sendNotification(remoteMessage, NOTIFICATION_CHANNEL_ID);

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
    }
  }

  /**
   * Handle time allotted to BroadcastReceivers.
   */
  private void handleNow() {
    Log.d(TAG, "Short lived task is done.");
  }

  /**
   * Create and show a simple notification containing the received FCM message.
   */
  private void sendNotification(RemoteMessage remoteMessage, String channelId) {
    Intent intent = new Intent(this, EventActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    //Define pending intent to trigger activity
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
        PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    //Create Notification according to builder pattern
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this, channelId)
            .setLargeIcon(Utils.getBitmapFromURL(remoteMessage.getData().get("imgUri")))
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(remoteMessage.getData().get("title"))
            .setContentText(remoteMessage.getData().get("description"))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    // Get Notification Manager
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // Send notification
    notificationManager.notify(0, notificationBuilder.build());
  }

}
