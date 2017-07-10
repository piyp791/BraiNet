package com.neurosky.mindwavemobiledemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;
import com.neurosky.mindwavemobiledemo.helper.WebRequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SearchResultsActivity extends AppCompatActivity {


    private LinearLayout wave_layout;
    DrawWaveView waveView = null;
    String data = "";
    String userInfo = "";
    TextView name;
    TextView id;
    TextView age;
    TextView gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        wave_layout = (LinearLayout) findViewById(R.id.result_wave_layout);
        name = (TextView)findViewById(R.id.search_result_name);
        id = (TextView)findViewById(R.id.search_result_id);
        age = (TextView)findViewById(R.id.search_result_age);
        gender = (TextView)findViewById(R.id.search_result_gender);

        Intent intent = getIntent();
        data = intent.getStringExtra("DATA");
        userInfo = intent.getStringExtra("USERINFO");

        JSONArray userInfoAr = null;
        try {
            userInfoAr = new JSONArray(userInfo);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        String idStr = null;
        String nameStr = null;
        String genderStr = null;
        String ageStr = null;
        try {
            idStr = userInfoAr.getString(0);
            nameStr = userInfoAr.getString(1);
            genderStr = userInfoAr.getString(2);
            ageStr = userInfoAr.getString(3);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        id.setText(idStr);
        name.setText(nameStr);
        age.setText(ageStr);
        gender.setText(genderStr);

        Log.d(Constants.CUSTOM_LOG_TYPE, "user info -->"+ userInfo);

        setUpDrawWaveView();

        final UpdateInitTask updateInitTask = new UpdateInitTask(SearchResultsActivity.this);
        try {
            updateInitTask.execute("", "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setUpDrawWaveView() {

        /*
        //Modification : PP-> instantiate new sensor data object
        dataObj = new SensorData();
        */

        waveView = new DrawWaveView(getApplicationContext());
        wave_layout.addView(waveView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        waveView.setValue(15000, 15000, -15000);
    }

    public void updateWaveView(int data) {
        if (waveView != null) {
            waveView.updateData(data);

        }
    }


    private class UpdateInitTask extends AsyncTask<String, Integer, String> {


        private Context context;

        public UpdateInitTask(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... param) {

            String userData = param[0];
            final String intent = param[1];

            Thread checkStatus = new Thread(){
                @Override
                public void run(){
                    while(!waveView.isReady()){
                        //Log.d(Constants.CUSTOM_LOG_TYPE, "NOT READY");
                    }
                }
            };

            checkStatus.start();

            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(Constants.CUSTOM_LOG_TYPE, "starting update view");

            JSONArray arr = null;
            try {
                arr = new JSONArray(data);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            int count = 0;
            for(int i=0;i<arr.length();i++){
                try {
                    updateWaveView(arr.getInt(i));
                    count++;
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            Log.d(Constants.CUSTOM_LOG_TYPE, "length of data->" + String.valueOf(count));

        }

    }

}
