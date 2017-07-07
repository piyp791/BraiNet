package com.neurosky.mindwavemobiledemo.activity;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.EEGPower;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;
import com.neurosky.mindwavemobiledemo.R;
import com.neurosky.mindwavemobiledemo.helper.Constants;
import com.neurosky.mindwavemobiledemo.helper.WebRequestHelper;
import com.neurosky.mindwavemobiledemo.helper.Utils;
import com.neurosky.mindwavemobiledemo.model.SensorData;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * This activity is the man entry of this app. It demonstrates the usage of 
 * (1) TgStreamReader.redirectConsoleLogToDocumentFolder()
 * (2) TgStreamReader.stopConsoleLog()
 * (3) demo of getVersion
 */
public class DemoActivity extends Activity {
	private static final String TAG = BluetoothDeviceDemoActivity.class.getSimpleName();
	private TgStreamReader tgStreamReader;

	// TODO connection sdk
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private String address = null;
	private WebRequestHelper webRequestHelper;
	String useIntent="";
	String userID = "";
	String resultID = "";
	String sessionID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main_view);

		Intent intent = getIntent();
		useIntent = intent.getStringExtra(Constants.INTENT_KEY);
		Log.d(Constants.CUSTOM_LOG_TYPE, "message->" +useIntent);
		userID = intent.getStringExtra("ID");
		Log.d(Constants.CUSTOM_LOG_TYPE, "user ID->" +userID);

		sessionID = intent.getStringExtra("SESSIONID");
		Log.d(Constants.CUSTOM_LOG_TYPE, "session ID->" +sessionID);

		//Modification : PP-> instantiate uploadshelper class
		webRequestHelper = WebRequestHelper.getInstance();

		initView();
		setUpDrawWaveView();

		try {
			// TODO
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
				Toast.makeText(
						this,
						"Please enable your Bluetooth and re-run this program !",
						Toast.LENGTH_LONG).show();
				finish();
//				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "error:" + e.getMessage());
			return;
		}

		Log.d(Constants.CUSTOM_LOG_TYPE, "oncreate");
	}

	private Button btn_start = null;
	private Button btn_stop = null;
	private Button btn_selectdevice = null;
	private Button btn_timed_record = null;
	private LinearLayout wave_layout;

	private int badPacketCount = 0;

	private void initView() {



		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_timed_record = (Button)findViewById(R.id.timed_record);

		wave_layout = (LinearLayout) findViewById(R.id.wave_layout);

		btn_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				badPacketCount = 0;
				showToast("connecting ...",Toast.LENGTH_SHORT);
				dataObj = null;
				dataObj = new SensorData();
				start();
			}
		});


		btn_timed_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				badPacketCount = 0;
				dataObj = null;
				dataObj = new SensorData();


				final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

				final Runnable task = new Runnable() {
					public void run() {
						//System.out.println("function started"+System.currentTimeMillis());
						//start_new();
						start();
					}
				};
				final ScheduledFuture<?> handle =
						scheduler.scheduleAtFixedRate(task, 1, 15, TimeUnit.SECONDS);
				scheduler.schedule(new Runnable() {
					public void run() {
						System.out.println("function stopped?"+System.currentTimeMillis());
						handle.cancel(true);
						if(tgStreamReader != null){
							tgStreamReader.stop();
						}

						Log.d(Constants.CUSTOM_LOG_TYPE, dataObj.getdataList().toString());
					}
				}, 15, SECONDS);

			}
		});

		btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(tgStreamReader != null){
					tgStreamReader.stop();
				}

                if(Utils.validateData(dataObj)){

                    //send data to the server
					JSONObject inpJson = Utils.processData(dataObj, useIntent);
					try {
						inpJson.put("ID", userID);
						inpJson.put("SESSIONID", sessionID);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					//webRequestHelper.doPost(inpJson.toString());
					final AuthenticateBrainWaveTask insertDataTask = new AuthenticateBrainWaveTask(DemoActivity.this);
					try {
						insertDataTask.execute(inpJson.toString(), useIntent);

					} catch (Exception ex) {
						ex.printStackTrace();
					}


                }else{
                    //data not valid!!!!
                    //data not to be sent to the server
                    //throw pop pop up
                    showToast("Data not validated", Toast.LENGTH_SHORT);
                }

				//Modification : PP-> clear the data object
				//dataObj = null;
				//showToast("New Sensor Data Object nulled !!", Toast.LENGTH_SHORT);
            }

		});

		btn_selectdevice =  (Button) findViewById(R.id.btn_selectdevice);

		btn_selectdevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				scanDevice();
			}

		});

		Log.d(Constants.CUSTOM_LOG_TYPE, "init view");
	}

	private void start(){
		if(address != null){
			BluetoothDevice bd = mBluetoothAdapter.getRemoteDevice(address);
			createStreamReader(bd);

            //Modification : PP-> instantiate new sensor data object

            showToast("New Sensor Data Object instantiated", Toast.LENGTH_SHORT);

			tgStreamReader.connectAndStart();
		}else{
			showToast("Please select device first!", Toast.LENGTH_SHORT);
		}

		Log.d(Constants.CUSTOM_LOG_TYPE, "start");
	}


	private void start_new(){

		showToast("function called!", Toast.LENGTH_SHORT);
	}

	public void stop() {
		if(tgStreamReader != null){
			tgStreamReader.stop();
			tgStreamReader.close();//if there is not stop cmd, please call close() or the data will accumulate
			tgStreamReader = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(tgStreamReader != null){
			tgStreamReader.close();
			tgStreamReader = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		stop();
	}

	// TODO view
	DrawWaveView waveView = null;

    //Modification : PP-> Sensor data object
    SensorData dataObj = null;

	// (2) demo of drawing ECG, set up of view
	public void setUpDrawWaveView() {

        /*
        //Modification : PP-> instantiate new sensor data object
        dataObj = new SensorData();
        */

		waveView = new DrawWaveView(getApplicationContext());
		wave_layout.addView(waveView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		waveView.setValue(9999, 9999, -9999);

		Log.d(Constants.CUSTOM_LOG_TYPE, "set up and draw");
	}
	// (2) demo of drawing ECG, update view
	public void updateWaveView(int data) {
		if (waveView != null) {
			waveView.updateData(data);

            //Modification : PP-> save this data in the
            dataObj.addData(data);
		}

		Log.d(Constants.CUSTOM_LOG_TYPE, "update view");
	}
	private int currentState = 0;
	private TgStreamHandler callback = new TgStreamHandler() {

		@Override
		public void onStatesChanged(int connectionStates) {
			// TODO Auto-generated method stub
			Log.d(TAG, "connectionStates change to: " + connectionStates);
			currentState  = connectionStates;
			switch (connectionStates) {
				case ConnectionStates.STATE_CONNECTED:
					//sensor.start();
					showToast("Connected", Toast.LENGTH_SHORT);
					break;
				case ConnectionStates.STATE_WORKING:
					//byte[] cmd = new byte[1];
					//cmd[0] = 's';
					//tgStreamReader.sendCommandtoDevice(cmd);
					LinkDetectedHandler.sendEmptyMessageDelayed(1234, 5000);
					break;
				case ConnectionStates.STATE_GET_DATA_TIME_OUT:
					//get data time out
					break;
				case ConnectionStates.STATE_COMPLETE:
					//read file complete
					break;
				case ConnectionStates.STATE_STOPPED:
					break;
				case ConnectionStates.STATE_DISCONNECTED:
					break;
				case ConnectionStates.STATE_ERROR:
					Log.d(TAG,"Connect error, Please try again!");
					break;
				case ConnectionStates.STATE_FAILED:
					Log.d(TAG,"Connect failed, Please try again!");
					break;
			}
			Message msg = LinkDetectedHandler.obtainMessage();
			msg.what = MSG_UPDATE_STATE;
			msg.arg1 = connectionStates;
			LinkDetectedHandler.sendMessage(msg);

			Log.d(Constants.CUSTOM_LOG_TYPE, "callback");
		}

		@Override
		public void onRecordFail(int a) {
			// TODO Auto-generated method stub
			Log.e(TAG,"onRecordFail: " +a);

		}

		@Override
		public void onChecksumFail(byte[] payload, int length, int checksum) {
			// TODO Auto-generated method stub

			badPacketCount ++;
			Message msg = LinkDetectedHandler.obtainMessage();
			msg.what = MSG_UPDATE_BAD_PACKET;
			msg.arg1 = badPacketCount;
			LinkDetectedHandler.sendMessage(msg);

			Log.d(Constants.CUSTOM_LOG_TYPE, "cecksum fail");

		}

		@Override
		public void onDataReceived(int datatype, int data, Object obj) {
			// TODO Auto-generated method stub
			Message msg = LinkDetectedHandler.obtainMessage();
			msg.what = datatype;
			msg.arg1 = data;
			msg.obj = obj;
			LinkDetectedHandler.sendMessage(msg);
			//Log.i(TAG,"onDataReceived");

			Log.d(Constants.CUSTOM_LOG_TYPE, "on data received");
		}

	};

	private boolean isPressing = false;
	private static final int MSG_UPDATE_BAD_PACKET = 1001;
	private static final int MSG_UPDATE_STATE = 1002;
	private static final int MSG_CONNECT = 1003;
	private boolean isReadFilter = false;

	int raw;
	private Handler LinkDetectedHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case 1234:
					tgStreamReader.MWM15_getFilterType();
					isReadFilter = true;
					//Log.d(TAG,"MWM15_getFilterType ");

					break;
				case 1235:
					tgStreamReader.MWM15_setFilterType(MindDataType.FilterType.FILTER_60HZ);
					//Log.d(TAG,"MWM15_setFilter  60HZ");
					LinkDetectedHandler.sendEmptyMessageDelayed(1237, 1000);
					break;
				case 1236:
					tgStreamReader.MWM15_setFilterType(MindDataType.FilterType.FILTER_50HZ);
					//Log.d(TAG,"MWM15_SetFilter 50HZ ");
					LinkDetectedHandler.sendEmptyMessageDelayed(1237, 1000);
					break;

				case 1237:
					tgStreamReader.MWM15_getFilterType();
					//Log.d(TAG,"MWM15_getFilterType ");

					break;

				case MindDataType.CODE_FILTER_TYPE:
					//Log.d(TAG,"CODE_FILTER_TYPE: " + msg.arg1 + "  isReadFilter: " + isReadFilter);
					if(isReadFilter){
						isReadFilter = false;
						if(msg.arg1 == MindDataType.FilterType.FILTER_50HZ.getValue()){
							LinkDetectedHandler.sendEmptyMessageDelayed(1235, 1000);
						}else if(msg.arg1 == MindDataType.FilterType.FILTER_60HZ.getValue()){
							LinkDetectedHandler.sendEmptyMessageDelayed(1236, 1000);
						}else{
							Log.e(TAG,"Error filter type");
						}
					}

					break;



				case MindDataType.CODE_RAW:
					updateWaveView(msg.arg1);
					break;
				case MindDataType.CODE_MEDITATION:
					//Log.d(TAG, "HeadDataType.CODE_MEDITATION " + msg.arg1);

					break;
				case MindDataType.CODE_ATTENTION:
					//Log.d(TAG, "CODE_ATTENTION " + msg.arg1);

					break;
				case MindDataType.CODE_EEGPOWER:
					EEGPower power = (EEGPower)msg.obj;
					if(power.isValidate()){

					}
					break;
				case MindDataType.CODE_POOR_SIGNAL://
					int poorSignal = msg.arg1;
					//Log.d(TAG, "poorSignal:" + poorSignal);


					break;
				case MSG_UPDATE_BAD_PACKET:


					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};


	public void showToast(final String msg,final int timeStyle){
		DemoActivity.this.runOnUiThread(new Runnable()
		{
			public void run()
			{
				Toast.makeText(getApplicationContext(), msg, timeStyle).show();
			}

		});
	}

	//show device list while scanning
	private ListView list_select;
	private BTDeviceListAdapter deviceListApapter = null;
	private Dialog selectDialog;

	// (3) Demo of getting Bluetooth device dynamically
	public void scanDevice(){

		if(mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}

		setUpDeviceListView();
		//register the receiver for scanning
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		mBluetoothAdapter.startDiscovery();

		Log.d(Constants.CUSTOM_LOG_TYPE, "oncreate");
	}

	private void setUpDeviceListView(){

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_device, null);
		list_select = (ListView) view.findViewById(R.id.list_select);
		selectDialog = new Dialog(this, R.style.dialog1);
		selectDialog.setContentView(view);
		//List device dialog

		deviceListApapter = new BTDeviceListAdapter(this);
		list_select.setAdapter(deviceListApapter);
		list_select.setOnItemClickListener(selectDeviceItemClickListener);

		selectDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				Log.e(TAG,"onCancel called!");
				DemoActivity.this.unregisterReceiver(mReceiver);
			}

		});

		selectDialog.show();

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice device: pairedDevices){
			deviceListApapter.addDevice(device);
		}
		deviceListApapter.notifyDataSetChanged();
	}

	//Select device operation
	private AdapterView.OnItemClickListener selectDeviceItemClickListener = new AdapterView.OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Rico ####  list_select onItemClick     ");
			if(mBluetoothAdapter.isDiscovering()){
				mBluetoothAdapter.cancelDiscovery();
			}
			//unregister receiver
			DemoActivity.this.unregisterReceiver(mReceiver);

			mBluetoothDevice =deviceListApapter.getDevice(arg2);
			selectDialog.dismiss();
			selectDialog = null;

			Log.d(TAG,"onItemClick name: "+mBluetoothDevice.getName() + " , address: " + mBluetoothDevice.getAddress() );
			address = mBluetoothDevice.getAddress().toString();

			//ger remote device
			BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mBluetoothDevice.getAddress().toString());

			/*
			tgStreamReader = createStreamReader(remoteDevice);
			tgStreamReader.connectAndStart();
			dataObj = null;
			dataObj = new SensorData();
			Toast.makeText(DemoActivity.this, "instantiating new object", Toast.LENGTH_SHORT).show();*/

			Log.d(Constants.CUSTOM_LOG_TYPE, "on item click");

		}

	};

	/**
	 * If the TgStreamReader is created, just change the bluetooth
	 * else create TgStreamReader, set data receiver, TgStreamHandler and parser
	 * @param bd
	 * @return TgStreamReader
	 */
	public TgStreamReader createStreamReader(BluetoothDevice bd){

		if(tgStreamReader == null){
			// Example of constructor public TgStreamReader(BluetoothDevice mBluetoothDevice,TgStreamHandler tgStreamHandler)
			tgStreamReader = new TgStreamReader(bd,callback);
			tgStreamReader.startLog();
		}else{
			// (1) Demo of changeBluetoothDevice
			tgStreamReader.changeBluetoothDevice(bd);

			// (4) Demo of setTgStreamHandler, you can change the data handler by this function
			tgStreamReader.setTgStreamHandler(callback);
		}

		Log.d(Constants.CUSTOM_LOG_TYPE, "create steam reader");
		return tgStreamReader;
	}

	/**
	 * Check whether the given device is bonded, if not, bond it
	 * @param bd
	 */
	public void bindToDevice(BluetoothDevice bd){
		int ispaired = 0;
		if(bd.getBondState() != BluetoothDevice.BOND_BONDED){
			//ispaired = remoteDevice.createBond();
			try {
				//Set pin
				if(Utils.autoBond(bd.getClass(), bd, "0000")){
					ispaired += 1;
				}
				//bind to device
				if(Utils.createBond(bd.getClass(), bd)){
					ispaired += 2;
				}
				Method createCancelMethod=BluetoothDevice.class.getMethod("cancelBondProcess");
				boolean bool=(Boolean)createCancelMethod.invoke(bd);
				Log.d(TAG,"bool="+bool);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, " paire device Exception:    " + e.toString());
			}
		}
		Log.d(TAG, " ispaired:    " + ispaired);

	}

	//The BroadcastReceiver that listens for discovered devices
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "mReceiver()");
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d(TAG,"mReceiver found device: " + device.getName());

				// update to UI
				deviceListApapter.addDevice(device);
				deviceListApapter.notifyDataSetChanged();

			}
		}

	};



	private class AuthenticateBrainWaveTask extends AsyncTask<String, Integer, String> {


		private Context context;

		public AuthenticateBrainWaveTask(Context context) {
			this.context = context;
		}


		@Override
		protected String doInBackground(String... param) {

			String userData = param[0];
			final String intent = param[1];

			JSONObject jsonObj = new JSONObject();
			JSONObject dataObj = null;
			try {
				dataObj = new JSONObject(userData);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			try{
				//jsonObj.put("DATA", dataObj);
				//jsonObj.put(Constants.INTENT_KEY, intent);
			}catch(Exception ex){
				ex.printStackTrace();
			}

			WebRequestHelper.get("/sendWave/" +dataObj.toString(), null, new JsonHttpResponseHandler() {
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

						//check if valid brainwave
						if(intent.equalsIgnoreCase(Constants.LOGIN_INTENT)){
							try {
								resultID = response.getString("ID");
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}

						DemoActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(DemoActivity.this.getBaseContext(), "Result ID:::" + resultID, Toast.LENGTH_LONG).show();
							}
						});

					}else{

						DemoActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(DemoActivity.this.getBaseContext(), "Something wrong!!!", Toast.LENGTH_LONG).show();
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


		}

	}
}
