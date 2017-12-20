package com.nuaa.safedriving;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private SensorManager sensorManager;
    SweetAlertDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取控件
        start = (Button)findViewById(R.id.start);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);        //传感器


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });
    }

    //开始记录传感器数据
    public void startRecord(){
        String s = "缺少";

        if( sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null)
            s += "陀螺仪传感器";
        else if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null){
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),sensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
        if( sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)
            s += ",加速度传感器";
        else if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_NORMAL);
        }
        if(s.length() > 3){
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText(s);
            pDialog.setConfirmText("确定");
            pDialog.show();
        }

    }

    //传感器数据获取并保存
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            double all =  Math.sqrt((double) (x*x+y*y+z*z));
           // Log.i(TAG, "x轴方向:"+x+"\ty轴方向:"+y+"\tz轴方向:"+z+"\tall:" + (all));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
