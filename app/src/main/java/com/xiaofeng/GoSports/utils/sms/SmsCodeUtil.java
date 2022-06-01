package com.xiaofeng.GoSports.utils.sms;

import cn.hutool.core.util.RandomUtil;

/**
 * @author xiaofeng
 * @description
 * @date 2022/6/1.
 */
public class SmsCodeUtil {
    /**
     * 随机生成指定长度的短信的验证码
     *
     * @param length 短信验证码长度枚举
     * @return 随机验证码
     * @author RenShiWei
     * Date: 2020/9/16 10:53
     */
    public static String createSmsRandomCode(int length) {
        return RandomUtil.randomNumbers(length);
    }

    /**
     * 创建短信验证码，缓存键策略
     * 策略：前缀_业务名_手机号
     *
     * @param prefix      前缀
     * @param phone       手机号
     * @param businessStr 业务名
     * @return 短信验证码，缓存键策略
     * @author RenShiWei
     * Date: 2020/9/16 10:53
     */
    public static String createSmsCacheKey(String prefix, String phone, String businessStr) {
        return prefix + "_" + businessStr + "_" + phone;
    }
}


