package com.macode.taskmaster;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddTask extends AppCompatActivity {
    File attachedFile;
    Map<String, Team> teams;
    String globalKey = "";
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String addressString;
    float latitude;
    float longitude;

    @RequiresApi(api = Build.VERSION_CODES.Q)
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

        Intent sharedImageIntent = getIntent();
        if (sharedImageIntent.getType() != null) {
            attachedFile = new File(getFilesDir(), "temporaryFile");
            // Use Uri to retrieve data: https://stackoverflow.com/questions/7832773/android-how-to-get-the-image-using-intent-data
            Uri imageData = sharedImageIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            try {
                InputStream inputStream = getContentResolver().openInputStream(imageData);
                FileUtils.copy(inputStream, new FileOutputStream(attachedFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            TextView fileStatusText = AddTask.this.findViewById(R.id.chosenFileText);
            String fileAttached = "File Attached";
            fileStatusText.setText(fileAttached);
            Log.i("Amplify.resultActivity", "Its impossibru!!!");
        }

        // Configure location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                currentLocation = locationResult.getLastLocation();
                Log.i("Location", currentLocation.toString());

                Geocoder geocoder = new Geocoder(AddTask.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);
                    Log.i("Location", addressList.get(0).toString());
                    latitude = (float) currentLocation.getLatitude();
                    longitude = (float) currentLocation.getLongitude();
                    addressString = addressList.get(0).getAddressLine(0);
                    Log.i("Location", addressString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        if (ActivityCompat.checkSelfPermission((this), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission((this), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.i("Location", "Location access permission is not permitted");
            requestLocationAccess();
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

        Button chosenFileButton = AddTask.this.findViewById(R.id.chosenFileButton);
        chosenFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventTracker.trackButtonClicked(view);
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
                EventTracker.trackButtonClicked(v);
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
                        .address(addressString)
                        .latitude(latitude)
                        .longitude(longitude)
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
            updatedAttachedFile(data);
        } else {
            Log.i("Amplify.resultActivity", "Did not receive image");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void updatedAttachedFile(Intent data) {
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
        Log.i("Amplify.resultActivity", "Its impossibru!!!");
    }

    public void requestLocationAccess() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }
}
