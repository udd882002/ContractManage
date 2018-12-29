package com.seven.contract.manage.enums;

/**
 * Created by apple on 2018/12/12.
 */
public enum ContractJoinStatusEnum {
    draft("草稿"),
    waitmine("待我签"),
    waitother("待其他人签"),
    refuse("拒签"),
    refuseother("被拒签"),
    complete("完成");

    private String desc;

    // 构造方法
    ContractJoinStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
