package com.nutrition.checker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        LinearLayout layout = (LinearLayout) findViewById(R.id.vertical_linear);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
                Intent intent;

                if(!previouslyStarted)
                    intent = new Intent(WelcomeActivity.this, FormActivity.class);
                else
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);

                startActivity(intent);
                return true;
            }
        });
    }
}
