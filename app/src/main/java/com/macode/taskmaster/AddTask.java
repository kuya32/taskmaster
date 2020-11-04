package com.macode.taskmaster;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTask extends AppCompatActivity {

    String teamTask;
    Map<String, Team> teams;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        teams = new HashMap<>();
        Amplify.API.query(
                ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        teams.put(team.getName(), team);
                    }
                    Log.i("Amplify.queryTeams", "Received teams");
                },
                error -> Log.e("Amplify.queryTeams", "Dud not receive teams")
        );

        NotificationChannel channel = new NotificationChannel("basic", "basic", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Basic notifications");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Button addTaskPageButton = AddTask.this.findViewById(R.id.addTaskPageButton);
        addTaskPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText taskTitleInput = AddTask.this.findViewById(R.id.taskTitle);
                EditText taskBodyInput = AddTask.this.findViewById(R.id.taskDescription);
                String taskTitle = taskTitleInput.getText().toString();
                String taskBody = taskBodyInput.getText().toString();
                System.out.println(String.format("Submitted! New task: %s has been added to the list! Description: %s.", taskTitle, taskBody));

                NotificationCompat.Builder builder = new NotificationCompat.Builder(AddTask.this, "basic")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(taskTitle)
                        .setContentText("Adding " + taskTitle + " to TODO list")
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(89898989, builder.build());

                Task task = Task.builder()
                        .title(taskTitle).body(taskBody)
                        .state("new").team(getTeam()).build();


                Amplify.API.mutate(ModelMutation.create(task),
                        response -> Log.i("AddTaskActivityAmplify", "Successfully added new task"),
                        error -> Log.e("AddTaskActivityAmplify", error.toString()));

                Intent intent = new Intent(AddTask.this, MainActivity.class);
                AddTask.this.startActivity(intent);
                finish();
            }
        });
    }

    public Team getTeam() {
        RadioGroup boxOfRadios = AddTask.this.findViewById(R.id.radioGroup);
        RadioButton selectedTeam = AddTask.this.findViewById(boxOfRadios.getCheckedRadioButtonId());
        String teamName = selectedTeam.getText().toString();
        Team chosenTeam = null;
        for (String team : teams.keySet()) {
            if (teams.get(team).getName().equals(teamName)) {
                chosenTeam = teams.get(team);
            }
        }
        return chosenTeam;
    }
}
