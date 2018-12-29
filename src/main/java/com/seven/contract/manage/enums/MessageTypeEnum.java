package com.seven.contract.manage.enums;

/**
 * Created by apple on 2018/12/20.
 */
public enum MessageTypeEnum {
    contractComplete("合同已完成"), contractRefuse("合同被拒签");

    private String desc;

    // 构造方法
    MessageTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
