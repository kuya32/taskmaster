package com.macode.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;

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

        taskTitle.setText(intent.getExtras().getString("title"));
        taskBody.setText(intent.getExtras().getString("body"));
        String stateText = "Progress: " + intent.getExtras().getString("state");
        taskState.setText(stateText);

        downloadFile(intent.getExtras().getString("fileKey"));
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