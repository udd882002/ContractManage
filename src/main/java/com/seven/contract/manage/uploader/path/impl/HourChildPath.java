package com.seven.contract.manage.uploader.path.impl;

import com.seven.contract.manage.uploader.path.ChildPath;
import com.seven.contract.manage.uploader.util.TimeUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "xin.uploader.child-path-strategy", havingValue = "hour"
)
public class HourChildPath implements ChildPath {
    @Override
    public String getChildPath() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(TimeUtil.year())
                .append("/").append(TimeUtil.month())
                .append("/").append(TimeUtil.day())
                .append("/").append(TimeUtil.hour());
        return sb.toString();
    }
}
