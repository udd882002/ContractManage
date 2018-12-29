package com.seven.contract.manage.dao;

import com.seven.contract.manage.common.BaseDao;
import com.seven.contract.manage.model.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageDao extends BaseDao<Message> {

}

