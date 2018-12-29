package com.seven.contract.manage.model;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Contract implements Serializable {

	/**
	 *	id
	 */
	private int id;

	/**
	 *	合同名
	 */
	private String contractName;

	/**
	 *	合同编号
	 */
	private String contractNo;

	/**
	 *	开始时间
	 */
	private java.util.Date startTime;

	/**
	 *	结束时间
	 */
	private java.util.Date endTime;

	/**
	 * 完成时间
	 */
	private java.util.Date completeTime;

	/**
	 *	状态 draft:草稿 signing:签约中  fail:失败 complete:完成
	 */
	private String status;

	/**
	 *	备注
	 */
	private String remark;

	/**
	 *	是否绝密合同 Y:是 N:否
	 */
	private String secretContract;

	/**
	 *	合同地址
	 */
	private String contractUrl;

	/**
	 *	合同发起上链地址
	 */
	private String contractStartBlockUrl;

	/**
	 *	合同完成上链地址
	 */
	private String contractEndBlockUrl;

	/**
	 *	创建时间
	 */
	private java.util.Date addTime;

	private String txId;

	private String program;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getContractStartBlockUrl() {
		return contractStartBlockUrl;
	}

	public void setContractStartBlockUrl(String contractStartBlockUrl) {
		this.contractStartBlockUrl = contractStartBlockUrl;
	}

	public String getContractEndBlockUrl() {
		return contractEndBlockUrl;
	}

	public void setContractEndBlockUrl(String contractEndBlockUrl) {
		this.contractEndBlockUrl = contractEndBlockUrl;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}
}
