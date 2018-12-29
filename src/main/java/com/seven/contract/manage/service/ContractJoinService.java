package com.seven.contract.manage.service;

import com.seven.contract.manage.model.ContractJoin;

import java.util.List;
import java.util.Map;

public interface ContractJoinService {

	public void addContractJoin(ContractJoin contractJoin);

	public void deleteById(long id);

	public void updateContractJoin(ContractJoin contractJoin);

	public ContractJoin selectOneById(long id);

	public List<ContractJoin> selectList(Map<String, Object> params);
}
