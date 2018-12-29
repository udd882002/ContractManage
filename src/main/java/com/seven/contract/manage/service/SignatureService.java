package com.seven.contract.manage.service;

import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.model.Signature;

import java.util.List;
import java.util.Map;

public interface SignatureService {

	public void addSignature(Signature signature);

	public PageResult<Signature> getListByPage(Map<String, Object> params, int pageNum, int pageSize);

	public void deleteById(long id);

	public void updateSignature(Signature signature);

	public Signature selectOneById(long id);

	public List<Signature> selectList(Map<String, Object> params);

}
