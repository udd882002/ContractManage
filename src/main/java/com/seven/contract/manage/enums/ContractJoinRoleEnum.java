package com.seven.contract.manage.enums;

/**
 * Created by apple on 2018/12/19.
 */
public enum ContractJoinRoleEnum {
    initiator("发起者"), join("参与者");

    private String desc;

    // 构造方法
    ContractJoinRoleEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
