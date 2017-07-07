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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        wave_layout = (LinearLayout) findViewById(R.id.result_wave_layout);

        Intent intent = getIntent();
        data = intent.getStringExtra("DATA");

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
        waveView.setValue(9999, 9999, -9999);
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
            for(int i=0;i<arr.length();i++){
                try {
                    updateWaveView(arr.getInt(i));
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }

        }



    }


}
