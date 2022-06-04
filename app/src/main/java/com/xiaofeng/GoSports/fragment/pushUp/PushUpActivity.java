package com.xiaofeng.GoSports.fragment.pushUp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.trace.LBSTraceClient;
import com.xiaofeng.GoSports.Dao.DBOpenHelper;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.fragment.running.RunningActivity;
import com.xiaofeng.GoSports.utils.TimerUtils;
import com.xiaofeng.GoSports.utils.VoiceUtils;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xiaofeng.GoSports.utils.path.RecordUtil;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.strategy.impl.MaterialDialogStrategy;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.Timer;
import java.util.TimerTask;

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
    TextView TextView_pushUp_time;
    TextView TextView_pushUp_energy;
    Button btn_pushUp_start;
    Button btn_pushUp_count;
    Button btn_pushUp_end;
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
    private static int count = 0;
    private boolean isPause = false;
    private boolean isStop = true;
    private double energyCost = 0;
    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s
    private static final int UPDATE_TEXTVIEW = 0;
    /**
     * 俯卧撑需要的相关
     */
    int count_up_down = 0;

    long oldTime;
    long newTime;
    long timeInterval;
    boolean if_longTimeInterval = false;
    /**
     * 俯卧撑数据库记录类
     */
    private DialogLoader mDialogLoader;
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
        TextView_pushUp_time = (TextView) findViewById(R.id.TextView_pushUp_time);
        TextView_pushUp_energy = (TextView) findViewById(R.id.TextView_pushUp_energy);
        btn_pushUp_count = (Button) findViewById(R.id.btn_pushUp_count);
        btn_pushUp_start = (Button) findViewById(R.id.btn_pushUp_start);
        btn_pushUp_end = (Button) findViewById(R.id.btn_pushUp_end);
        //弹窗相关
        mDialogLoader = DialogLoader.getInstance().setIDialogStrategy(new MaterialDialogStrategy());
        //数据库相关
        dbOpenHelper = new DBOpenHelper(PushUpActivity.this);
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
                Log.v(TAG, "用户点击开始俯卧撑");
                if (isStop) {
                    isStop = !isStop;
                    beginTime_system = System.currentTimeMillis();
                    voiceUtils.speakWords("开始俯卧撑！动起来！");
                    startTimer();
                } else {
                    XToastUtils.toast("已经在运动啦！");
                }

            }
        });
        btn_pushUp_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStop) {
                    if (isPause) {
                        Log.i(TAG, "Resume");
                        XToastUtils.toast("继续俯卧撑....");
                        voiceUtils.speakWords("继续俯卧撑");
                    } else {
                        Log.i(TAG, "Pause");
                        XToastUtils.toast("暂停俯卧撑....");
                        voiceUtils.speakWords("暂停俯卧撑");
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
                        updateEnergyCost();
                        break;
                    default:
                        break;
                }
            }
        };
        btn_pushUp_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStop) {
                    /**
                     * 弹出对话框
                     */
                    mDialogLoader.showConfirmDialog(
                            PushUpActivity.this, "结束运动提示 \n 确认结束运动吗", "确认",
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
                                     * 将运动记录加入数据库中
                                     */
                                    dbOpenHelper.addPushUpRecord(String.valueOf(beginTime_system),
                                            String.valueOf(endTime_system),
                                            String.valueOf(energyCost),
                                            String.valueOf(count_up_down),
                                            String.valueOf(durationTime_system));
                                    btn_pushUp_count.setText("燃起来");
                                    TextView_pushUp_energy.setText("---");
                                    TextView_pushUp_time.setText(TimerUtils.timeConversion(0));
                                    XToast.normal(PushUpActivity.this, "运动结束").show();
                                    voiceUtils.speakWords("真棒!做了" + count_up_down + "个俯卧撑，消耗了" + energyCost + "卡的热量。");
                                    dialogInterface.dismiss();
                                }
                            }, "取消",
                            /**
                             * 点击了取消，运动继续
                             */
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isPause = !isPause;
                                    XToast.normal(PushUpActivity.this, "运动继续").show();
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
                    Log.i(TAG, "count: " + String.valueOf(count));
                    sendMessage(UPDATE_TEXTVIEW);
                    do {
                        try {
                            Log.i(TAG, "sleep(1000)...");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);
                    count++;
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
        count = 0;
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
        TextView_pushUp_time.setText(TimerUtils.timeConversion(count));
    }

    /**
     * 更新消耗卡路里
     */
    private void updateEnergyCost() {
        energyCost += (int) (Math.random() * 5);
        TextView_pushUp_energy.setText(energyCost + "卡");
    }

    //监听事件
    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!isStop && !isPause) {
                //获取event接收的数据
                float[] values = event.values;
                //获取距离传感器的距离
                float dis = values[0];
                //获取最大距离getMaximumRange（）,系统固定值
                float maxAccuracy = mProximity.getMaximumRange();
                if (dis <= maxAccuracy) {
                    //判断是否首次检测到目标下沉
                    if (oldTime == 0) {
                        oldTime = System.currentTimeMillis();
                    } else {
                        oldTime = newTime;
                        newTime = System.currentTimeMillis();
                        timeInterval = newTime - oldTime;
                        if (timeInterval > 250) {
                            if_longTimeInterval = true;
                        }
                    }
                }
                //获取到时间间隔较长
                if (if_longTimeInterval) {
                    count_up_down++;
                    btn_pushUp_count.setText(String.valueOf(count_up_down));
                    voiceUtils.speakWords(String.valueOf(count_up_down));
                    if_longTimeInterval = false;
                    switch (count_up_down) {
                        case 10:
                            voiceUtils.speakWords("太棒啦，已经做了十个，继续加油！");
                            break;
                        case 20:
                            voiceUtils.speakWords("太棒啦，已经做了二十个，继续加油！");
                            break;
                        case 30:
                            voiceUtils.speakWords("太棒啦，已经做了三十个，继续加油！");
                            break;
                        case 40:
                            voiceUtils.speakWords("太棒啦，已经做了四十个，继续加油！");
                            break;
                        case 50:
                            voiceUtils.speakWords("太棒啦，已经做了五十个，继续加油！");
                            break;
                    }
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
