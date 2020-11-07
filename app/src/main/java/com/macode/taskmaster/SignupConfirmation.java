package com.macode.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;

public class SignupConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_confirmation);

        ((Button) findViewById(R.id.confirmationButton)).setOnClickListener(view -> {
            EventTracker.trackButtonClicked(view);
            String username = ((TextView) findViewById(R.id.usernameConfirmation)).getText().toString();
            String verificationCode = ((TextView) findViewById(R.id.verificationCode)).getText().toString();

            Amplify.Auth.confirmSignUp(
                    username,
                    verificationCode,
                    result -> {
                        Log.i("Amplify.confirm", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                        startActivity(new Intent(SignupConfirmation.this, Login.class));
                    },
                    error -> Log.e("Amplify.confirm", error.toString())
            );
        });


    }
}