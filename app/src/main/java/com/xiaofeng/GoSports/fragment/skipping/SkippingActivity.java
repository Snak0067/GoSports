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
public class SkippingActivity extends Activity implements SensorEventListener {
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
    private float mTimestamp; // 记录上次的时间戳
    private float mAngle[] = new float[3]; // 记录xyz三个方向上的旋转角度
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
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

//
//    //    创建监听器对象
//    SensorEventListener listener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            float[] values = event.values;
//            float x = values[0];
//            float y = values[1];
//            float z = values[2];
//
//            int coll = 18;   //作为一个标准值
////            判断在什么情况下要切换图片
//            if (Math.abs(x) > coll | Math.abs(y) > coll | Math.abs(z) > coll) {
//                /**
//                 * 300:摇晃了300毫秒之后，开始震动
//                 * 500：震动持续的时间，震动持续了500毫秒。
//                 * */
//                long[] pattern = {300, 500};
//                vibrator.vibrate(pattern, -1);
//                count++;
//                Log.d(TAG, "法生摇晃：" + count);
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
        //注册感光器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) { // 陀螺仪角度变更事件
            if (mTimestamp != 0) {
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;
                // x轴的旋转角度，手机平放桌上，然后绕侧边转动
                float angleX = (float) Math.toDegrees(mAngle[0]);
                // y轴的旋转角度，手机平放桌上，然后绕底边转动
                float angleY = (float) Math.toDegrees(mAngle[1]);
                // z轴的旋转角度，手机平放桌上，然后水平旋转
                float angleZ = (float) Math.toDegrees(mAngle[2]);
                int coll = 18;   //作为一个标准值
//            判断在什么情况下要切换图片
                if (Math.abs(angleZ) > coll) {
                    /**
                     * 300:摇晃了300毫秒之后，开始震动
                     * 500：震动持续的时间，震动持续了500毫秒。
                     * */
                    long[] pattern = {300, 300};
                    vibrator.vibrate(pattern, -1);
                    count++;
                    String desc = String.format("陀螺仪检测到当前\nx轴方向的转动角度为%f\ny轴方向的转动角度为%f\nz轴方向的转动角度为%f",
                            angleX, angleY, angleZ);
                    Log.d(TAG, desc);
                    Log.d(TAG, "发生摇晃：" + count);
                }
            }
            mTimestamp = event.timestamp;
        }
    }

    /**
     * 当传感器精度改变时回调该方法，一般无需处理
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
