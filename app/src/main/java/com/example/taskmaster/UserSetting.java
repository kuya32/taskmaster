package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

public class UserSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        findViewById(R.id.usernameButton).setOnClickListener((view) -> {
            EditText username = findViewById(R.id.usernameEditText);
            preferenceEditor.putString("usernameTasks", username.getText().toString() + "'s Tasks");
            preferenceEditor.apply();
            Toast toast = Toast.makeText(this, "Welcome " + username.getText().toString() + "!", Toast.LENGTH_LONG);
            toast.show();
        });
    }
}