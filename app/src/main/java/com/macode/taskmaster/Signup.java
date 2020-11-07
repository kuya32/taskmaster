package com.macode.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ((Button) findViewById(R.id.signupUserButton)).setOnClickListener(view -> {
            EventTracker.trackButtonClicked(view);
            String username = ((TextView) findViewById(R.id.usernameSignupForm)).getText().toString();
            String password = ((TextView) findViewById(R.id.passwordSignupForm)).getText().toString();
            String email = ((TextView) findViewById(R.id.emailSignupForm)).getText().toString();

            Amplify.Auth.signUp(
                    username,
                    password,
                    AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build(),
                    result -> {
                        Log.i("Amplify.signup", "Result: " + result.toString());
                        startActivity(new Intent(Signup.this, SignupConfirmation.class));
                    },
                    error -> Log.e("Amplify.signup", "Sign up failed", error)
            );
        });
    }
}