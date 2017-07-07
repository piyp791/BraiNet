package com.neurosky.mindwavemobiledemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;

public class HomeActivity extends AppCompatActivity {

    Button recordBtn;
    Intent recordIntent;
    TextView welcomeText;

    Button searchBtn;
    Intent searchIntent;

    String userId;
    String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recordBtn = (Button)findViewById(R.id.home_record);
        welcomeText = (TextView)findViewById(R.id.home_welcomeTxt);


        //get intent
        Intent intent = getIntent();
        final String useIntent = intent.getStringExtra(Constants.INTENT_KEY);
        userId = intent.getStringExtra("ID");
        sessionId = intent.getStringExtra("SESSIONID");
        Log.d(Constants.CUSTOM_LOG_TYPE, "user id->" +userId);
        Log.d(Constants.CUSTOM_LOG_TYPE, "session id->" +sessionId);
        Log.d(Constants.CUSTOM_LOG_TYPE, "intent->" +useIntent);

        //set welcome text
        welcomeText.setText("Welcome " + userId + " to the home screen");

         /*button listeners*/
        /*listener for record button*/
        recordBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                recordIntent = new Intent(HomeActivity.this, DemoActivity.class);
                recordIntent.putExtra(Constants.INTENT_KEY, useIntent);
                recordIntent.putExtra("ID", userId);
                recordIntent.putExtra("SESSIONID", sessionId);
                startActivity(recordIntent);
            }
        });

        searchBtn = (Button)findViewById(R.id.home_search);

         /*button listeners*/
        /*listener for record button*/
        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
                searchIntent.putExtra("ID", userId);
                searchIntent.putExtra("SESSIONID", sessionId);
                startActivity(searchIntent);
            }
        });
    }
}