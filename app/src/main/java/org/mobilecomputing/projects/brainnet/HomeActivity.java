package org.mobilecomputing.projects.brainnet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    Button recordBtn;
    Intent recordIntent;

    Button searchBtn;
    Intent searchIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recordBtn = (Button)findViewById(R.id.home_record);

         /*button listeners*/
        /*listener for record button*/
        recordBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                recordIntent = new Intent(HomeActivity.this, RecordActivity.class);
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
                startActivity(searchIntent);
            }
        });
    }
}