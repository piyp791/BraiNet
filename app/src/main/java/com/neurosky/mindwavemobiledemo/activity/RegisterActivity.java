package com.neurosky.mindwavemobiledemo.activity;

import android.content.Context;
import android.content.Intent;
import android.icu.lang.UCharacterEnums;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;
import com.neurosky.mindwavemobiledemo.helper.WebRequestHelper;
import com.neurosky.mindwavemobiledemo.model.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    Button enterBtn;
    Intent enterIntent;
    EditText name;
    EditText age;
    RadioGroup genderGrp;
    String maleOrFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        enterBtn = (Button)findViewById(R.id.register_enter);

        name = (EditText)findViewById(R.id.register_nameEdit);
        age = (EditText)findViewById(R.id.register_ageEdit);
        genderGrp = (RadioGroup)findViewById(R.id.register_genderGrp);


        genderGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            //gender has been selected, do your thing
            if (genderGrp.getCheckedRadioButtonId() == R.id.register_maleBtn) {
                maleOrFemale = "m";
            } else {
                maleOrFemale = "f";
            }

            }
        });


         /*button listeners*/
        /*listener for enter button*/
        enterBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {


                String nameVal = name.getText().toString();
                int ageVal = Integer.parseInt(age.getText().toString());
                String genderVal = maleOrFemale;
                //send the details to the server.
                //once the id is retrieved from the server, then proceed to the next screen
                User user = new User(nameVal, ageVal, genderVal);

                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("NAME", nameVal);
                    jsonObject.put("AGE", ageVal);
                    jsonObject.put("GENDER", maleOrFemale);
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                final InsertDataTask insertDataTask = new InsertDataTask(RegisterActivity.this);
                try {
                    insertDataTask.execute(jsonObject.toString(), Constants.REGISTER_INTENT);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }



    private class InsertDataTask extends AsyncTask<String, Integer, String> {


        private Context context;

        public InsertDataTask(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... param) {

            String userData = param[0];
            String intent = param[1];

            JSONObject jsonObj = new JSONObject();
            JSONObject dataObj = null;
            try {
                dataObj = new JSONObject(userData);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            try{
                jsonObj.put("DATA", dataObj);
                jsonObj.put(Constants.INTENT_KEY, intent);
            }catch(Exception ex){
                ex.printStackTrace();
            }

            WebRequestHelper.get("/register/ " +jsonObj.toString(), null, new JsonHttpResponseHandler() {
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
                        String userId = "";
                        try {
                            userId = response.getString("userid");
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                        String sessionID = userId + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                        Log.d(Constants.CUSTOM_LOG_TYPE, sessionID);

                        enterIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                        enterIntent.putExtra(Constants.INTENT_KEY, Constants.REGISTER_INTENT);
                        enterIntent.putExtra("ID", userId);
                        enterIntent.putExtra("SESSIONID", sessionID);
                        startActivity(enterIntent);

                    }else{
                        //Toast.makeText(RegisterActivity.this, "Something wrong!!!", Toast.LENGTH_SHORT).show();
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(RegisterActivity.this.getBaseContext(), "Something wrong!!!", Toast.LENGTH_LONG).show();
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

            //get value of id
            String nameVal = name.getText().toString();
            String ageVal = age.getText().toString();
            int genderId = genderGrp.getCheckedRadioButtonId();


            if(nameVal==null || nameVal.isEmpty() ||
                    ageVal==null || ageVal.isEmpty() ||
                    genderId==-1){
                Log.d(Constants.CUSTOM_LOG_TYPE, "Empty field");
                Toast.makeText(RegisterActivity.this, "Fill all the fields first", Toast.LENGTH_SHORT).show();
            }


        }

    }

}
