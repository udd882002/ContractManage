package com.seven.contract.manage.vo;

/**
 * Created by apple on 2019/1/23.
 */
public class ContactVo {

    private int id;
    private String name;
    private String publicKeys;
    private String phone;
    private String company;
    private String position;
    private String remark;
    private long contactMid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(String publicKeys) {
        this.publicKeys = publicKeys;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getContactMid() {
        return contactMid;
    }

    public void setContactMid(long contactMid) {
        this.contactMid = contactMid;
    }
}
