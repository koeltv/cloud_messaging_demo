package com.example.cloud_messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CANAL = "myNotificationCanal";

    /**
     * Appelée quand un nouveau token est créé.
     * Invoqué quand l'application est installé, et si jamais le token change
     * Called when a new token for the default Firebase project is generated.
     * Params:
     * @param token - Le token utilisé pour envoyer des messages à cette application
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FirebaseToken", "Refreshed token: " + token);
        Toast.makeText(this, "new token: " + token, Toast.LENGTH_LONG).show();
    }

    /**
     * Appelée quand un message est reçu.
     * Cette méthode est également appelée quand alors que l'application est au 1er plan.
     * Les paramètres de la notification peuvent être récupérés via RemoteMessage.getNotification().
     * @param message - le message distant reçu
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        RemoteMessage.Notification myNotification = message.getNotification();

        if (myNotification != null) {
            String myMessage = myNotification.getBody();
            Uri imageURI = myNotification.getImageUrl();
            Log.d("FirebaseMessage", "Vous venez de recevoir: " + myMessage);

            //Creation de l'action déclenché lors d'un clic sur la notification
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            //Fabrication de la notification android
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CANAL);

            //Ajout du titre, contenu
            notificationBuilder.setContentTitle("My notification");
            notificationBuilder.setContentText(myMessage);

            //Essaye d'ajouter l'image via l'uri fourni
            if (imageURI != null) {
                try {
                    notificationBuilder.setLargeIcon(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Ajout de l'action sur clique, sur suppression et de l'icone
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setDeleteIntent(pendingIntent);
            notificationBuilder.setSmallIcon(R.drawable.notification);

            //Si la version d'android est oreo ou supérieur, ajout des notions de channel
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = getString(R.string.notification_channel_id);
                String channelTitle = getString(R.string.notification_channel_title);
                String channelDescription = getString(R.string.notification_channel_desc);
                NotificationChannel channel = new NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(channelDescription);
                notificationManager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(channelId);
            }

            //Publication de la notification
            notificationManager.notify(1, notificationBuilder.build());
        }
    }
}
