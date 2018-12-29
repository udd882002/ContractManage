package com.seven.contract.manage.dao;

import com.seven.contract.manage.common.BaseDao;
import com.seven.contract.manage.model.Contract;
import com.seven.contract.manage.vo.ContractVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ContractDao extends BaseDao<Contract> {

	public List<ContractVo> selectListByManage(Map<String, Object> params);
}

