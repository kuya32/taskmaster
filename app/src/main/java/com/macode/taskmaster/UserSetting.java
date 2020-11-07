package com.macode.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class UserSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        findViewById(R.id.usernameButton).setOnClickListener((view) -> {
            EventTracker.trackButtonClicked(view);
            RadioGroup boxOfRadios = UserSetting.this.findViewById(R.id.userSettingsRadioGroup);
            RadioButton selectedTeam = UserSetting.this.findViewById(boxOfRadios.getCheckedRadioButtonId());
            String team = selectedTeam.getText().toString();
            TextView username = findViewById(R.id.usernameEditText);

            preferenceEditor.putString("usernameTasks", username.getText().toString());
            preferenceEditor.putString("team", team);
            preferenceEditor.apply();
            Toast toast = Toast.makeText(this, "Welcome " + username.getText().toString() + "!", Toast.LENGTH_LONG);
            toast.show();

            Intent intent = new Intent(UserSetting.this, MainActivity.class);
            UserSetting.this.startActivity(intent);
            finish();
        });
    }

}