package com.seven.contract.manage.controller.member.data.request;

import java.io.Serializable;

/**
 * Created by apple on 2018/12/16.
 */
public class RegisteredRequest implements Serializable {

    /**
     * 类型 personal：个人 company：企业
     */
    private String type;

    /**
     * 用户名/企业名称
     */
    private String name;

    /**
     * 手机号/管理员手机号
     */
    private String phone;

    /**
     * 身份证号/法人身份证号
     */
    private String idCard;

    /**
     * 注册验证码
     */
    private String verifyCode;

    /**
     * 用户照片/营业执照扫描件
     */
    private String memberImg;

    /**
     * 身份证正面照片
     */
    private String idCardFrontImg;

    /**
     * 身份证返面照片
     */
    private String idCardBackImg;

    /**
     * 密码
     */
    private String privateKeyPwd;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

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

    public String getPrivateKeyPwd() {
        return privateKeyPwd;
    }

    public void setPrivateKeyPwd(String privateKeyPwd) {
        this.privateKeyPwd = privateKeyPwd;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }
}
