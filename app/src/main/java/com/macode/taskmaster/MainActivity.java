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
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.ApiOperation;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener{

    List<Task> taskInstances = new ArrayList<>();
//    Database database;
    RecyclerView recyclerView;
    Handler handler;
    Handler handleTaskFromSubscription;


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView homepageTitle = findViewById(R.id.homepageTitle);
        String usernameHomepage = preferences.getString("usernameTasks", "My Tasks");
        String usernameTeamName = preferences.getString("team", "Favorite Team");
        homepageTitle.setText(String.format("%s: %s's Tasks", usernameTeamName, usernameHomepage));

        setupRecyclerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return false;
                    }
                });

//        handleTaskAdded = new Handler(Looper.getMainLooper(),
//                message -> {
//                    setupRecyclerView();
//
//                    return false;
//                });

        handleTaskFromSubscription = new Handler(Looper.getMainLooper(),
                message -> {
                    recyclerView.getAdapter().notifyItemInserted(taskInstances.size() - 1);
                    recyclerView.smoothScrollToPosition(taskInstances.size() - 1);
                    Toast toast = Toast.makeText(this, "You added a new task", Toast.LENGTH_LONG);
                    toast.show();
                    return false;
        });



//        database = Room.databaseBuilder(getApplicationContext(), Database.class, "macode_task_master")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();



        configureAws();
        getTasksFromAws();
        setupTaskSubscription();



//        connectAdapterToRecyclerView(handler);

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

//    private void connectAdapterToRecyclerView(Handler handler) {
//        recyclerView = findViewById(R.id.taskListRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TaskAdapter(taskInstances, this));
//
//
//    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.taskListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(taskInstances, this));
    }

    public void setupTaskSubscription() {
         Amplify.API.subscribe(
                ModelSubscription.onCreate(Task.class),
                onEstablish -> Log.i("Amplify.subscription", "Established"),
                onCreated -> {
                    Log.i("Amplify.subscription", "Task was created: " + onCreated.getData().getTitle());
                    Task task = onCreated.getData();
                    taskInstances.add(task);
                    handleTaskFromSubscription.sendEmptyMessage(1);
                },
                failure -> Log.i("Amplify", failure.toString()),
                 () -> Log.i("Amplify", "Complete")
        );
    }

    public void configureAws() {
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MainActivityAmplify", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("MainActivityAmplify", "Could not initialize Amplify", e);
        }
    }

    public void getTasksFromAws() {
        Amplify.API.query(
                ModelQuery.list(Task.class),
                response -> {
                    for (Task tasks : response.getData()) {
                        taskInstances.add(tasks);
                        System.out.println(tasks);
                    }
                    handler.sendEmptyMessage(1);
                    Log.i("Amplify", "Got this many tasks from Dynamo " + taskInstances.size());
                },
                error -> Log.i("Amplify", "Did not get tasks"));
    }

//    public void createTeams() {
//        Team seahawks = Team.builder()
//                .name("Seattle Seahawks").build();
//
//        Team bills = Team.builder()
//                .name("Buffalo Bills").build();
//
//        Team cardinals = Team.builder()
//                .name("Arizona Cardinals").build();
//
//        Amplify.API.mutate(ModelMutation.create(seahawks),
//                response -> Log.i("Amplify.addTeam", "Successfully added team"),
//                error -> Log.e("Amplify.addTeam", error.toString()));
//
//        Amplify.API.mutate(ModelMutation.create(bills),
//                response -> Log.i("Amplify.addTeam", "Successfully added team"),
//                error -> Log.e("Amplify.addTeam", error.toString()));
//
//        Amplify.API.mutate(ModelMutation.create(cardinals),
//                response -> Log.i("Amplify.addTeam", "Successfully added team"),
//                error -> Log.e("Amplify.addTeam", error.toString()));
//    }

    @Override
    public void taskListener(Task task) {
        Intent goToTaskDetailsIntent = new Intent(MainActivity.this, TaskDetail.class);
        goToTaskDetailsIntent.putExtra("title", task.getTitle());
        goToTaskDetailsIntent.putExtra("body", task.getBody());
        goToTaskDetailsIntent.putExtra("state", task.getState());
        this.startActivity(goToTaskDetailsIntent);
    }
}