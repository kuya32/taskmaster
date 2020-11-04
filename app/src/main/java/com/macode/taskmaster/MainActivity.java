package com.macode.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener{

    List<Task> taskInstances = new ArrayList<>();
//    Database database;
    RecyclerView recyclerView;
    Handler handler;
    Handler handleTaskFromSubscription;
    Handler handleCheckLoggedIn;

    @Override
    public void onResume() {
        super.onResume();
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        TextView homepageTitle = findViewById(R.id.homepageTitle);
//        String usernameHomepage = preferences.getString("usernameTasks", "My Tasks");
//        String usernameTeamName = preferences.getString("team", "Favorite Team");
//        homepageTitle.setText(String.format("%s: %s's Tasks", usernameTeamName, usernameHomepage));

        if (Amplify.Auth.getCurrentUser() != null) {
            TextView taskListTitle = findViewById(R.id.homepageTitle);
            String updatedHomepageTitle = Amplify.Auth.getCurrentUser().getUsername() + "'s Tasks";
            taskListTitle.setText(updatedHomepageTitle);
        } else {
            String warning = "You Need To Login";
            TextView taskListTitle = findViewById(R.id.homepageTitle);
            taskListTitle.setText(warning);
        }

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

        handleTaskFromSubscription = new Handler(Looper.getMainLooper(),
                message -> {
                    recyclerView.getAdapter().notifyItemInserted(taskInstances.size() - 1);
                    recyclerView.smoothScrollToPosition(taskInstances.size() - 1);
                    Toast toast = Toast.makeText(this, "You added a new task", Toast.LENGTH_LONG);
                    toast.show();
                    return false;
        });

        handleCheckLoggedIn = new Handler(Looper.getMainLooper(), message -> {
            if (message.arg1 == 0) {
                Log.i("Amplify.login", "Handler: They were not logged in");
            } else if (message.arg1 == 1) {
                Log.i("Amplify.login", "Handler: They were logged in");

                TextView usernameHomepageView = findViewById(R.id.homepageTitle);
                usernameHomepageView.setText(String.format("%s's Tasks", Amplify.Auth.getCurrentUser().getUsername()));

            } else {
                Log.i("Amplify.login", "Send true or false please");
            }
            return false;
        });

        configureAws();
        getTasksFromAws();
        setupTaskSubscription();
        getIsSignedIn();
//        addOneMockUsers();
//        verifyOneMockUser();
//        loginMockUser();

        Button signupButton = MainActivity.this.findViewById(R.id.signupButton);
        signupButton.setOnClickListener((view) -> {
            System.out.println("Going to signup page.");
            Intent goToSignupIntent = new Intent(MainActivity.this, Signup.class);
            MainActivity.this.startActivity(goToSignupIntent);
        });

        Button loginButton = MainActivity.this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener((view) -> {
            System.out.println("Going to login page.");
            Intent goToLoginIntent = new Intent(MainActivity.this, Login.class);
            MainActivity.this.startActivity(goToLoginIntent);
        });

        Button logoutButton = MainActivity.this.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener((view) -> {
            System.out.println("Going to logout user.");
            Amplify.Auth.signOut(
                    () -> Log.i("AuthQuickstart", "Signed Out Successfully"),
                    error -> Log.e("AuthQuickstart", error.toString())
            );
            Intent backToLoginIntent = new Intent(MainActivity.this, Login.class);
            MainActivity.this.startActivity(backToLoginIntent);
        });

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

    public void loginMockUser(){
        Amplify.Auth.signIn(
                "Kuya",
                "kuya3232",
                result -> {
                    Log.i("Amplify.login", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    getIsSignedIn();
                },
                error -> Log.e("Amplify.login", error.toString())
        );
    }

    public void verifyOneMockUser(){
        Amplify.Auth.confirmSignUp(
                "Kuya",
                "625911",
                result -> Log.i("Amplify.confirm", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                error -> Log.e("Amplify.confirm", error.toString())
        );
    }

    public void addOneMockUsers() {
        Amplify.Auth.signUp(
                "Kuya",
                "kuya3232",
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), "m.acode@outlook.com").build(),
                result -> Log.i("Amplify.signup", "Result: " + result.toString()),
                error -> Log.e("Amplify.signup", "Sign up failed", error)
        );
    }

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
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
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

    public void getIsSignedIn() {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i("Amplify.login", result.toString());
                    Message message = new Message();

                    if (result.isSignedIn()) {
                        message.arg1 = 1;
                    } else {
                        message.arg1 = 0;
                    }
                    handleCheckLoggedIn.sendMessage(message);
                },
                error -> Log.e("Amplify.login", error.toString())
        );
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