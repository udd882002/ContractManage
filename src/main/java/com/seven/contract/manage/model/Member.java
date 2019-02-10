package com.seven.contract.manage.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Member implements Serializable {

	/**
	 *	id
	 */
	private int id;

	/**
	 *	用户名
	 */
	private String name;

	/**
	 *	公钥
	 */
	private String publicKeys;

	/**
	 *	手机号
	 */
	private String phone;

	/**
	 *	身份证号
	 */
	private String idCard;

	/**
	 *	用户照片
	 */
	private String memberImg;

	/**
	 *	身份证正面照片
	 */
	private String idCardFrontImg;

	/**
	 *	身份证返面照片
	 */
	private String idCardBackImg;

	/**
	 *	私钥文件地址
	 */
	private String privateKeysFileUrl;

	/**
	 *	公司
	 */
	private String company;

	/**
	 *	职位
	 */
	private String position;

	/**
	 * 统一社会信用代码
	 */
	private String creditCode;

	/**
	 *	注册时间
	 */
	private java.util.Date addTime;

	/**
	 * 类型 personal：个人 company：企业
	 */
	private String type;

	/**
	 * CA认证信息
	 */
	private String caCert;


	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public String getPublicKeys() {
		return publicKeys;
	}

	public void setPublicKeys(String publicKeys) {
		this.publicKeys = publicKeys;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return this.phone;
	}

	public void setIdCard(String idCard){
		this.idCard = idCard;
	}

	public String getIdCard(){
		return this.idCard;
	}

	public void setMemberImg(String memberImg){
		this.memberImg = memberImg;
	}

	public String getMemberImg(){
		return this.memberImg;
	}

	public void setIdCardFrontImg(String idCardFrontImg){
		this.idCardFrontImg = idCardFrontImg;
	}

	public String getIdCardFrontImg(){
		return this.idCardFrontImg;
	}

	public void setIdCardBackImg(String idCardBackImg){
		this.idCardBackImg = idCardBackImg;
	}

	public String getIdCardBackImg(){
		return this.idCardBackImg;
	}

	public void setPrivateKeysFileUrl(String privateKeysFileUrl){
		this.privateKeysFileUrl = privateKeysFileUrl;
	}

	public String getPrivateKeysFileUrl(){
		return this.privateKeysFileUrl;
	}

	public void setCompany(String company){
		this.company = company;
	}

	public String getCompany(){
		return this.company;
	}

	public void setPosition(String position){
		this.position = position;
	}

	public String getPosition(){
		return this.position;
	}

	public void setAddTime(java.util.Date addTime){
		this.addTime = addTime;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public java.util.Date getAddTime(){
		return this.addTime;
	}

	public String getCreditCode() {
		return creditCode;
	}

	public void setCreditCode(String creditCode) {
		this.creditCode = creditCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCaCert() {
		return caCert;
	}

	public void setCaCert(String caCert) {
		this.caCert = caCert;
	}
}
