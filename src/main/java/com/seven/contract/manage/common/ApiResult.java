package com.seven.contract.manage.common;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by apple on 2018/12/11.
 */
public class ApiResult<T> implements Serializable {

    private String result = "success";

    private String msg = "请求成功";

    private String reqCode = "0000";

    private T data;

    private String sessionId = "";

    public ApiResult(HttpServletRequest request, String result, String reqCode, String msg) {
        if (request != null) {
            this.sessionId = request.getSession().getId();
        }
        this.result = result;
        this.reqCode = reqCode;
        this.msg = msg;
    }

    public ApiResult(HttpServletRequest request) {
        if (request != null) {
            this.sessionId = request.getSession().getId();
        }
    }

    public ApiResult(HttpServletRequest request, T data) {
        if (request != null) {
            this.sessionId = request.getSession().getId();
        }
        this.data = data;
    }

    public ApiResult() {

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static ApiResult success(HttpServletRequest request) {
        return new ApiResult(request);
    }

    public static ApiResult success(HttpServletRequest request, Object data) {
        return new ApiResult(request, data);
    }

    public static ApiResult fail(HttpServletRequest request, String reqCode, String msg) {
        return new ApiResult(request, "fail", reqCode, msg);
    }

    public static ApiResult fail(HttpServletRequest request) {
        return fail(request, "9999", "请求失败");
    }

    public static ApiResult fail(HttpServletRequest request, String msg) {
        return fail(request, "9999", msg);
    }
}
