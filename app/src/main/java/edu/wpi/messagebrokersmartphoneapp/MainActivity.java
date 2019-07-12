package edu.wpi.messagebrokersmartphoneapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    RecyclerAdapter recyclerAdapter;
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, DiscoverableService.class);
        this.startService(i);

        createNotificationChannel();

        idList.add("8962151");
        idList.add("6515212");
        idList.add("859541");
        contentList.add("Content 1");
        contentList.add("Content 2");
        contentList.add("Content 3");

        initRecyclerView();


        ((TextView)findViewById(R.id.phoneNameTextView)).setText(Build.MANUFACTURER + " - " + Build.MODEL);

        final TextView firebaseTokenTextView = (TextView) findViewById(R.id.firebaseTokenTextView);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                firebaseTokenTextView.setText(token);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.myRecyclerView);
        recyclerAdapter = new RecyclerAdapter(this, idList, contentList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {

        EditText editText = (EditText) findViewById(R.id.editText);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myChannelID1")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Title")
                .setContentText(editText.getText())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Random ran = new Random();
        int uniqueID = ran.nextInt();
        System.out.println("Click on button! UUID: " + uniqueID);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(uniqueID, builder.build());

        idList.add(String.valueOf(uniqueID));
        contentList.add(editText.getText().toString());
        recyclerAdapter.notifyDataSetChanged();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myChannelName"; // getString(R.string.channel_name);
            String description = "myChannelDescription"; // getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myChannelID1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}