package com.seven.contract.manage.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "bytom")
@PropertySource("classpath:bytom.properties")
public class BytomProperties {

    public static String electronicAccont;

    public static String electronicContract;

    public static String password;

    public static String controlProgram;

    public static String defalutCoreUrl;

    public static String accessToken;

    public String getElectronicAccont() {
        return electronicAccont;
    }

    public void setElectronicAccont(String electronicAccont) {
        this.electronicAccont = electronicAccont;
    }

    public String getElectronicContract() {
        return electronicContract;
    }

    public void setElectronicContract(String electronicContract) {
        this.electronicContract = electronicContract;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getControlProgram() {
        return controlProgram;
    }

    public void setControlProgram(String controlProgram) {
        this.controlProgram = controlProgram;
    }

    public String getDefalutCoreUrl() {
        return defalutCoreUrl;
    }

    public void setDefalutCoreUrl(String defalutCoreUrl) {
        this.defalutCoreUrl = defalutCoreUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}