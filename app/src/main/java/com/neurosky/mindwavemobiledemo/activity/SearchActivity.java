package com.neurosky.mindwavemobiledemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;
import com.neurosky.mindwavemobiledemo.helper.WebRequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

import static android.support.constraint.R.id.parent;

public class SearchActivity extends AppCompatActivity{

    Button searchBtn;
    Button fetchSessionBtn;
    Intent searchIntent;
    EditText searchID;
    TextView searchIDLabel;
    String searchId_str;
    String gender;
    int age;
    String isAdmin;
    TextView searchNameLabel;
    TextView searchAgeLabel;
    TextView searchGenderLabel;
    TextView searchSessionIDLabel;
    EditText searchName;
    EditText searchAge;
    RadioGroup searchGender;
    Spinner searchSessionList;
    String[] strSessionList;
    String userInfo;
    String sessionIDSelected;
    String nonAdminUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchIDLabel = (TextView)findViewById(R.id.search_id);
        searchNameLabel = (TextView)findViewById(R.id.search_name);
        searchAgeLabel = (TextView)findViewById(R.id.search_age);
        searchGenderLabel = (TextView)findViewById(R.id.search_gender);
        searchSessionIDLabel = (TextView)findViewById(R.id.search_sesisonIdDropDownLabel);

        searchID = (EditText)findViewById(R.id.search_idEdit);
        searchName = (EditText)findViewById(R.id.search_nameEdit);
        searchAge = (EditText)findViewById(R.id.search_ageEdit);
        searchGender = (RadioGroup)findViewById(R.id.search_genderGrp);
        searchSessionList = (Spinner)findViewById(R.id.search_sessionIdDropDown);

        searchBtn = (Button)findViewById(R.id.search_searchBtn);
        fetchSessionBtn = (Button)findViewById(R.id.fetchSessionIDBtn);

        Intent intent = getIntent();
        isAdmin = intent.getStringExtra("ISADMIN");
        Log.d(Constants.CUSTOM_LOG_TYPE, "is admin-->"+ isAdmin);
        nonAdminUserData = intent.getStringExtra("USERDATA");

        searchID.setText("");
        searchAge.setText("");
        searchName.setText("");

        if(isAdmin.equalsIgnoreCase("no")){
            searchIDLabel.setVisibility(View.INVISIBLE);
            searchID.setVisibility(View.INVISIBLE);
            searchNameLabel.setVisibility(View.INVISIBLE);
            searchName.setVisibility(View.INVISIBLE);
            searchAgeLabel.setVisibility(View.INVISIBLE);
            searchAge.setVisibility(View.INVISIBLE);
            searchGenderLabel.setVisibility(View.INVISIBLE);
            searchGender.setVisibility(View.INVISIBLE);
        }

         /*button listeners*/
        /*listener for record button*/
        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                JSONObject jsonObject = new JSONObject();
                try{
                    searchId_str = searchID.getText().toString();
                    if(searchId_str!=null && !searchId_str.isEmpty()){
                        jsonObject.put("ID", searchId_str);
                    }else{
                        JSONArray userInfoArr = new JSONArray(userInfo);
                        Log.d(Constants.CUSTOM_LOG_TYPE, "User ID-->" +userInfoArr.getString(0));
                        jsonObject.put("ID", userInfoArr.getString(0));


                        sessionIDSelected = searchSessionList.getSelectedItem().toString();
                        jsonObject.put("SESSIONID", sessionIDSelected);
                    }


                }catch(Exception ex){
                    ex.printStackTrace();
                }

