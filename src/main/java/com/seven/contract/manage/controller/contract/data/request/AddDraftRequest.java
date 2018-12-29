package com.seven.contract.manage.controller.contract.data.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by apple on 2018/12/22.
 */
public class AddDraftRequest implements Serializable {

    /**
     *	合同名
     */
    private String contractName;

    /**
     *	结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date endTime;

    /**
     *	备注
     */
    private String remark;

    /**
     *	是否绝密合同 Y:是 N:否
     */
    private String secretContract = "N";

    /**
     *	合同地址
     */
    private String contractUrl;

    /**
     * 联系人MID,多人用逗号分割
     */
    private String contactMids;

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    public Date getEndTime() {
        return endTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSecretContract() {
        return secretContract;
    }

    public void setSecretContract(String secretContract) {
        this.secretContract = secretContract;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getContactMids() {
        return contactMids;
    }

    public void setContactMids(String contactMids) {
        this.contactMids = contactMids;
    }

}
