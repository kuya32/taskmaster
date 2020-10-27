package com.macode.taskmaster;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

public class AddTask extends AppCompatActivity {

    Database database;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "macode_task_master")
                .allowMainThreadQueries()
                .build();

        NotificationChannel channel = new NotificationChannel("basic", "basic", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Basic notifications");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Button addTaskPageButton = AddTask.this.findViewById(R.id.addTaskPageButton);
        addTaskPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText taskTitleInput = AddTask.this.findViewById(R.id.taskTitle);
                EditText taskDescriptionInput = AddTask.this.findViewById(R.id.taskDescription);
                String taskTitle = taskTitleInput.getText().toString();
                String taskDescription = taskDescriptionInput.getText().toString();
                System.out.println(String.format("Submitted! New task: %s has been added to the list! Description: %s.", taskTitle, taskDescription));

                NotificationCompat.Builder builder = new NotificationCompat.Builder(AddTask.this, "basic")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(taskTitle)
                        .setContentText("Adding " + taskTitle + " to TODO list")
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(89898989, builder.build());

                Task task = new Task(taskTitle, taskDescription, "assigned");
                database.taskDAO().saveTask(task);

                Toast toast = Toast.makeText(AddTask.this, "You added a new task", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
