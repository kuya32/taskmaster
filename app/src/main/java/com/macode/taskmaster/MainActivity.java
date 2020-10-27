package com.macode.taskmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener{

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView homepageTitle = findViewById(R.id.homepageTitle);
        homepageTitle.setText(preferences.getString("usernameTasks", "My Tasks"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Garbage", "Take out the garbage", "assigned"));
        tasks.add(new Task("Dishes", "Wash the dishes", "assigned"));
        tasks.add(new Task("Laundry", "Fold the laundry", "assigned"));
        tasks.add(new Task("Feed the Cat", "Feed Tofu", "assigned"));
        tasks.add(new Task("Coding", "Complete lab assignment", "assigned"));
        tasks.add(new Task("Garbage", "Take out the garbage", "assigned"));
        tasks.add(new Task("Dishes", "Wash the dishes", "assigned"));
        tasks.add(new Task("Laundry", "Fold the laundry", "assigned"));
        tasks.add(new Task("Feed the Cat", "Feed Tofu", "assigned"));
        tasks.add(new Task("Coding", "Complete lab assignment", "assigned"));
        tasks.add(new Task("Garbage", "Take out the garbage", "assigned"));
        tasks.add(new Task("Dishes", "Wash the dishes", "assigned"));
        tasks.add(new Task("Laundry", "Fold the laundry", "assigned"));
        tasks.add(new Task("Feed the Cat", "Feed Tofu", "assigned"));
        tasks.add(new Task("Coding", "Complete lab assignment", "assigned"));
        tasks.add(new Task("Garbage", "Take out the garbage", "assigned"));
        tasks.add(new Task("Dishes", "Wash the dishes", "assigned"));
        tasks.add(new Task("Laundry", "Fold the laundry", "assigned"));
        tasks.add(new Task("Feed the Cat", "Feed Tofu", "assigned"));
        tasks.add(new Task("Coding", "Complete lab assignment", "assigned"));

        RecyclerView recyclerView = findViewById(R.id.taskListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(tasks, this));

        Button addTaskButton = MainActivity.this.findViewById(R.id.addTaskMainButton);
        addTaskButton.setOnClickListener((view) -> {
            System.out.println("Going to add a task page.");
            Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTask.class);
            MainActivity.this.startActivity(goToAddTaskIntent);
        });

        Button allTasksButton = MainActivity.this.findViewById(R.id.allTasksMainButton);
        allTasksButton.setOnClickListener((view) -> {
            System.out.println("Going to all tasks page.");
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasks.class);
            MainActivity.this.startActivity(goToAllTasksIntent);
        });

        ImageButton userSettingsButton = MainActivity.this.findViewById(R.id.userSettingsButton);
        userSettingsButton.setOnClickListener((view) -> {
            System.out.println("Going to user settings page.");
            Intent goToUserSettingsIntent = new Intent(MainActivity.this, UserSetting.class);
            MainActivity.this.startActivity((goToUserSettingsIntent));
        });
    }

    @Override
    public void taskListener(Task task) {
        Intent goToTaskDetailsIntent = new Intent(MainActivity.this, TaskDetail.class);
        goToTaskDetailsIntent.putExtra("title", task.getTitle());
        goToTaskDetailsIntent.putExtra("body", task.getBody());
        goToTaskDetailsIntent.putExtra("state", task.getState());
        this.startActivity(goToTaskDetailsIntent);
    }
}