package com.example.rubi.gyroclick;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import java.awt.AWTException;
//import java.awt.Robot;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//import java.awt.MouseInfo;
//import java.awt.Point;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Socket socket;
    private PrintWriter outStream;
    private Button rightbutton,leftbutton;
    private boolean isConnected;
    private Context activityContext;
    private GestureDetectorCompat mDetector;
    double axis[] = {0.0,0.0,0.0};
    double axis_acceleration[] = {0.0,0.0,0.0};
    double prev_x = 0.0;
    double prev_y = 0.0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        rightbutton = findViewById(R.id.rightbutton);
        activityContext  = this;
        ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
        connectPhoneTask.execute(ConnectionData.IP_ADDR);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            // Success! There's a magnetometer.
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);


        }
        else {
            Toast.makeText(activityContext,"No Sensor!",Toast.LENGTH_LONG).show();

        }
        rightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outStream.println("100,100");
                Toast.makeText(activityContext,"right click",Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,   mSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("aa", "pause");
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                InetAddress serverAddr = InetAddress.getByName(params[0]);
                socket = new Socket(serverAddr, ConnectionData.PORT);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e("remotedroid", "Error while connecting", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            isConnected = result;
            Toast.makeText(activityContext,isConnected?"Connected to server!":"Error while connecting",Toast.LENGTH_LONG).show();
            try {
                if(isConnected) {
                    outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                            .getOutputStream())), true); //create output stream to send data to server
                }
            }catch (IOException e){
                Log.e("remotedroid", "Error while creating OutWriter", e);
                Toast.makeText(activityContext,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event){
        final double alpha = 0.8;
        axis[0] = alpha * axis[0] + (1 - alpha) * event.values[0];
        axis[1] = alpha * axis[1] + (1 - alpha) * event.values[1];
        axis[2] = alpha * axis[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        axis_acceleration[0] = event.values[0] - axis[0];
        axis_acceleration[1] = event.values[1] - axis[1];
        axis_acceleration[2] = event.values[2] - axis[2];
        prev_x = axis_acceleration[0];
        prev_y = axis_acceleration[1];
        if (isConnected)
        {
            Toast.makeText(activityContext, String.valueOf(prev_x - axis_acceleration[0]) + "," + String.valueOf(prev_y - axis_acceleration[1]), Toast.LENGTH_LONG).show();
            Log.d("acceleration: ", "x: " + axis_acceleration[0] + " y: " + axis_acceleration[1] + " z: " + axis_acceleration[2]);
//            outStream.println(String.valueOf(prev_x - axis_acceleration[0]) + "," + String.valueOf(prev_y - axis_acceleration[1]));
            outStream.println(String.valueOf(event.values[0]+","+event.values[1]+","+event.values[2]));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}