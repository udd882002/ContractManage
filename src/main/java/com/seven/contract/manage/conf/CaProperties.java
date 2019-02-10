package com.seven.contract.manage.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by apple on 2019/1/30.
 */
@Configuration
@ConfigurationProperties(prefix = "ca")
@PropertySource("classpath:ca.properties")
public class CaProperties {

    public static String url;
    public static String pwd;
    public static String personalCertificate;
    public static String personalCharge;
    public static String businessCertificate;
    public static String businessCharge;

    public String getPersonalCharge() {
        return personalCharge;
    }

    public void setPersonalCharge(String personalCharge) {
        this.personalCharge = personalCharge;
    }

    public String getBusinessCertificate() {
        return businessCertificate;
    }

    public void setBusinessCertificate(String businessCertificate) {
        this.businessCertificate = businessCertificate;
    }

    public String getBusinessCharge() {
        return businessCharge;
    }

    public void setBusinessCharge(String businessCharge) {
        CaProperties.businessCharge = businessCharge;
    }

    public String getPersonalCertificate() {

        return personalCertificate;
    }

    public void setPersonalCertificate(String personalCertificate) {
        this.personalCertificate = personalCertificate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
