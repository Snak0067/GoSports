package com.xiaofeng.GoSports.fragment.running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.fragment.Walking.RecordActivity;
import com.xiaofeng.GoSports.fragment.Walking.WalkingActivity;
import com.xiaofeng.GoSports.utils.SettingUtils;
import com.xiaofeng.GoSports.utils.TimerUtils;
import com.xiaofeng.GoSports.utils.VoiceUtils;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xiaofeng.GoSports.utils.path.DbAdapter;
import com.xiaofeng.GoSports.utils.path.PathRecord;
import com.xiaofeng.GoSports.utils.path.RecordUtil;
import com.xiaofeng.GoSports.utils.sms.SendSmsUtil;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.strategy.impl.MaterialDialogStrategy;
import com.xuexiang.xui.widget.toast.XToast;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RunningActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, TraceListener {
    MapView mMapView = null;
    /**
     * 程序调试信息
     */
    private static final String TAG = "RunningActivity";
    /**
     * 设置对话框策略
     */
    private DialogLoader mDialogLoader;
    /**
     * 运动状态相关
     */
    private int steps = 0;
    /**
     * 计时器相关
     */
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private static int count = 0;
    private boolean isPause = false;
    private boolean isStop = true;
    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s
    private static final int UPDATE_TEXTVIEW = 0;
    /**
     * 语音播报
     */
    VoiceUtils voiceUtils;
    //初始化地图控制器对象
    AMap aMap;
    //初始化布局控件
    Button btn_start, btn_stop, btn_pause;
    TextView TextView_speed, TextView_heartRate, TextView_steps, TextView_time, TextView_distance;

    //定位需要的数据
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;

    //定位蓝点
    MyLocationStyle myLocationStyle;
    /**
     * 轨迹显示相关
     */
    private PolylineOptions mPolyoptions, tracePolytion;
    private Polyline mpolyline;
    private PathRecord record;
    private TraceOverlay mTraceoverlay;
    private List<TraceLocation> mTracelocationlist = new ArrayList<TraceLocation>();
    private List<TraceOverlay> mOverlayList = new ArrayList<TraceOverlay>();
    private List<AMapLocation> recordList = new ArrayList<AMapLocation>();
    private int tracesize = 30;
    private int mDistance = 0;
    private Marker mlocMarker;
    /**
     * 轨迹数据库帮助类
     */
    private DbAdapter DbHepler;
    /**
     * 时间记录类
     */
    private long mStartTime;
    private long mEndTime;
    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = true;

    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 28 && getApplicationContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,//访问粗定位
                    Manifest.permission.ACCESS_FINE_LOCATION,//获得好位置
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
                    Manifest.permission.READ_EXTERNAL_STORAGE,//读取外部存储器
                    Manifest.permission.READ_PHONE_STATE,//读取手机状态
                    BACK_LOCATION_PERMISSION//回位置的许可
            };
            needCheckBackLocation = true;
        }
        setContentView(R.layout.activity_running);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        init();

        initpolyline();
    }

    private void init() {
        if (aMap == null) aMap = mMapView.getMap();
        /**
         * 设置控件
         */
        TextView_distance = (TextView) findViewById(R.id.textView_distance);
        TextView_time = (TextView) findViewById(R.id.TextView_time);
        TextView_steps = (TextView) findViewById(R.id.TextView_steps);
        TextView_heartRate = (TextView) findViewById(R.id.TextView_heartRate);
        TextView_speed = (TextView) findViewById(R.id.TextView_speed);
        btn_start = (Button) findViewById(R.id.btn_running_start);
        btn_stop = (Button) findViewById(R.id.btn_running_stop);
        btn_pause = (Button) findViewById(R.id.btn_running_pause);
        /**
         * 设置监听启动和停止
         */
        initListeners();
        mDialogLoader = DialogLoader.getInstance().setIDialogStrategy(new MaterialDialogStrategy());
        //设置地图的放缩级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setOnMapLoadedListener(() -> setUp(aMap));
        //蓝点初始化
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle.showMyLocation(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //从location对象中获取经纬度信息，地址描述信息，建议拿到位置之后调用逆地理编码接口获取
            }
        });

        //高德隐私政策弹窗
        if (!SettingUtils.isAgreeMapPrivacy()) {
            privacyCompliance();
        }
        SettingUtils.setIsAgreeMapPrivacy(true);
        //设置语音
        voiceUtils = new VoiceUtils(RunningActivity.this);
        mTraceoverlay = new TraceOverlay(aMap);

    }

    public void initListeners() {
        /**
         * 对开始跑步进行监听
         */
        btn_start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "用户点击开始运动");
                if (isStop) {
                    isStop = !isStop;
                    voiceUtils.speakWords("开始跑步！动起来！");
                    startTimer();

                    /**
                     * 轨迹类实现
                     */
                    aMap.clear(true);
                    if (record != null) {
                        record = null;
                    }
                    record = new PathRecord();
                    mStartTime = System.currentTimeMillis();
                    record.setDate(getcueDate(mStartTime));
                    TextView_distance.setText("0.00");
                } else {
                    XToastUtils.info("正在跑步中....");
                }
            }
        });
        /**
         * 对暂停跑步进行监听
         */
        btn_pause.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!isStop) {
                    if (isPause) {
                        Log.i(TAG, "Resume");
                        btn_pause.setText("暂停");
                        XToastUtils.info("继续跑步....");
                        voiceUtils.speakWords("继续跑步");
                    } else {
                        Log.i(TAG, "Pause");
                        btn_pause.setText("继续");
                        XToastUtils.info("暂停跑步....");
                        voiceUtils.speakWords("暂停跑步");
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
                        updateTextView();
                        updateDistances();
                        updateSteps();
                        updateHeartRates();
                        break;
                    default:
                        break;
                }
            }
        };

        /**
         * 对结束跑步进行监听
         */
        btn_stop.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!isStop) {
                    isPause = !isPause;
                    /**
                     * 弹出对话框
                     */
                    mDialogLoader.showConfirmDialog(
                            RunningActivity.this, "结束运动提示 \n 确认结束运动吗", "确认",
                            /**
                             * 点击了确认，运动结束
                             */
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isPause = false;
                                    isStop = true;
                                    stopTimer();
                                    TextView_time.setText(TimerUtils.timeConversion(0));
                                    XToast.normal(RunningActivity.this, "运动结束").show();
                                    voiceUtils.speakWords("运动已结束");
                                    dialogInterface.dismiss();
                                    /**
                                     * 轨迹实现类
                                     */
                                    mEndTime = System.currentTimeMillis();
                                    mOverlayList.add(mTraceoverlay);
                                    LBSTraceClient mTraceClient = null;
                                    try {
                                        mTraceClient = new LBSTraceClient(getApplicationContext());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mTraceClient.queryProcessedTrace(2, RecordUtil.parseTraceLocationList(record.getPathline()), LBSTraceClient.TYPE_AMAP, RunningActivity.this);
                                    saveRecord(record.getPathline(), record.getDate());
                                }
                            }, "取消",
                            /**
                             * 点击了取消，运动继续
                             */
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isPause = !isPause;
                                    XToast.normal(RunningActivity.this, "运动继续").show();
                                    voiceUtils.speakWords("运动继续");
                                    dialogInterface.dismiss();
                                }
                            });
                } else {
                    voiceUtils.speakWords("运动未开始");
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
     * 更新时间
     */
    private void updateTextView() {
        TextView_time.setText(TimerUtils.timeConversion(count));
    }

    /**
     * 更新步数
     */
    private void updateSteps() {
        steps += (int) (Math.random() * 5);
        TextView_steps.setText(String.valueOf(steps));
    }

    /**
     * 更新距离
     */
    private void updateDistances() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        TextView_distance.setText(decimalFormat.format(getTotalDistance() / 1000d));
    }

    /**
     * 更新心率
     */
    private void updateHeartRates() {
        int heartRate = (int) (Math.random() * 40) + 60;
        TextView_heartRate.setText(String.valueOf(heartRate));
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

    public void sendMessage(int id) {
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }


    private void setUp(AMap amap) {
        UiSettings uiSettings = amap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }


    /*************************************** 定位监听******************************************************/
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        startlocation();

    }

    /**
     * 开始定位。
     */
    private void startlocation() {
        if (mlocationClient == null) {
            try {
                mlocationClient = new AMapLocationClient(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(2000);

            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();

        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    protected void saveRecord(List<AMapLocation> list, String time) {
        if (list != null && list.size() > 0) {
            DbHepler = new DbAdapter(this);
            DbHepler.open();
            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);
            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);
            String stratpoint = amapLocationToString(firstLocaiton);
            String endpoint = amapLocationToString(lastLocaiton);
            DbHepler.createrecord(String.valueOf(distance), duration, average,
                    pathlineSring, stratpoint, endpoint, time);
            DbHepler.close();
        } else {
            Toast.makeText(RunningActivity.this, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    /**
     * 实时轨迹画线
     */
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = aMap.addPolyline(mPolyoptions);
            }
        }
//		if (mpolyline != null) {
//			mpolyline.remove();
//		}
//		mPolyoptions.visible(true);
//		mpolyline = mAMap.addPolyline(mPolyoptions);
//			PolylineOptions newpoly = new PolylineOptions();
//			mpolyline = mAMap.addPolyline(newpoly.addAll(mPolyoptions.getPoints()));
//		}
    }

    /**
     * 定位回调  在回调方法中调用“mListener.onLocationChanged(amapLocation);”可以在地图上显示系统小蓝点。
     *
     * @param amapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng mylocation = new LatLng(amapLocation.getLatitude(),
                        amapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(mylocation));
                if (!isStop) {
                    record.addpoint(amapLocation);
                    mPolyoptions.add(mylocation);
                    mTracelocationlist.add(RecordUtil.parseTraceLocation(amapLocation));
                    redrawline();
                    if (mTracelocationlist.size() > tracesize - 1) {
                        trace();
                    }
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    public void record(View view) {
        Intent intent = new Intent(RunningActivity.this, RecordActivity.class);
        startActivity(intent);
    }

    private void trace() {
        List<TraceLocation> locationList = new ArrayList<>(mTracelocationlist);
        LBSTraceClient mTraceClient = null;
        try {
            mTraceClient = new LBSTraceClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTraceClient.queryProcessedTrace(1, locationList, LBSTraceClient.TYPE_AMAP, this);
        TraceLocation lastlocation = mTracelocationlist.get(mTracelocationlist.size() - 1);
        mTracelocationlist.clear();
        mTracelocationlist.add(lastlocation);
    }

    @Override
    public void onRequestFailed(int i, String s) {
        mOverlayList.add(mTraceoverlay);
        mTraceoverlay = new TraceOverlay(aMap);
    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {

    }

    /**
     * 轨迹纠偏成功回调。
     *
     * @param lineID      纠偏的线路ID
     * @param linepoints  纠偏结果
     * @param distance    总距离
     * @param waitingtime 等待时间
     */
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int waitingtime) {
        if (lineID == 1) {
            if (linepoints != null && linepoints.size() > 0) {
                mTraceoverlay.add(linepoints);
                mDistance += distance;
                mTraceoverlay.setDistance(mTraceoverlay.getDistance() + distance);
                if (mlocMarker == null) {
                    mlocMarker = aMap.addMarker(new MarkerOptions().position(linepoints.get(linepoints.size() - 1))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.point))
                            .title("距离：" + mDistance + "米"));
                    mlocMarker.showInfoWindow();
                } else {
                    mlocMarker.setTitle("距离：" + mDistance + "米");
                    Toast.makeText(RunningActivity.this, "距离" + mDistance, Toast.LENGTH_SHORT).show();
                    mlocMarker.setPosition(linepoints.get(linepoints.size() - 1));
                    mlocMarker.showInfoWindow();
                }
            }
        } else if (lineID == 2) {
            if (linepoints != null && linepoints.size() > 0) {
                aMap.addPolyline(new PolylineOptions()
                        .color(Color.RED)
                        .width(40).addAll(linepoints));
            }
        }

    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.GRAY);
        tracePolytion = new PolylineOptions();
        tracePolytion.width(40);
        tracePolytion.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.grasp_trace_line));
    }

    /**
     * 最后获取总距离
     *
     * @return
     */
    private int getTotalDistance() {
        int distance = 0;
        for (TraceOverlay to : mOverlayList) {
            distance = distance + to.getDistance();
        }
        return distance;
    }

    /*************************************** 权限检查******************************************************/
    /**
     * 弹出隐私政策确认对话框
     */
    private void privacyCompliance() {
        MapsInitializer.updatePrivacyShow(RunningActivity.this, true, true);
        mDialogLoader.showConfirmDialog(
                RunningActivity.this, "高德地图温馨提示(隐私合规示例)",
                "\"亲，感谢您对GoSports一直以来的信任！我们依据最新的监管要求更新了GoSports《隐私权政策》，特向您说明如下\n1.为向您提供交易相关基本功能，我们会收集、使用必要的信息；\n2.基于您的明示授权，我们可能会获取您的位置（为您提供附近的商品、店铺及优惠资讯等）等信息，您有权拒绝或取消授权；\n3.我们会采取业界先进的安全措施保护您的信息安全；\n4.未经您同意，我们不会从第三方处获取、共享或向提供您的信息；\n",
                "同意",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsInitializer.updatePrivacyAgree(RunningActivity.this, true);
                        dialogInterface.dismiss();
                    }
                },
                "不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsInitializer.updatePrivacyAgree(RunningActivity.this, false);
                        dialogInterface.dismiss();
                    }
                });
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            BACK_LOCATION_PERMISSION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    /**
     * 重新绘制加载地图
     */
    @Override
    protected void onResume() {
        try {
            super.onResume();
            //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
            mMapView.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 在activity执行onPause时执行mapView.onPause ()，暂停地图的绘制
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if (!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });
            builder.setPositiveButton("设置",
                    (dialog, which) -> {
                        try {
                            startAppSettings();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    });

            builder.setCancelable(false);
            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
