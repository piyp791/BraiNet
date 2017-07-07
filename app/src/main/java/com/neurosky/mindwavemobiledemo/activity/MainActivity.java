package com.neurosky.mindwavemobiledemo.activity;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;

public class MainActivity extends AppCompatActivity{

    Button loginBtn;
    Button registerBtn;
    Intent registerIntent;
    Intent loginIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loginBtn = (Button)findViewById(R.id.login);
        registerBtn = (Button)findViewById(R.id.register);



        /*button listeners*/
        /*listener for login button*/
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });

        /*listener for register button*/
        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}