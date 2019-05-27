package com.seven.contract.manage.utils;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.conf.SmsProperties;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 返回参数 描述
 >0 成功,系统生成的任务id或自定义的任务id
 0 失败
 -1 用户名或者密码不正确
 -2 必填选项为空
 -3 短信内容0个字节
 -4 0个有效号码
 -5 余额不够
 -10 用户被禁用
 -11 短信内容超过500字
 -12 无扩展权限（ext字段需填空）
 -13 IP校验错误
 -14 内容解析异常
 -990 未知错误
 **/
public class SmsSendUtil {

    private static Logger logger = LoggerFactory.getLogger(SmsSendUtil.class);

    public static String fillMD5(String md5) {
        return md5.length() == 32 ? md5 : fillMD5("0" + md5);
    }

    public static boolean sendSms(String mobile, String msg) {
        try {
            String userName = SmsProperties.account;
            String passwd = SmsProperties.pswd;
            String url = SmsProperties.url;

            MessageDigest md5_pass = MessageDigest.getInstance("MD5");
            md5_pass.update((passwd).getBytes("UTF-8"));
            String result = fillMD5(new BigInteger(1, md5_pass.digest()).toString(16));

            String tmp = userName + result;
            md5_pass.reset();
            md5_pass.update((tmp).getBytes("UTF-8"));
            result = fillMD5(new BigInteger(1, md5_pass.digest()).toString(16));

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", userName));
            nvps.add(new BasicNameValuePair("password", result));
            nvps.add(new BasicNameValuePair("mobile", mobile));
            nvps.add(new BasicNameValuePair("content", msg));

            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            CloseableHttpResponse response = null;
            try {
                response = HttpClients.createDefault().execute(httppost);
                logger.debug("response = {}", JSON.toJSONString(response));
            } catch (IOException e) {
                logger.error("短信下发失败", e);
                return false;
            }
            HttpEntity entity = response.getEntity();
            logger.debug("entity = {}", JSON.toJSONString(entity));

            String code = null;
            try {
                code = EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.debug("code = {}", code);

            if (!NumberUtil.isNumeric(code) || Long.valueOf(code) < 1) {
                logger.error("短信下发失败, code = {}", code);
                return false;
            }

        } catch(Exception e) {
            logger.error("发送 POST 请求出现异常！", e);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

        SmsSendUtil.sendSms("15834198706", "您好，您的验证码是8888");


    }

}
