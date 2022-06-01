

package com.xiaofeng.GoSports.fragment.other;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xiaofeng.GoSports.LoginActivity;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.activity.LoginActivityByPhone;
import com.xiaofeng.GoSports.activity.MainActivity;
import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.databinding.FragmentPhoneLoginBinding;
import com.xiaofeng.GoSports.utils.RandomUtils;
import com.xiaofeng.GoSports.utils.SettingUtils;
import com.xiaofeng.GoSports.utils.TokenUtils;
import com.xiaofeng.GoSports.utils.Utils;
import com.xiaofeng.GoSports.utils.XToastUtils;
import com.xiaofeng.GoSports.utils.sdkinit.UMengInit;
import com.xiaofeng.GoSports.utils.sms.SendSmsUtil;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;

/**
 * 登录页面
 *
 * @author xiaofeng
 * @since 2022-5-17 22:15
 */
@Page(anim = CoreAnim.none)
public class LoginFragment extends BaseFragment<FragmentPhoneLoginBinding> implements View.OnClickListener {
    /**
     * SMS发送短信Util类
     */
    private SendSmsUtil smsUtil;
    private View mJumpView;
    /**
     * 倒计时帮助类->验证码60s倒计时
     */
    private CountDownButtonHelper mCountDownHelper;

    @NonNull
    @Override
    protected FragmentPhoneLoginBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentPhoneLoginBinding.inflate(inflater, container, false);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        mJumpView = titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
            @Override
            public void performAction(View view) {
                onLoginSuccess();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        //初始化短信smsUtil
        smsUtil = new SendSmsUtil();

        mCountDownHelper = new CountDownButtonHelper(binding.btnGetVerifyCode, 60);
        //隐私政策弹窗
        if (!SettingUtils.isAgreePrivacy()) {
            Utils.showPrivacyDialog(getContext(), (dialog, which) -> {
                dialog.dismiss();
                handleSubmitPrivacy();
            });
        }
        boolean isAgreePrivacy = SettingUtils.isAgreePrivacy();
        binding.cbProtocol.setChecked(isAgreePrivacy);
        refreshButton(isAgreePrivacy);
        binding.cbProtocol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingUtils.setIsAgreePrivacy(isChecked);
            refreshButton(isChecked);
        });
    }

    @Override
    protected void initListeners() {
        binding.btnGetVerifyCode.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.tvOtherLogin.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);
        binding.tvUserProtocol.setOnClickListener(this);
        binding.tvPrivacyProtocol.setOnClickListener(this);
    }

    private void refreshButton(boolean isChecked) {
        ViewUtils.setEnabled(binding.btnLogin, isChecked);
        ViewUtils.setEnabled(mJumpView, isChecked);
    }

    private void handleSubmitPrivacy() {
        SettingUtils.setIsAgreePrivacy(true);
        UMengInit.init();
        // 应用市场不让默认勾选
//        ViewUtils.setChecked(cbProtocol, true);
    }

    @SingleClick
    @Override
    /**
     * 登陆界面动作事件响应
     */
    public void onClick(View v) {
        int id = v.getId();
        //如果点击的事件来源是获取验证码
        if (id == R.id.btn_get_verify_code) {
            //首先校验输入的手机号是否正确
            if (binding.etPhoneNumber.validate()) {
                getVerifyCode(binding.etPhoneNumber.getEditValue());
            }
        } else if (id == R.id.btn_login) {
            if (binding.etPhoneNumber.validate()) {
                if (binding.etVerifyCode.validate()) {
                    loginByVerifyCode(binding.etPhoneNumber.getEditValue(), binding.etVerifyCode.getEditValue());
                }
            }
        } else if (id == R.id.tv_other_login) {
            XToastUtils.info("其他登录方式");
            ActivityUtils.startActivity(LoginActivity.class);
        } else if (id == R.id.tv_forget_password) {
            XToastUtils.info("忘记密码");
        } else if (id == R.id.tv_user_protocol) {
            Utils.gotoProtocol(this, false, true);
        } else if (id == R.id.tv_privacy_protocol) {
            Utils.gotoProtocol(this, true, true);
        }

    }

    /**
     * 获取验证码
     */
    private void getVerifyCode(String phoneNumber) {
        /**
         * Runnable做HTTP请求
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                smsUtil.sendSms(getContext(), phoneNumber);
                smsUtil.pullSmsSendStatus();
            }
        }).start();

//        XToastUtils.warning("只是演示，验证码请随便输");
        mCountDownHelper.start();
    }

    /**
     * 根据验证码登录
     *
     * @param phoneNumber 手机号
     * @param verifyCode  验证码
     */
    private void loginByVerifyCode(String phoneNumber, String verifyCode) {
        // TODO: 2020/8/29 这里只是界面演示而已
        if (smsUtil.verifySmsCode(getContext(), phoneNumber, verifyCode)) {
            onLoginSuccess();
        } else {
            XToastUtils.info("登陆失败，请确认您的验证码");
        }

    }

    /**
     * 登录成功的处理
     */
    private void onLoginSuccess() {
        String token = RandomUtils.getRandomNumbersAndLetters(16);
        if (TokenUtils.handleLoginSuccess(token)) {
            popToBack();
            ActivityUtils.startActivity(MainActivity.class);
        }
    }

    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }


}

