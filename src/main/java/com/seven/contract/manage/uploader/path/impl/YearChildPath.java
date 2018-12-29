package com.seven.contract.manage.uploader.path.impl;

import com.seven.contract.manage.uploader.path.ChildPath;
import com.seven.contract.manage.uploader.util.TimeUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "xin.uploader.child-path-strategy", havingValue = "year"
)
public class YearChildPath implements ChildPath {

    @Override
    public String getChildPath() {
        return "/" + TimeUtil.year();
    }
}
