package com.seven.contract.manage.service;

import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.enums.ContractStatusEnum;
import com.seven.contract.manage.model.Contract;
import com.seven.contract.manage.vo.ContractVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ContractService {

	public int addContract(int mid, String contractName, Date endTime, String remark, String secretContract, String contractUrl, ContractStatusEnum status, List<Integer> contactMids) throws Exception;

	public PageResult<Contract> getListByPage(Map<String, Object> params, int pageNum, int pageSize);

	public void deleteById(long id);

	public long updateContract(long id, int mid, String contractUrl) throws AppRuntimeException;

	public Contract selectOneById(long id);

	public void save(Contract contract);

	public List<Contract> selectList(Map<String, Object> params);

	public List<ContractVo> selectListByManage(Map<String, Object> params);

	public void draftToSigning(long id, int mid, String contractUrl) throws AppRuntimeException;

	public Map<String, Object> signing(long id, int mid, String signFlag, String contractUrl) throws Exception;

	public boolean saveSignature(int mid, long id, String signature) throws Exception;

	public void unlock(long id) throws Exception;
}
