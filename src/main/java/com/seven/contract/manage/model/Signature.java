package com.seven.contract.manage.model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Signature implements Serializable {

	/**
	 *	id
	 */
	private long id;

	/**
	 *	用户MID
	 */
	private long mid;

	/**
	 *	签章地址
	 */
	private String url;

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

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return this.url;
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

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public java.util.Date getAddTime(){
		return this.addTime;
	}

}
