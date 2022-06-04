package com.xiaofeng.GoSports.fragment.pushUp;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.utils.VoiceUtils;
import com.xuexiang.xpage.annotation.Page;

/**
 * @author xiaofeng
 * @description 俯卧撑计数器，调用手机传感器之距离传感器 人体靠近并远离即获得一次计数
 * @date 2022/6/4.
 */
@Page
public class PushUpActivity extends Activity {
    private static final String TAG = "PushUpActivity";
    /**
     * 俯卧撑界面的组件
     */
    TextView textview_push_up_count;
    Button btn_pushUp_start;
    /**
     * 俯卧撑需要的相关
     */
    boolean ifDown = false;
    boolean ifUp = false;
    boolean ifStart = false;

    SensorManager sm;
    Sensor mProximity;
    VoiceUtils voiceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_up);
        //初始化控件及相关工具类
        init();
        //初始化感应器相关
        initSensor();
        //注册监听类
        initListener();

    }

    private void init() {
        voiceUtils = new VoiceUtils(PushUpActivity.this);
        //绑定TextView组件
        textview_push_up_count = (TextView) findViewById(R.id.textview_push_up_count);
        btn_pushUp_start = (Button) findViewById(R.id.btn_pushUp_start);
    }

    private void initSensor() {
        //1.获取SensorManager管理对象
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        //2.获取据传感器对象
        mProximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //3.给Sensor注册监听事件
        sm.registerListener(listener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initListener() {
        /**
         * 为开始俯卧撑按钮进行注册监听器,当按下按钮是才开始监听
         */
        btn_pushUp_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceUtils.speakWords("开始俯卧撑！动起来！");
                ifStart = true;
            }
        });
    }

    //监听事件
    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (ifStart) {
                //获取event接收的数据
                float[] values = event.values;
                //获取距离传感器的距离
                float dis = values[0];
                //获取最大距离getMaximumRange（）,系统固定值
                float maxAccuracy = mProximity.getMaximumRange();
                if (dis <= maxAccuracy) {

                } else {

                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
