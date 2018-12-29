package com.seven.contract.manage.model;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {

	/**
	 *	id
	 */
	private int id;

	/**
	 *	用户MID
	 */
	private int mid;

	/**
	 *	消息类型
	 */
	private String type;

	/**
	 *	消息内容
	 */
	private String content;

	/**
	 *	备注
	 */
	private String remark;

	/**
	 *	添加时间
	 */
	private java.util.Date addTime;

	/**
	 *	已读标识 Y:已读 N:未读
	 */
	private String readFlag;



	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
	}

	public void setMid(int mid){
		this.mid = mid;
	}

	public int getMid(){
		return this.mid;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return this.type;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return this.content;
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

	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}
}
