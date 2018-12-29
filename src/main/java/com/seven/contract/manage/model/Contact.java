package com.seven.contract.manage.model;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Contact implements Serializable {

	/**
	 *	id
	 */
	private long id;

	/**
	 *	用户MID
	 */
	private long mid;

	/**
	 *	联系人MID
	 */
	private long contactMid;

	/**
	 *	备注
	 */
	private String remark;

	/**
	 *	添加时间
	 */
	private java.util.Date addTime;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMid() {
		return mid;
	}

	public void setMid(long mid) {
		this.mid = mid;
	}

	public long getContactMid() {
		return contactMid;
	}

	public void setContactMid(long contactMid) {
		this.contactMid = contactMid;
	}

	public void setRemark(String remark){
		this.remark = remark;
	}

	public String getRemark(){
		return this.remark;
	}

	public void setAddTime(java.util.Date addTime){
		this.addTime = addTime;
	}

	public java.util.Date getAddTime(){
		return this.addTime;
	}

}
