package com.seven.contract.manage.common;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2018/12/11.
 */
public interface BaseDao<T> {

    public void insert(T t);

    public void update(T t);

    public T selectOne(@Param("id") long id);

    public void deleteById(@Param("id") long id);

    public List<T> selectList(Map<String, Object> params);

    public int totalRows(Map<String, Object> params);




}
