package com.xiaofeng.GoSports.utils.sms;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.PullSmsSendStatusRequest;
import com.tencentcloudapi.sms.v20190711.models.PullSmsSendStatusResponse;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.xiaofeng.GoSports.utils.SharePreferenceUtil.SPUtils;

/**
 * @author xiaofeng
 * @description
 * @date 2022/6/1.
 */
public class SendSmsUtil {
    private final String PREFIX = "SMS";
    private final String SECRET_ID = "AKIDFKAk35rNdcLmxrYz92g38uPedHfogHac";
    private final String SECRET_Key = "nJYVDenfHcgU1II1SdX9UaPxEExPaU8V";
    private final String SDKAPPID = "1400686390";
    // 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
    Credential cred;
    // 实例化一个http选项，可选，没有特殊需求可以跳过
    HttpProfile httpProfile;
    /* 实例化一个客户端配置对象，可以指定超时时间等配置 */
    ClientProfile clientProfile;
    /* 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8 */
    SmsClient client;
    /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数   */
    SendSmsRequest req;


    public SendSmsUtil() {
        init();
    }

    private void init() {
        //实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
        cred = new Credential(SECRET_ID, SECRET_Key);
        httpProfile = new HttpProfile();
        // 设置代理（无需要直接忽略）
        // httpProfile.setProxyHost("真实代理ip");
        // httpProfile.setProxyPort(真实代理端口);
        /* SDK默认使用POST方法。
         * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
        httpProfile.setReqMethod("POST");
        /* SDK有默认的超时时间，非必要请不要进行调整
         * 如有需要请在代码中查阅以获取最新的默认值 */
        httpProfile.setConnTimeout(60);
        /* 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象*/
        client = new SmsClient(cred, "ap-guangzhou", clientProfile);
        /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
         * 你可以直接查询SDK源码确定接口有哪些属性可以设置
         * 属性可能是基本类型，也可能引用了另一个数据结构
         * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
        req = new SendSmsRequest();

        req.setSmsSdkAppid(SDKAPPID);

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
        String signName = "村事宝个人小程序";
        req.setSign(signName);

        /* 模板 ID: 必须填写已审核通过的模板 ID */
        String templateId = "1424638";
        req.setTemplateID(templateId);
    }

    public void sendSms(Context context, String phone) {
        // 缓存的键
        final String smsKey = SmsCodeUtil.createSmsCacheKey(PREFIX, phone, "GoSports_sign");
        //下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]  示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号
        String[] phoneNumbers = {"+86" + phone};
        req.setPhoneNumberSet(phoneNumbers);
        //模板参数: 若无模板参数，则设置为空（第一个参数为随机验证码，第二个参数为有效时间）
        final String smsRandomCode = SmsCodeUtil.createSmsRandomCode(6);
        //短信模板所需的参数
        String[] templateParams = {smsRandomCode, "5"};
        req.setTemplateParamSet(templateParams);
        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse res = null;
        try {
            res = client.SendSms(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }

        // 输出json格式的字符串回包
        Logger.json(SendSmsResponse.toJsonString(res));

        //进行存储
        SPUtils.put(context, smsKey, smsRandomCode);

    }

    public Boolean verifySmsCode(Context context, String phone, String code) {
        final String smsKey = SmsCodeUtil.createSmsCacheKey(PREFIX, phone, "GoSports_sign");
        String localCode = (String) SPUtils.get(context, smsKey, "");
        if (localCode != null && localCode.equals(code)) {
            return true;
        }
        return false;
    }

    public void pullSmsSendStatus() {
        try {
            /* 必要步骤：
             * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
             * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
             * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
             * 以免泄露密钥对危及你的财产安全。
             * SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi */
            Credential cred = new Credential(SECRET_ID, SECRET_Key);

            // 实例化一个http选项，可选，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            // 设置代理
            // httpProfile.setProxyHost("真实代理ip");
            // httpProfile.setProxyPort(真实代理端口);
            /* SDK默认使用POST方法。
             * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
            httpProfile.setReqMethod("POST");
            /* SDK有默认的超时时间，非必要请不要进行调整
             * 如有需要请在代码中查阅以获取最新的默认值 */
            httpProfile.setConnTimeout(60);
            /* 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com */
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            /* 非必要步骤:
             * 实例化一个客户端配置对象，可以指定超时时间等配置 */
            ClientProfile clientProfile = new ClientProfile();
            /* SDK默认用TC3-HMAC-SHA256进行签名
             * 非必要请不要修改这个字段 */
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);

            /* 实例化要请求产品(以sms为例)的client对象
             * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8 */
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);

            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 你可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
            PullSmsSendStatusRequest req = new PullSmsSendStatusRequest();

            /* 填充请求参数,这里request对象的成员变量即对应接口的入参
             * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
             * 基本类型的设置:
             * 帮助链接：
             * 短信控制台: https://console.cloud.tencent.com/smsv2
             * 腾讯云短信小助手: https://cloud.tencent.com/document/product/382/3773#.E6.8A.80.E6.9C.AF.E4.BA.A4.E6.B5.81 */

            /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
            req.setSmsSdkAppid(SDKAPPID);

            // 设置拉取最大条数，最多100条
            Long limit = 5L;
            req.setLimit(limit);

            /* 通过 client 对象调用 PullSmsSendStatus 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 PullSmsSendStatusResponse 类的实例，与请求对象对应 */
            PullSmsSendStatusResponse res = client.PullSmsSendStatus(req);

            // 输出json格式的字符串回包
            Logger.json(PullSmsSendStatusResponse.toJsonString(res));

        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
    }
}

