package com.seven.contract.manage.controller.member.data.request;

import java.io.Serializable;

/**
 * Created by apple on 2018/12/16.
 */
public class RegisteredRequest implements Serializable {

    private String name;

    private String phone;

    private String idCard;

    private String verifyCode;

    private String memberImg;

    private String idCardFrontImg;

    private String idCardBackImg;

    private String privateKeysFileUrl;

    private String publicKeys;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getMemberImg() {
        return memberImg;
    }

    public void setMemberImg(String memberImg) {
        this.memberImg = memberImg;
    }

    public String getIdCardFrontImg() {
        return idCardFrontImg;
    }

    public void setIdCardFrontImg(String idCardFrontImg) {
        this.idCardFrontImg = idCardFrontImg;
    }

    public String getIdCardBackImg() {
        return idCardBackImg;
    }

    public void setIdCardBackImg(String idCardBackImg) {
        this.idCardBackImg = idCardBackImg;
    }

    public String getPrivateKeysFileUrl() {
        return privateKeysFileUrl;
    }

    public void setPrivateKeysFileUrl(String privateKeysFileUrl) {
        this.privateKeysFileUrl = privateKeysFileUrl;
    }

    public String getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(String publicKeys) {
        this.publicKeys = publicKeys;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
