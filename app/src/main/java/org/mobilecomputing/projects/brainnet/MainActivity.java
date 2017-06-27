package org.mobilecomputing.projects.brainnet;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity{


    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    ToggleButton bluetoothToggle;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    Button connect;
    private Socket client;
    private PrintWriter printwriter;
    private EditText textField;
    private Button button;
    private String messsage;
    int stopThread = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (checkPermissions()){
            // permissions granted.



        } else {
            // show dialog informing them that we lack certain permissions
        }


        this.bluetoothToggle = (ToggleButton)findViewById(R.id.bltSwitch) ;
        this.bluetoothToggle.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                toggleBluetooth(isChecked);
            }
        }) ;

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);



        connect = (Button)findViewById(R.id.connect);

        connect.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new ConnectToServer().execute();
            }
        });

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(Constants.CUSTOM_LOG_TYPE, "action :" +action);

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Log.d(Constants.CUSTOM_LOG_TYPE, "Device discovery started");

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Log.d(Constants.CUSTOM_LOG_TYPE, "Finished scanning devices");

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(Constants.CUSTOM_LOG_TYPE, "Found device " + device.getName());
            }
        }

    };


    private class ConnectToServer extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            /** Define a host server */
            String host = Constants.HOST;
            /** Define a port */
            int port = Constants.PORT;

            StringBuffer instr = new StringBuffer();
            String TimeStamp;
            Log.d(Constants.CUSTOM_LOG_TYPE, "SocketClient initialized");

            try {
                /** Obtain an address object of the server */
                InetAddress address = InetAddress.getByName(host);
                /** Establish a socket connetion */
                Socket connection = new Socket(address, port);
                /** Instantiate a BufferedOutputStream object */

                BufferedOutputStream bos = new BufferedOutputStream(connection.
                        getOutputStream());

                /** Instantiate an OutputStreamWriter object with the optional character
                 * encoding.
                 */
                OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");

                TimeStamp = new java.util.Date().toString();
                String process = "Calling the Socket Server on "+ host + " port " + port +
                        " at " + TimeStamp +  (char) 13;

                /** Write across the socket connection and flush the buffer */
                osw.write(process);
                osw.flush();

                /** Instantiate a BufferedInputStream object for reading
                 /** Instantiate a BufferedInputStream object for reading
                 * incoming socket streams.
                 */

                BufferedInputStream bis = new BufferedInputStream(connection.
                        getInputStream());
                /**Instantiate an InputStreamReader with the optional
                 * character encoding.
                 */

                InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");

                /**Read the socket's InputStream and append to a StringBuffer */
                int c;
                while ( (c = isr.read()) != 13)
                    instr.append( (char) c);

                /** Close the socket connection. */
                connection.close();
                Log.d(Constants.CUSTOM_LOG_TYPE, "server message-->" +instr);
            }
            catch (IOException f) {
                System.out.println("IOException: " + f);
            }
            catch (Exception g) {
                System.out.println("Exception: " + g);
            }

            return null;
        }
    }


    private void toggleBluetooth(boolean isChecked){
        //toggle bluetooth
        Log.d(Constants.CUSTOM_LOG_TYPE, "toggle button ->" +isChecked);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!isChecked && mBluetoothAdapter.isEnabled()) {
            boolean isDisabled = mBluetoothAdapter.disable();
            Log.d(Constants.CUSTOM_LOG_TYPE, "disabled bluetooth ?" +isDisabled);
        }else if(isChecked && !mBluetoothAdapter.isEnabled()){
            boolean isEnabled = mBluetoothAdapter.enable();
            Log.d(Constants.CUSTOM_LOG_TYPE, "enabled bluetooth ?" +isEnabled);
            discoverDevices();
            //findBT();
        }
    }


    public void discoverDevices(){

        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        boolean result = mBluetoothAdapter.startDiscovery();
        Log.d(Constants.CUSTOM_LOG_TYPE, "Device discovery started ? " + result);

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:Constants.permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Log.d(Constants.CUSTOM_LOG_TYPE, "No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("Samsung"))
                {
                    Log.d(Constants.CUSTOM_LOG_TYPE, "device address->" +device.getAddress());
                    mmDevice = device;
                    break;
                }
            }
        }
        Log.d(Constants.CUSTOM_LOG_TYPE, "Bluetooth Device Found");
    }

    void sendData() throws IOException
    {
        /*String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                    Log.d(Constants.CUSTOM_LOG_TYPE, "Permission granted!!");

                } else {
                    // no permissions granted.
                }
                return;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
}