                final SearchTask searchTask = new SearchTask(SearchActivity.this);
                try {
                    searchTask.execute(jsonObject.toString(), "search");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        fetchSessionBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                final SearchTask searchTask = new SearchTask(SearchActivity.this);
                try {
                    searchTask.execute("", "fetchSessions");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        searchGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            //gender has been selected, do your thing
            if (searchGender.getCheckedRadioButtonId() == R.id.register_maleBtn) {
                gender = "m";
            } else {
                gender = "f";
            }

            }
        });
    }


    private String restoreSessionID(String modifiedSessionID){
        return "";
    }

    private class SearchTask extends AsyncTask<String, Integer, String> {


        private Context context;

        public SearchTask(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... param) {

            String params = param[0];
            final String requestedURI = param[1];

            JSONObject dataObj;
            dataObj = doParamsProcess(requestedURI, params);

            WebRequestHelper.get("/" + requestedURI + "/ " + dataObj.toString(), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d(Constants.CUSTOM_LOG_TYPE, response.toString());
                    doRequestHandle(requestedURI, response);
                }
            });
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        private JSONObject doParamsProcess(String requestedURI, String params) {

            JSONObject dataObj = null;
            if (requestedURI.equalsIgnoreCase("search")) {
                try {
                    dataObj = new JSONObject(params);
                    Log.d(Constants.CUSTOM_LOG_TYPE, "request sent to retreive brain wave ->" + dataObj.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }else if(requestedURI.equalsIgnoreCase("fetchSessions")){


                //if ID filled, then search on the basis of name, age, gender and ID
                //else, searched on the basis of just name, age and gender
                // if name, age and gender are not filled, then search just on the base of ID
                //check if name age gender filled
                if(isAdmin.equalsIgnoreCase("NO")){
                    JSONArray userInfoJSon = null;
                    try {
                        userInfoJSon = new JSONArray(nonAdminUserData);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

                    dataObj = new JSONObject();
                    try{
                        dataObj.put("ID", userInfoJSon.getString(0));
                        dataObj.put("NAME", userInfoJSon.getString(1));
                        dataObj.put("GENDER", userInfoJSon.getString(2));
                        dataObj.put("AGE", userInfoJSon.getString(3));
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }else if(isAdmin.equalsIgnoreCase("Yes")){

                    dataObj = new JSONObject();
                    String id = searchID.getText().toString();
                    String name = searchName.getText().toString();
                    int age = 0;
                    if(!searchAge.getText().toString().isEmpty()){
                        age = Integer.parseInt(searchAge.getText().toString());
                    }

                    String gender = "f";

                    //check which field has been filled
                    if(name!=null && !name.isEmpty()){
                        try {
                            dataObj.put("NAME", name);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    if(age!=0){
                        try {
                            dataObj.put("AGE", age);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    if(gender!=null && !gender.isEmpty()){
                        try {
                            dataObj.put("GENDER", gender);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    if(id!=null && !id.isEmpty()){
                        try {
                            dataObj.put("ID", id);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }

            }
            return dataObj;
        }

        private void doRequestHandle(String requestedURI, JSONObject response) {

            if (requestedURI.equalsIgnoreCase("search")) {
                doProcessSearchForID(response);
            }else if (requestedURI.equalsIgnoreCase("fetchSessions")) {
                //Log.d(Constants.CUSTOM_LOG_TYPE, response.toString());
                String status = "";
                try {
                    status = response.getString("status");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (status.equals("success")) {

                    JSONArray sessionIDList;
                    JSONObject userData;
                    try {
                        String userDataStr = response.getString("userdata");
                        userData = new JSONObject(userDataStr);
                        sessionIDList = userData.getJSONArray("data");
                        userInfo = userData.getJSONArray("userInfo").toString();
                        strSessionList = new String[sessionIDList.length()];
                        for(int i=0;i<sessionIDList.length();i++){
                            strSessionList[i] = formatSessionStr(sessionIDList.getString(i));
                        }

                        //populate spinner
                        SearchActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, strSessionList);
                                searchSessionList.setAdapter(adapter);
                            }
                        });

                    }catch(Exception ex){
                        ex.printStackTrace();
                    }

                }else{
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SearchActivity.this.getBaseContext(), "Something wrong!!!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }

        private String formatSessionStr(String sessionID){

            int firstIndex = sessionID.indexOf('_');
            int secondIndex = sessionID.indexOf('_', firstIndex + 1);
            Log.d(Constants.CUSTOM_LOG_TYPE, "sessoinID->"+ sessionID + " first index->" + firstIndex + " second index-->" + secondIndex);
            String newSessionID = sessionID.substring(firstIndex+1, secondIndex);
            newSessionID = newSessionID.substring(0,4) + "-" + newSessionID.substring(4,6) + "-" + newSessionID.substring(6);
            //return newSessionID;
            return sessionID;
        }
        private void doProcessSearchForID(JSONObject response){
            //open HomeScreen Activity
            String status = "";
            try {
                status = response.getString("status");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (status.equals("success")) {

                //retrieve userid
                Object data = "";
                JSONArray waveData = null;
                try {
                    //data = response.get("USERDATA");
                    waveData = (JSONArray) response.get("BRAINWAVE");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                String id = searchID.getText().toString();
                Log.d(Constants.LOGIN_INTENT, "id-->" + id);
                Log.d(Constants.LOGIN_INTENT, "wavedata-->" + waveData);


                searchIntent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                searchIntent.putExtra("DATA", waveData.toString());
                searchIntent.putExtra("USERINFO", userInfo);
                startActivity(searchIntent);

            } else {
                //Toast.makeText(RegisterActivity.this, "Something wrong!!!", Toast.LENGTH_SHORT).show();
                SearchActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchActivity.this.getBaseContext(), "Something wrong!!!", Toast.LENGTH_LONG).show();
                    }
                });

            }

        }

    }
}
