package com.xiaofeng.GoSports;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anrwatchdog.ANRWatchDog;
import com.xiaofeng.GoSports.Dao.DBOpenHelper;
import com.xiaofeng.GoSports.Model.User;
import com.xiaofeng.GoSports.activity.MainActivity;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xiaofeng.GoSports.utils.sdkinit.ANRWatchDogInit;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.common.logger.Logger;

/**
 * 账号密码的登陆界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private DBOpenHelper mDBOpenHelper;
    private EditText et_User, et_Psw;
    private CheckBox cb_rmbPsw;
    private String userName;
    //SharedPreferences保存的数据主要是类似配置信息格式的数据 此处用于保存密码
    private SharedPreferences.Editor editor;
    private String username, password;

    /**
     * 创建 Activity 时先来重写 onCreate() 方法
     * 保存实例状态
     * super.onCreate(savedInstanceState);
     * 设置视图内容的配置文件
     * setContentView(R.layout.activity_login);
     * 上面这行代码真正实现了把视图层 View 也就是 layout 的内容放到 Activity 中进行显示
     * 初始化视图中的控件对象 initView()
     * 实例化 DBOpenHelper，待会进行登录验证的时候要用来进行数据查询
     * mDBOpenHelper = new DBOpenHelper(this);
     */

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//禁止横屏
        setContentView(R.layout.activity_login);

        initView();//初始化界面
        mDBOpenHelper = new DBOpenHelper(this);
        /**
         * 启动时查看用户是否已经有了在SharedPreferences缓存
         * 如果有缓存那么，自动填充
         */
        SharedPreferences sp = getSharedPreferences("user_mes", MODE_PRIVATE);
        editor = sp.edit();
        if (sp.getBoolean("flag", false)) {
            String user_read = sp.getString("user", "");
            String psw_read = sp.getString("psw", "");
            et_User.setText(user_read);
            et_Psw.setText(psw_read);
        }
        //调用封装的方法，防止重复快速点击
        ANRWatchDogInit.init();
    }

    private void initView() {
        //初始化控件
        et_User = findViewById(R.id.et_User);
        et_Psw = findViewById(R.id.et_Psw);
        cb_rmbPsw = findViewById(R.id.cb_rmbPsw);
        Button btn_Login = findViewById(R.id.btn_Login);
        TextView tv_register = findViewById(R.id.tv_Register);
        //设置点击事件监听器
        btn_Login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        //赋值

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Register: //注册
                ActivityUtils.startActivity(RegisteredActivity.class);
                finish();
                break;
            case R.id.btn_Login:
                Logger.e("点击登录");
                username = et_User.getText().toString().trim();
                password = et_Psw.getText().toString().trim();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    User user = new User(userName, password);
                    if (mDBOpenHelper.findUserIfExisted(username, password)) {
                        Logger.e("数据库匹配成功");
                        if (cb_rmbPsw.isChecked()) {
                            //用户勾选了记住密码：将密码和用户名和flag等存入SharedPreferences中
                            editor.putBoolean("flag", true);
                            editor.putString("psw", user.getPassword());
                        } else {
                            //用户未勾选记住密码：只将用户名存入SharedPreferences中
                            editor.putString("psw", "");
                        }
                        editor.putString("user", user.getName());
                        editor.apply();

                        Logger.e("登陆成功");
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        ActivityUtils.startActivity(MainActivity.class);//设置自己跳转到成功的界面
                    } else {
                        Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    XToastUtils.info("请输入你的用户名或密码");
                }
                break;
        }
    }
}

