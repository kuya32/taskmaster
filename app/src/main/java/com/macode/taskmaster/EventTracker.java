package com.macode.taskmaster;

import android.util.Log;
import android.view.View;

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.core.Amplify;

import java.util.Date;

public class EventTracker {
    public static void trackButtonClicked (View view) {
        String buttonId = view.getResources().getResourceEntryName(view.getId());
        Log.i("EventTracker", "Button clicked on " + buttonId);

        String currentUser;

        if (Amplify.Auth.getCurrentUser() != null) {
            currentUser = Amplify.Auth.getCurrentUser().getUsername();
        } else {
            currentUser = "Guest";
        }

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("Button Clicked")
                .addProperty("Which Button?", buttonId)
                .addProperty("Successful", true)
                .addProperty("Username", currentUser)
                .addProperty("Is the user chill?", true)
                .build();

        Amplify.Analytics.recordEvent(event);
    }

    public static void eventTrackerAppStart() {
        Log.i("EventTracker", "App Start Event Tracker");

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("NewAppStart")
                .addProperty("time", Long.toString(new Date().getTime()))
                .addProperty("StartupSuccess", "We have eyes and ears everywhere!")
                .build();

        Amplify.Analytics.recordEvent(event);
    }
}
