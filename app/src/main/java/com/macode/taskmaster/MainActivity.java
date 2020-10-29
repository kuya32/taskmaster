package com.macode.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener{

    Database database;
    RecyclerView recyclerView;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView homepageTitle = findViewById(R.id.homepageTitle);
        homepageTitle.setText(preferences.getString("usernameTasks", "My Tasks"));



//        RecyclerView recyclerView = findViewById(R.id.taskListRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TaskAdapter(tasks, this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MainActivityAmplify", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("MainActivityAmplify", "Could not initialize Amplify", e);
        }

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "macode_task_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        Handler handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return false;
                    }
                });

        connectAdapterToRecyclerView(handler);

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

    private void connectAdapterToRecyclerView(Handler handler) {
        List<Task> taskInstances = new ArrayList<>();
        recyclerView = findViewById(R.id.taskListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(taskInstances, this));

        Amplify.API.query(
                ModelQuery.list(Task.class),
                response -> {
                    for (Task tasks : response.getData()) {
                        taskInstances.add(tasks);
                    }
                    handler.sendEmptyMessage(1);
                    Log.i("Amplify.queryitems", "Got this many items from Dynamo " + taskInstances.size());
                },
                error -> Log.i("Amplify.queryitems", "Did not get items"));
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