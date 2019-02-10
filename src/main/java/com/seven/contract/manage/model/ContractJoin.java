package com.seven.contract.manage.model;
import java.io.Serializable;

@SuppressWarnings("serial")
public class ContractJoin implements Serializable {

	/**
	 *	id
	 */
	private int id;

	/**
	 *	合同ID
	 */
	private int contractId;

	/**
	 *	参于人mid
	 */
	private int mid;

	/**
	 *	参于人名称
	 */
	private String name;

	/**
	 *	参于人手机号
	 */
	private String phone;

	/**
	 *	参于人签字时间
	 */
	private java.util.Date signTime;

	/**
	 *	参于人文件hash
	 */
	private String fileHash;

	/**
	 *	参于人密文
	 */
	private String privateKeys;

	/**
	 *	状态 draft:草稿 waitmine:待我签 waitother:待其它人签 refuse:拒签 refuseother:被拒签 complete:完成
	 */
	private String status;

	/**
	 *	备注
	 */
	private String remark;

	/**
	 * 角色 initiator:发起者 join:参与者
	 */
	private String role;

	/**
	 * 签约顺序
	 */
	private int sort;

	/**
	 * 标签ID
	 */
	private int labelId;

	/**
	 * 是否已归档 N:否 Y:是
	 */
	private String isArchive;


	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return this.id;
	}

	public void setContractId(int contractId){
		this.contractId = contractId;
	}

	public int getContractId(){
		return this.contractId;
	}

	public void setMid(int mid){
		this.mid = mid;
	}

	public int getMid(){
		return this.mid;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return this.phone;
	}

	public void setSignTime(java.util.Date signTime){
		this.signTime = signTime;
	}

	public java.util.Date getSignTime(){
		return this.signTime;
	}

	public void setFileHash(String fileHash){
		this.fileHash = fileHash;
	}

	public String getFileHash(){
		return this.fileHash;
	}

	public String getPrivateKeys() {
		return privateKeys;
	}

	public void setPrivateKeys(String privateKeys) {
		this.privateKeys = privateKeys;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return this.status;
	}

	public void setRemark(String remark){
		this.remark = remark;
	}

	public String getRemark(){
		return this.remark;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getLabelId() {
		return labelId;
	}

	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}

	public String getIsArchive() {
		return isArchive;
	}

	public void setIsArchive(String isArchive) {
		this.isArchive = isArchive;
	}
}
