package com.xiaofeng.GoSports.fragment.skipping;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaofeng.GoSports.Dao.DBOpenHelper;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.fragment.pushUp.PushUpActivity;
import com.xiaofeng.GoSports.utils.TimerUtils;
import com.xiaofeng.GoSports.utils.VoiceUtils;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.strategy.impl.MaterialDialogStrategy;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.Timer;
import java.util.TimerTask;

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
    private DialogLoader mDialogLoader;
    private float mTimestamp; // 记录上次的时间戳
    private float mAngle[] = new float[3]; // 记录xyz三个方向上的旋转角度
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
    private int tripleToOne = 0;
    int count_skipping = 0;
    /**
     * 震动器相关
     */
    private SensorManager sensorManager;
    private Sensor sensor;
    private Vibrator vibrator;
    private float angleX;
    private float angleY;
    private float angleZ;
    private float angleX_new;
    private float angleY_new;
    private float angleZ_new;
    /**
     * 数据库类
     */
    private DBOpenHelper dbOpenHelper;
    private long beginTime_system;
    private long endTime_system;
    private long durationTime_system;
    /**
     * 计时器相关
     */
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private boolean isPause = false;
    private boolean isStop = true;
    private double energyCost = 0;
    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s
    private static final int UPDATE_TEXTVIEW = 0;
    private static int count_time = 0;
    /**
     * 工具类
     */
    private VoiceUtils voiceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skipping);
        init();
        initSensor();
        initListener();
    }

    private void init() {
        TextView_skipping_time = (TextView) findViewById(R.id.TextView_skipping_time);
        TextView_skipping_energy = (TextView) findViewById(R.id.TextView_skipping_energy);
        btn_skipping_start = (Button) findViewById(R.id.btn_skipping_start);
        btn_skipping_count = (Button) findViewById(R.id.btn_skipping_count);
        btn_skipping_end = (Button) findViewById(R.id.btn_skipping_end);
        voiceUtils = new VoiceUtils(SkippingActivity.this);
        //弹窗相关
        mDialogLoader = DialogLoader.getInstance().setIDialogStrategy(new MaterialDialogStrategy());
        //数据库相关
        dbOpenHelper = new DBOpenHelper(SkippingActivity.this);
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
            if (mTimestamp != 0 && !isPause && !isStop) {
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;
                // x轴的旋转角度，手机平放桌上，然后绕侧边转动
                angleX_new = (float) Math.toDegrees(mAngle[0]);
                // y轴的旋转角度，手机平放桌上，然后绕底边转动
                angleY_new = (float) Math.toDegrees(mAngle[1]);
                // z轴的旋转角度，手机平放桌上，然后水平旋转
                angleZ_new = (float) Math.toDegrees(mAngle[2]);
                int coll = 30;   //作为一个标准值
                // 判断在什么情况下要计数
                if (Math.abs(angleX_new - angleX) > coll
                        || Math.abs(angleY_new - angleY) > coll
                        || Math.abs(angleZ_new - angleZ) > coll) {
                    tripleToOne++;
                    //进行上下偏移的两次计数转换为1次计数
                    if (tripleToOne % 3 == 0) {
                        /**
                         * 300:摇晃了300毫秒之后，开始震动
                         * 500：震动持续的时间，震动持续了500毫秒。
                         * */
                        long[] pattern = {300, 300};
                        vibrator.vibrate(pattern, -1);
                        count_skipping++;
                        btn_skipping_count.setText(String.valueOf(count_skipping));
                        updateEnergyCost();
                        String desc = String.format("陀螺仪检测到当前\nx轴方向的转动角度为%f\ny轴方向的转动角度为%f\nz轴方向的转动角度为%f",
                                angleX, angleY, angleZ);
                        Log.d(TAG, desc);
                        Log.d(TAG, "发生摇晃：" + count_skipping);
                        voiceUtils.speakWords(String.valueOf(count_skipping));
                    }
                }
                angleX = angleX_new;
                angleY = angleY_new;
                angleZ = angleZ_new;
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

    private void initListener() {
        /**
         * 点击开始按钮
         */
        btn_skipping_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "用户点击开始跳绳");
                if (isStop) {
                    isStop = !isStop;
                    beginTime_system = System.currentTimeMillis();
                    voiceUtils.speakWords("开始跳绳！跳起来！");
                    startTimer();
                } else {
                    XToastUtils.toast("已经在运动啦！");
                }
            }
        });
        /**
         * 点击暂停按钮（次数按钮）
         */
        btn_skipping_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStop) {
                    if (isPause) {
                        Log.i(TAG, "Resume");
                        XToastUtils.toast("继续跳绳....");
                        voiceUtils.speakWords("继续跳绳");
                    } else {
                        Log.i(TAG, "Pause");
                        XToastUtils.toast("暂停跳绳....");
                        voiceUtils.speakWords("暂停跳绳");
                    }
                    isPause = !isPause;
                } else {
                    voiceUtils.speakWords("运动未开始");
                }
            }
        });
        /**
         * handler
         */
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXTVIEW:
                        updateTime();
                        break;
                    default:
                        break;
                }
            }
        };
        btn_skipping_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStop) {
                    /**
                     * 弹出对话框
                     */
                    isPause = true;
                    mDialogLoader.showConfirmDialog(
                            SkippingActivity.this, "结束运动提示 \n 确认结束运动吗", "确认",
                            /**
                             * 点击了确认，运动结束
                             */
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isPause = false;
                                    isStop = true;
                                    stopTimer();
                                    endTime_system = System.currentTimeMillis();
                                    durationTime_system = endTime_system - beginTime_system;
                                    /**
                                     * 将跳绳的运动记录加入数据库中
                                     */
                                    dbOpenHelper.addSkippingRecord(String.valueOf(beginTime_system),
                                            String.valueOf(endTime_system),
                                            String.valueOf(energyCost),
                                            String.valueOf(count_skipping),
                                            String.valueOf(durationTime_system));
                                    btn_skipping_count.setText("燃起来");
                                    TextView_skipping_energy.setText("---");
                                    TextView_skipping_time.setText(TimerUtils.timeConversion(0));
                                    XToast.normal(SkippingActivity.this, "运动结束").show();
                                    voiceUtils.speakWords("真棒!跳了" + count_skipping + "下，消耗了" + energyCost + "卡的热量。");
                                    reInitData();
                                    dialogInterface.dismiss();
                                }
                            }, "取消",
                            /**
                             * 点击了取消，运动继续
                             */
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isPause = false;
                                    XToast.normal(SkippingActivity.this, "运动继续").show();
                                    voiceUtils.speakWords("运动继续");
                                    dialogInterface.dismiss();
                                }
                            });
                }
            }
        });

    }

    /**
     * 计时器开始计时
     */
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "计时: " + String.valueOf(count_time));
                    sendMessage(UPDATE_TEXTVIEW);
                    do {
                        try {
                            Log.i(TAG, "sleep(1000)...");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);
                    count_time++;
                }
            };
        }
        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, delay, period);
    }

    /**
     * 停止计时器
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        count_time = 0;
    }

    /**
     * 发送消息
     *
     * @param id
     */
    public void sendMessage(int id) {
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }

    /**
     * 更新时间
     */
    private void updateTime() {
        TextView_skipping_time.setText(TimerUtils.timeConversion(count_time));
    }

    /**
     * 更新消耗卡路里
     */
    private void updateEnergyCost() {
        energyCost += (int) (Math.random() * 3);
        TextView_skipping_energy.setText(energyCost + "卡");
    }

    private void reInitData() {
        count_time = 0;
        energyCost = 0;
        beginTime_system = 0;
        endTime_system = 0;
        durationTime_system = 0;
        isPause = false;
        isStop = true;
        angleX = 0;
        angleY = 0;
        angleZ = 0;
        angleX_new = 0;
        angleY_new = 0;
        angleZ_new = 0;
    }

}
