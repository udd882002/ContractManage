package com.seven.contract.manage.service;

import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.enums.ContractStatusEnum;
import com.seven.contract.manage.model.Contract;
import com.seven.contract.manage.vo.ContractVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ContractService {

	public int addContract(int mid, String contractName, Date endTime, Date validTime, int labelId, String remark, String secretContract, String contractUrl, ContractStatusEnum status, String contractSourceUrl, String privateKeyPwd, List<Integer> contactMids) throws Exception;

	public PageResult<Contract> getListByPage(Map<String, Object> params, int pageNum, int pageSize);

	public void deleteById(long id);

	public long updateContract(long id, int mid, String contractUrl, String contractSourceUrl) throws Exception;

	public Contract selectOneById(long id);

	public void save(Contract contract);

	public List<Contract> selectList(Map<String, Object> params);

	public List<ContractVo> selectListByManage(Map<String, Object> params);

	public void draftToSigning(long id, int mid, String contractUrl, String contractSourceUrl, String privateKeyPwd) throws Exception;

	public Map<String, Object> signing(long id, int mid, String signFlag, String contractUrl, String privateKeyPwd) throws Exception;
/*
	public boolean saveSignature(int mid, long id, String signature) throws Exception;

	public void unlock(long id) throws Exception;
*/

	public void transaction(long id) throws Exception;
}
