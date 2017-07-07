package com.neurosky.mindwavemobiledemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;
import com.neurosky.mindwavemobiledemo.helper.WebRequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    Button searchBtn;
    Intent searchIntent;
    EditText searchID;
    String searchId_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchID = (EditText)findViewById(R.id.search_idEdit);
        searchBtn = (Button)findViewById(R.id.search_searchBtn);

         /*button listeners*/
        /*listener for record button*/
        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                JSONObject jsonObject = new JSONObject();
                try{
                    searchId_str = searchID.getText().toString();
                    jsonObject.put("ID", searchId_str);

                }catch(Exception ex){
                    ex.printStackTrace();
                }


                final SearchTask searchTask = new SearchTask(SearchActivity.this);
                try {
                    searchTask.execute(jsonObject.toString(), "");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });
    }

    private class SearchTask extends AsyncTask<String, Integer, String> {


        private Context context;

        public SearchTask(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... param) {

            String searchParam = param[0];

            JSONObject dataObj = null;
            try {
                dataObj = new JSONObject(searchParam);
            }catch(Exception ex){
                ex.printStackTrace();
            }

            WebRequestHelper.get("/search/ " +dataObj.toString(), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d(Constants.CUSTOM_LOG_TYPE, response.toString());

                    //open HomeScreen Activity
                    String status = "";
                    try {
                        status = response.getString("status");
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    if(status.equals("success")){

                        //retrieve userid
                        Object data = "";
                        JSONArray waveData = null;
                        try {
                            //data = response.get("USERDATA");
                            waveData = (JSONArray)response.get("BRAINWAVE");

                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                        String id = searchID.getText().toString();
                        Log.d(Constants.LOGIN_INTENT, "id-->"+ id);
                        Log.d(Constants.LOGIN_INTENT, "wavedata-->"+ waveData);


                        searchIntent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                        searchIntent.putExtra("DATA", waveData.toString());
                        startActivity(searchIntent);

                    }else{
                        //Toast.makeText(RegisterActivity.this, "Something wrong!!!", Toast.LENGTH_SHORT).show();
                        SearchActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SearchActivity.this.getBaseContext(), "Something wrong!!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /*
            //get value of id
            String nameVal = name.getText().toString();
            String ageVal = age.getText().toString();
            int genderId = genderGrp.getCheckedRadioButtonId();


            if(nameVal==null || nameVal.isEmpty() ||
                    ageVal==null || ageVal.isEmpty() ||
                    genderId==-1){
                Log.d(Constants.CUSTOM_LOG_TYPE, "Empty field");
                Toast.makeText(RegisterActivity.this, "Fill all the fields first", Toast.LENGTH_SHORT).show();
            }*/


        }

    }


}
