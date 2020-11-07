package com.macode.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener{

    List<Task> taskInstances = new ArrayList<>();
    RecyclerView recyclerView;
    Handler handler;
    Handler handleTaskFromSubscription;
    Handler handleCheckLoggedIn;

    public static final String TAG = "Amplify";

        private static PinpointManager pinpointManager;

        public static PinpointManager getPinpointManager(final Context applicationContext) {
            if (pinpointManager == null) {
                final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
                AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                    }

                    @Override
                    public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                    }
                });

                PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                        applicationContext,
                        AWSMobileClient.getInstance(),
                        awsConfig);

                pinpointManager = new PinpointManager(pinpointConfig);

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                                final String token = task.getResult().getToken();
                                Log.d(TAG, "Registering push notifications token: " + token);
                                pinpointManager.getNotificationClient().registerDeviceToken(token);
                            }
                        });
            }
            return pinpointManager;
        }

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
        getPinpointManager(getApplicationContext());
        getTasksFromAws();
        setupTaskSubscription();
        getIsSignedIn();

        Button signupButton = MainActivity.this.findViewById(R.id.signupButton);
        signupButton.setOnClickListener((view) -> {
            EventTracker.trackButtonClicked(view);
            System.out.println("Going to signup page.");
            Intent goToSignupIntent = new Intent(MainActivity.this, Signup.class);
            MainActivity.this.startActivity(goToSignupIntent);
        });

        Button loginButton = MainActivity.this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener((view) -> {
            EventTracker.trackButtonClicked(view);
            System.out.println("Going to login page.");
            Intent goToLoginIntent = new Intent(MainActivity.this, Login.class);
            MainActivity.this.startActivity(goToLoginIntent);
        });

        Button logoutButton = MainActivity.this.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener((view) -> {
            EventTracker.trackButtonClicked(view);
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
            EventTracker.trackButtonClicked(view);
            System.out.println("Going to add a task page.");
            Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTask.class);
            MainActivity.this.startActivity(goToAddTaskIntent);
        });

        Button allTasksButton = MainActivity.this.findViewById(R.id.allTasksMainButton);
        allTasksButton.setOnClickListener((view) -> {
            EventTracker.trackButtonClicked(view);
            System.out.println("Going to all tasks page.");
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasks.class);
            MainActivity.this.startActivity(goToAllTasksIntent);
        });

        ImageButton userSettingsButton = MainActivity.this.findViewById(R.id.userSettingsButton);
        userSettingsButton.setOnClickListener((view) -> {
            EventTracker.trackButtonClicked(view);
            System.out.println("Going to user settings page.");
            Intent goToUserSettingsIntent = new Intent(MainActivity.this, UserSetting.class);
            MainActivity.this.startActivity((goToUserSettingsIntent));
        });
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
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSPinpointAnalyticsPlugin(getApplication()));
            Amplify.configure(getApplicationContext());

            EventTracker.eventTrackerAppStart();

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
        goToTaskDetailsIntent.putExtra("fileKey", task.getFileKey());
        this.startActivity(goToTaskDetailsIntent);
    }
}