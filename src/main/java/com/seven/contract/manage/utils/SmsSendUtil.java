package com.seven.contract.manage.utils;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.conf.SmsProperties;
import com.seven.contract.manage.utils.http.HttpClientResult;
import com.seven.contract.manage.utils.http.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2019/1/22.
 */
public class SmsSendUtil {

    private static Logger logger = LoggerFactory.getLogger(SmsSendUtil.class);

    public static boolean sendSms(String mobile, String msg) {

        Map<String, String> params = new HashMap<>();
        params.put("account", SmsProperties.account);
        params.put("pswd", SmsProperties.pswd);
        params.put("mobile", mobile);
        params.put("msg", msg);
        params.put("needstatus", String.valueOf(true));
        params.put("product", "");

        String url = "http://" + SmsProperties.domain + "/msg/HttpBatchSendSM";
        logger.debug("--------->url:{}", url);
        try {
            HttpClientResult result = HttpClientUtils.doGet(url, params);

            logger.debug("--------->result:{}", JSON.toJSONString(result));
            if (result == null || result.getCode() != 200) {
                logger.error("短信发送接口请求失败");
                return false;
            }

            String content = result.getContent();
            logger.debug("-------->content:{}", content);
            String status = content.split("\n")[0].split(",")[1];
            logger.debug("-------->status:{}", status);
            if (!status.equals("0")) {
                logger.error("短信下发失败");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

//        SmsSendUtil.sendSms("15834198706", "您好，您的验证码是8888");

        String content = "20190123151306,0\n6900123151306503900\n";

        String[] c =  content.split("\n");

        for (int i = 0; i < c.length; i++) {
            System.out.println("-------->" + c[i]);
            System.out.println("-------->" + i);
        }


    }

}
