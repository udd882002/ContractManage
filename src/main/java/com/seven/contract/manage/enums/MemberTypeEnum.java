package com.seven.contract.manage.enums;

/**
 * Created by apple on 2019/1/19.
 */
public enum MemberTypeEnum {

    personal("个人"), company("企业");

    private String desc;

    // 构造方法
    MemberTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
