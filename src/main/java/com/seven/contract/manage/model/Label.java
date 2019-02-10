package com.seven.contract.manage.model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Label implements Serializable {

	/**
	 *	id
	 */
	private int id;

	/**
	 *	用户MID
	 */
	private long mid;

	/**
	 *	标签名称
	 */
	private String labelName;

	/**
	 *	添加时间
	 */
	private java.util.Date addTime;



	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
	}

	public long getMid() {
		return mid;
	}

	public void setMid(long mid) {
		this.mid = mid;
	}

	public void setLabelName(String labelName){
		this.labelName = labelName;
	}

	public String getLabelName(){
		return this.labelName;
	}

	public void setAddTime(java.util.Date addTime){
		this.addTime = addTime;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public java.util.Date getAddTime(){
		return this.addTime;
	}

}
