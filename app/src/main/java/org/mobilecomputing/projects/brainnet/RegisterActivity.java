package org.mobilecomputing.projects.brainnet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    Button enterBtn;
    Intent enterIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        enterBtn = (Button)findViewById(R.id.enter);

         /*button listeners*/
        /*listener for enter button*/
        enterBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                enterIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(enterIntent);

            }
        });
    }
}