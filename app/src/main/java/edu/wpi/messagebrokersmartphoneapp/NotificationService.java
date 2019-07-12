package edu.wpi.messagebrokersmartphoneapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "MyDebug";


    public NotificationService() {
        Log.d(TAG, "Constructor of Notification Service called");
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();
            createNotificationWithIntent(data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            createNotification("Test from console", remoteMessage.getNotification().getBody());
        }



        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);


        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    private void createNotification(String title, String content) {
        Random ran = new Random();
        int uniqueID = ran.nextInt();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myChannelID1")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(uniqueID, builder.build());
    }

    private void createNotificationWithIntent(Map<String, String> data){
        String notificationTitle = data.get("title");
        String notificationContent = data.get("description");
        String interactionData = data.get("interaction");


        Random ran = new Random();
        int uniqueID = ran.nextInt();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myChannelID1")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        // ---------- INTENT ----------
        Intent notifyIntent = new Intent(this, NotificationActivity.class); // Creating new intent
        notifyIntent.putExtra("TITLE", notificationTitle);
        notifyIntent.putExtra("DESCRIPTION", notificationContent);
        notifyIntent.putExtra("INTERACTION", interactionData);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Set the Activity to start in a new, empty task
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT); // Create the PendingIntent
        builder.setContentIntent(notifyPendingIntent); // Setting intent for that notification
        // ---------- END OF INTENT ----------

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(uniqueID, builder.build());
    }
}
