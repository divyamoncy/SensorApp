package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.content.Context;
import java.util.Date;
import java.util.List;
import java.io.FileOutputStream;

import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.hardware.SensorEventListener;
import java.io.File;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager mSensorManager;
    Long tsLong;
    String ts;
    int f=0;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorGyroscope;
    private Sensor mSensorLinear;
    BluetoothSocket mmSocket;
    long x;



    //Bluetooth connection settings.
    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static final int  REQUEST_ENABLE_BT = 2;

    //Checking if bluetooth is enabled.
    if(!bluetoothAdapter.isEnabled())


    {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is now on", Toast.LENGTH_SHORT);
    toast.show();

    } else {
    Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth was on already", Toast.LENGTH_SHORT);
    toast.show();
    }



    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
    String name=bluetoothAdapter.getName();

if(pairedDevices.size() > 0) {
// There are paired devices. Get the name and address of each paired device.
    for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
    String deviceHardwareAddress = device.getAddress();// MAC address
    Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_SHORT).show();
    Log.d("pairedDevices", deviceName);

    UUID uuid = device.getUuids()[0].getUuid();
try{
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    }





    // TextViews to display current sensor values
    private TextView errorbox1;
    private TextView mTextSensorAccelerometer;
    private TextView mTextSensorGyroscope;
    private TextView mTextSensorLinear;
    FileOutputStream outputStream,outputStream2,outputStream3;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mSensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Log.d("hello","manager done");
        List<Sensor> sensorList  =
                mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d("d",sensorList.toString());
        mTextSensorAccelerometer = (TextView) findViewById(R.id.label_light);
//        mTextSensorGyroscope=(TextView)findViewById(R.id.gyro) ;
//        mTextSensorLinear=(TextView)findViewById(R.id.linear);
        errorbox1=(TextView)findViewById(R.id.errorb);
        mSensorAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGyroscope= mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorLinear=mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        String sensor_error = getResources().getString(R.string.error_no_sensor);
        if (mSensorGyroscope == null) {
            Log.d("hello","nogyroscope");
        }
        if (mSensorLinear == null) {
            Log.d("hello","nolinear acc");
        }

        final Button button = (Button) findViewById(R.id.start);
        final Button button2 = (Button) findViewById(R.id.stop);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // your handler code here
                Context x=getBaseContext();
                startRecord(v,x);
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // your handler code here
                stopRecord(v);
            }
        });








    }



    public void stopRecord(View v) {
        try {

            mSensorManager.unregisterListener(this);

            outputStream.close();
            outputStream2.close();
            outputStream3.close();
        }
        catch(Exception e){
            errorbox1.setText(e.toString());
        }
    }

    public void startRecord(View v,Context context){
        // your handler code here
        try {

            tsLong = System.currentTimeMillis() / 1000;
            ts = tsLong.toString();
            File appFilesDirectory = new File(context.getExternalFilesDir("files"),ts+"acc.csv");
            String fileContents = "timestamp,millisecond,acc_x,acc_y,acc_z\n";

            File appFilesDirectory2 = new File(context.getExternalFilesDir("files"),ts+"gyro.csv");
            String fileContents2 = "timestamp,millisecond,gyro_x,gyro_y,gyro_z\n";

            File appFilesDirectory3= new File(context.getExternalFilesDir("files"),ts+"linear.csv");
            String fileContents3 = "timestamp,millisecond,linear_x,linear_y,linear_z\n";


            outputStream = new FileOutputStream(appFilesDirectory);
            outputStream.write(fileContents.getBytes());

            outputStream2 = new FileOutputStream(appFilesDirectory2);
            outputStream2.write(fileContents2.getBytes());

            outputStream3 = new FileOutputStream(appFilesDirectory3);
            outputStream3.write(fileContents3.getBytes());


           mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
           mSensorManager.registerListener(this, mSensorGyroscope,SensorManager.SENSOR_DELAY_GAME);
          mSensorManager.registerListener(this, mSensorLinear,SensorManager.SENSOR_DELAY_GAME);







        }
        catch (Exception e){
            errorbox1.setText(e.toString());
        }
    }



    @Override
    public void onSensorChanged (SensorEvent sensorEvent){
        try {

            Date currentTime = Calendar.getInstance().getTime();

            long timeInMillis = (new Date()).getTime()
                    + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
            if (f == 0) {
                x = timeInMillis;
                f = 1;
            }
            int sensorType = sensorEvent.sensor.getType();
            float accX = sensorEvent.values[0];
            float accY = sensorEvent.values[1];
            float accZ = sensorEvent.values[2];
            String s = currentTime + "," + (timeInMillis - x) + "," + accX + "," + accY + "," + accZ + "\n";
            switch (sensorType) {
                // Event came from the light sensor.
                case Sensor.TYPE_ACCELEROMETER:
                    // Handle light sensor
                    //Log.d("debug", "ACC" + s);
                    outputStream.write(s.getBytes());
                    break;
                case Sensor.TYPE_GYROSCOPE:
                   // Log.d("debug", "GYRO" + s);
                  outputStream2.write(s.getBytes());
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    //Log.d("debug", "LINEAR" + s);
                    outputStream3.write(s.getBytes());
                    break;
                default:
                    // do nothing
            }
            mTextSensorAccelerometer.setText(getResources().getString(
                    R.string.label_light, accX));
        } catch (Exception e) {
            errorbox1.setText(e.toString());
        }


    }


    @Override
    public void onAccuracyChanged (Sensor sensor,int i){

    }

    }

