package com.neurosky.mindwavemobiledemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    Button enterBtn;
    Intent enterIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterBtn = (Button)findViewById(R.id.enter);

         /*button listeners*/
        /*listener for enter button*/
        enterBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                enterIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(enterIntent);

            }
        });
    }
}