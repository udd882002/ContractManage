package com.seven.contract.manage.service.impl;

import com.seven.contract.manage.dao.ContractJoinDao;
import com.seven.contract.manage.model.ContractJoin;
import com.seven.contract.manage.service.ContractJoinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContractJoinServiceImpl implements ContractJoinService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ContractJoinDao contractJoinDao;

	@Override
	public void addContractJoin(ContractJoin contractJoin) {
		contractJoinDao.insert(contractJoin);
	}

	@Override
	public void updateContractJoin(ContractJoin contractJoin){
		contractJoinDao.update(contractJoin);
	}
	@Override
	public void deleteById(long id) {
		contractJoinDao.deleteById(id);
	}
	@Override
	public ContractJoin selectOneById(long id) {
		ContractJoin contractJoin = contractJoinDao.selectOne(id);
		return contractJoin;
	}

	@Override
	public List<ContractJoin> selectList(Map<String, Object> params) {
		return contractJoinDao.selectList(params);
	}

}
