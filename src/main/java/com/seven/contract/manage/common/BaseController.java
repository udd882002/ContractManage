package com.seven.contract.manage.common;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2018/12/12.
 */
public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String MEMBER_SESSION_KEY = "member_session_key";

    protected Member checkLogin(HttpServletRequest request) throws AppRuntimeException {
        Object object = request.getSession().getAttribute(MEMBER_SESSION_KEY);
        if (object == null) {
            logger.error("用户未登陆");
            throw new AppRuntimeException("0001", "用户未登陆,请登陆");
        }

        return (Member) object;
    }

    protected void printHttpHeader(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        logger.debug("request header = {}", JSON.toJSONString(map));
    }
}
