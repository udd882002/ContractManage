package com.seven.contract.manage.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by apple on 2019/1/22.
 */
@Configuration
@ConfigurationProperties(prefix = "sms")
@PropertySource("classpath:sms.properties")
public class SmsProperties {

    public static String url;

    public static String account;

    public static String pswd;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        SmsProperties.url = url;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        SmsProperties.pswd = pswd;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        SmsProperties.account = account;
    }
}
