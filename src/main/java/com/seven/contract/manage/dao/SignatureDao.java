package com.seven.contract.manage.dao;

import com.seven.contract.manage.common.BaseDao;
import com.seven.contract.manage.model.Signature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignatureDao extends BaseDao<Signature> {

    public Signature selectOneByMid(@Param("mid") long mid);
}

