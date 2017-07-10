package com.neurosky.mindwavemobiledemo.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.neurosky.mindwavemobiledemo.model.SensorData;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Utils {

    public static boolean autoBond(Class btClass,BluetoothDevice device,String strPin) throws Exception {
    	Method autoBondMethod = btClass.getMethod("setPin",new Class[]{byte[].class});
    	Boolean result = (Boolean)autoBondMethod.invoke(device,new Object[]{strPin.getBytes()}); 
    	return result;
    }
    public static boolean createBond(Class btClass,BluetoothDevice device) throws Exception {
    	Method createBondMethod = btClass.getMethod("createBond"); 
    	Boolean returnValue = (Boolean) createBondMethod.invoke(device);
    	return returnValue.booleanValue();
    }
	public static  int getRawWaveValue(byte highOrderByte, byte lowOrderByte)
	 {
		   int hi = ((int)highOrderByte)& 0xFF;
		   int lo = ((int)lowOrderByte) & 0xFF;
		   return( (hi<<8) | lo );
	 }
    
	public static String byte2String( byte[] b) {  
		StringBuffer sb = new StringBuffer();
		   for (int i = 0; i < b.length; i++) { 
		     String hex = Integer.toHexString(b[i] & 0xFF); 
		     if (hex.length() == 1) { 
		       hex = '0' + hex; 
		     } 
		     sb.append(hex);
		   } 
		   return sb.toString().toLowerCase();
		}

	public static boolean validateData(SensorData dataObj){


		return true;
	}

	public static JSONObject processData(SensorData dataObj, String intent){

		//mock objects for now
		List<Integer> dataList = null;

		if(dataObj==null || dataObj.getdataList()==null || dataObj.getdataList().isEmpty()){

			dataList = new ArrayList<>();
			 for(int i=0;i<13648;i++){
				 dataList.add((int)Math.random()*100 + i);
			 }
		}else{
			dataList = dataObj.getdataList();
		}

		Log.d(Constants.CUSTOM_LOG_TYPE, dataList.toString());

		JSONObject inpJson = new JSONObject();
		try {
			inpJson.put("DATA", dataList);
			inpJson.put(Constants.INTENT_KEY, intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return inpJson;
    }

	public static void main(String args[]){

        List<List<Float>> xyList = new ArrayList<>();
        List<Float> xList = new ArrayList<Float>();
        List<Float> yList = new ArrayList<Float>();
        xList.add(1.0f);
        xList.add(2.0f);
        yList.add(3.0f);
        yList.add(3.0f);
        xyList.add(xList);
        xyList.add(yList);
        System.out.println(xyList.toString());

	}
}
