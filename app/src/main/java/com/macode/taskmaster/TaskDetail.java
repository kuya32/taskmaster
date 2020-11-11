package com.macode.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;

import org.w3c.dom.Text;

import java.io.File;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent intent = getIntent();
        TextView taskTitle = TaskDetail.this.findViewById(R.id.taskDetailTitle);
        TextView taskBody = TaskDetail.this.findViewById(R.id.taskDetailBody);
        TextView taskState = TaskDetail.this.findViewById(R.id.taskDetailState);
        TextView taskAddress = TaskDetail.this.findViewById(R.id.taskDetailAddress);

        taskTitle.setText(intent.getExtras().getString("title"));
        taskBody.setText(intent.getExtras().getString("body"));
        String stateText = "Progress: " + intent.getExtras().getString("state");
        taskState.setText(stateText);
        taskAddress.setText(intent.getExtras().getString("address"));

        downloadFile(intent.getExtras().getString("fileKey"));

        Button checkMapLocationButton = this.findViewById(R.id.checkMapLocationButton);
        checkMapLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventTracker.trackButtonClicked(view);
                System.out.println("Checking location");
                float lat = intent.getExtras().getFloat("latitude");
                float lon = intent.getExtras().getFloat("longitude");
                // Explain how to use String.format with floats: https://stackoverflow.com/questions/5195837/format-float-to-n-decimal-places/5195976
                String location = String.format("geo:%.4f,%.4f", lat, lon);
                // Reference for using Google Map Intents for Android: https://developers.google.com/maps/documentation/urls/android-intents
                Uri mapIntentUri = Uri.parse(location);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void downloadFile(String fileKey) {
        Amplify.Storage.downloadFile(
                fileKey,
                new File(getApplicationContext().getFilesDir() + "/" + fileKey + ".txt"),
                result -> {
                    Log.i("Amplify.S3Download", "Successfully downloaded: " + result.getFile().getName());
                    ImageView taskFileKeyImage = findViewById(R.id.taskDetailFileKeyImage);
                    taskFileKeyImage.setImageBitmap(BitmapFactory.decodeFile(result.getFile().getPath()));
                },
                error -> Log.e("Amplify.S3Download",  "Download Failure", error)
        );
    }


}