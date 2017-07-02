package org.mobilecomputing.projects.brainnet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SearchActivity extends AppCompatActivity {



    Button searchBtn;
    Intent searchIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchBtn = (Button)findViewById(R.id.search_searchBtn);

         /*button listeners*/
        /*listener for record button*/
        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                searchIntent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                startActivity(searchIntent);
            }
        });
    }
}
