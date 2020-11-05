package com.macode.taskmaster;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends AppCompatActivity {
    File attachedFile;
    Map<String, Team> teams;
    String globalKey = "";

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

        Button chosenFileButton = AddTask.this.findViewById(R.id.chosenFileButton);
        chosenFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveFile();
            }
        });

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

                if (attachedFile.exists()) {
                    globalKey = attachedFile.getName() + Math.random();
                    uploadFile(attachedFile, globalKey);
                }

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
                        .state("new").fileKey(globalKey)
                        .team(getTeam()).build();

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

    public void uploadFile(File file, String fileKey) {
        Amplify.Storage.uploadFile(
                fileKey,
                file,
                result -> Log.i("Amplify.S3", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("Amplify.S3", "Upload failed", storageFailure)
        );
    }

    public void retrieveFile() {
        Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getImageIntent.setType("*/*");

        // Example of getting specific file types
//        getPicIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{".png", ".jpg"});

        // These work together to make sure the images are immediately accessible and openable locally
//        getPicIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        getPicIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(getImageIntent, 32);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 32) {
            Log.i("Amplify.resultImage", "Got the image");
            attachedFile = new File(getFilesDir(), "temporaryFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(attachedFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            TextView fileStatusText = AddTask.this.findViewById(R.id.chosenFileText);
            String fileAttached = "File Attached";
            fileStatusText.setText(fileAttached);
        } else {
            Log.i("Amplify.resultActivity", "Its impossibru!!!");
        }
    }
}
