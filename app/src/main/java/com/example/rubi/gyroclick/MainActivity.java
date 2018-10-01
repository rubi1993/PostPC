package com.example.rubi.gyroclick;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
//import java.awt.AWTException;
//import java.awt.Robot;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//import java.awt.MouseInfo;
//import java.awt.Point;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    EditText editText;
    private Socket socket;
    private PrintWriter outStream;
    private Button rightbutton,leftbutton;
    private ImageButton keyboardbutton, pauseButton;
    private ImageView pauseActivityButton;
    private boolean isConnected;
    private Context activityContext;
    private GestureDetectorCompat mDetector;
    double axis[] = {0.0,0.0,0.0};
    double axis_acceleration[] = {0.0,0.0,0.0};
    double prev_x = 0.0;
    double prev_y = 0.0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isLongPress=false;
    private boolean isPaused=false;
    private RelativeLayout pauseLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate for MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        rightbutton = findViewById(R.id.rightbutton);
        leftbutton = findViewById(R.id.leftbutton);
        editText= findViewById(R.id.hiddenTxt);
        keyboardbutton= findViewById(R.id.keyboardButton);
        pauseButton = findViewById(R.id.pauseButton);
        pauseLayout = findViewById(R.id.pauseLayout);
        pauseActivityButton = findViewById(R.id.pauseActivityButton);
        activityContext  = this;
        final ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();


        Intent intent = getIntent();
        String value = intent.getStringExtra("connection_value"); //if it's a string you stored.
        if (value != null) {
            String[] substring = value.split(":");
            ConnectionData.IP_ADDR = substring[1];
            ConnectionData.PORT = Integer.parseInt(substring[2]);
            System.out.println("ConnectionData: " + ConnectionData.IP_ADDR + ":" + ConnectionData.PORT);

        }


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
                outStream.println("right_click");
            }
        });
        rightbutton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                outStream.println("right_click_long");
                isLongPress=true;
                return true;
            }

        });
        rightbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(isLongPress){
                        outStream.println("right_click_stop");
                        isLongPress=false;
                    }
                }
                return false;
            }
        });

        leftbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outStream.println("left_click");
            }
        });
        leftbutton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    outStream.println("left_click_long");
                    isLongPress=true;
                return true;
            }

        });
        leftbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(isLongPress){
                        outStream.println("left_click_stop");
                        isLongPress=false;
                    }
                }
                return false;
            }
        });
        keyboardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(v);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPaused) {
                    // user wants to pause now
                    rightbutton.setEnabled(false);
                    leftbutton.setEnabled(false);

                    keyboardbutton.setClickable(false);
                    pauseLayout.setVisibility(RelativeLayout.VISIBLE);
                    isPaused = true;
                } else {
                    rightbutton.setEnabled(true);
                    leftbutton.setEnabled(true);
                    keyboardbutton.setClickable(true);
                    pauseButton.setClickable(true);
                    pauseLayout.setVisibility(RelativeLayout.INVISIBLE);
                    isPaused = false;
                }
            }
        });
        pauseActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // user wants to resume
                rightbutton.setEnabled(true);
                leftbutton.setEnabled(true);
                keyboardbutton.setClickable(true);
                pauseLayout.setVisibility(RelativeLayout.INVISIBLE);
                isPaused = false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            String curString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Keyboard s: " + s.toString() + ", start: " + start + ", before: " + before + ", count: " + count);
                if (start == 0 && count == 0) {
                    return;
                } else if (count == 0) {
                    outStream.println("backspace");
                    return;
                }
                if (s.charAt(count - 1) == ' ') {
                    outStream.println("space");
                    return;
                }
                if (s.length() < curString.length()) {
                    // user delete
                    outStream.println("backspace");
                } else if (s.length() > curString.length()) {
                    outStream.println(s.charAt(count - 1));
                }
                curString = s.toString();
//                if(s.length()==1){
//                    outStream.println(s.toString());
//                } else if (s.length() > 1) {
//                    outStream.println(s.toString().substring(s.toString().length() - 1));
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                s.clear();
            }
        });
    }

    public void showKeyboard(View v){
        String charToSend;
        if(editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(activityContext.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//            charToSend=editText.getText().toString();
//            Toast.makeText(activityContext,charToSend,Toast.LENGTH_LONG).show();
//            if(charToSend!="" && charToSend.length()==1){
//                outStream.println(charToSend);
//            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myIntent = new Intent(MainActivity.this, QRScanner.class);
        MainActivity.this.startActivity(myIntent);
        outStream.close();
        finish();
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
        if (isPaused) {
            return;
        }
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
//            Toast.makeText(activityContext, String.valueOf(prev_x - axis_acceleration[0]) + "," + String.valueOf(prev_y - axis_acceleration[1]), Toast.LENGTH_LONG).show();
//            Log.d("acceleration: ", "x: " + axis_acceleration[0] + " y: " + axis_acceleration[1] + " z: " + axis_acceleration[2]);
//            outStream.println(String.valueOf(prev_x - axis_acceleration[0]) + "," + String.valueOf(prev_y - axis_acceleration[1]));
            outStream.println(String.valueOf(event.values[0]+","+event.values[1]+","+event.values[2]));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}