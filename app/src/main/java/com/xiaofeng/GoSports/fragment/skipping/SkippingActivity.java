package com.xiaofeng.GoSports.fragment.skipping;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;

/**
 * @author xiaofeng
 * @description 跳绳主界面
 * @date 2022/6/4.
 */
@Page
public class SkippingActivity extends Activity {
    private static final String TAG = "SkippingActivity";
    /**
     * 界面控件相关
     */
    private TextView TextView_skipping_time;
    private TextView TextView_skipping_energy;
    private Button btn_skipping_start;
    private Button btn_skipping_count;
    private Button btn_skipping_end;
    /**
     * 跳绳数据相关
     */
    private int count = 0;
    /**
     * 震动器相关
     */
    private SensorManager sensorManager;
    private Sensor sensor;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skipping);
        init();
        initSensor();

    }

    private void init() {
        TextView_skipping_time = (TextView) findViewById(R.id.TextView_skipping_tip_time);
        TextView_skipping_energy = (TextView) findViewById(R.id.TextView_skipping_tip_energy);
        btn_skipping_start = (Button) findViewById(R.id.btn_skipping_start);
        btn_skipping_count = (Button) findViewById(R.id.btn_skipping_count);
        btn_skipping_end = (Button) findViewById(R.id.btn_skipping_end);
    }

    /**
     * 初始化震动传感器相关
     */
    private void initSensor() {
        //1.获取传感器服务管理对象
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //2.获取传感器对象
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //3.震动服务对象
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }


    //    创建监听器对象
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            int coll = 18;   //作为一个标准值
//            判断在什么情况下要切换图片
            if (Math.abs(x) > coll | Math.abs(y) > coll | Math.abs(z) > coll) {
                /**
                 * 300:摇晃了300毫秒之后，开始震动
                 * 500：震动持续的时间，震动持续了500毫秒。
                 * */
                long[] pattern = {300, 500};
                vibrator.vibrate(pattern, -1);
                count++;
                Log.d(TAG, "法生摇晃：" + count);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);


    }
}
