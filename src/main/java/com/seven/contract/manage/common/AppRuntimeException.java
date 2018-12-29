package com.seven.contract.manage.common;

/**
 * Created by apple on 2018/12/12.
 */
public class AppRuntimeException extends Exception {

    private String reqCode ;  //异常对应的返回码
    private String msg;  //异常对应的描述信息

    public AppRuntimeException() {
        super();
    }

    public AppRuntimeException(String message) {
        super(message);
        msg = message;
    }

    public AppRuntimeException(String reqCode, String message) {
        super();
        this.reqCode = reqCode;
        this.msg = message;
    }

    public String getReqCode() {
        return reqCode;
    }

    public String getMsg() {
        return msg;
    }
}
