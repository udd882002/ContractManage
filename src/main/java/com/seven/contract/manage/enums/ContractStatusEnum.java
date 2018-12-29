package com.seven.contract.manage.enums;

/**
 * Created by apple on 2018/12/20.
 */
public enum ContractStatusEnum {

    draft("草稿"), signing("签约中"), fail("被拒签"), registered("上链中"), complete("完成");

    private String desc;

    // 构造方法
    ContractStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
