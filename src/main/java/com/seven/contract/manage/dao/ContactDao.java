package com.seven.contract.manage.dao;

import com.seven.contract.manage.common.BaseDao;
import com.seven.contract.manage.model.Contact;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ContactDao extends BaseDao<Contact>{

    public List<Map<String, Object>> selectListForSearch(Map<String, Object> params);
}

