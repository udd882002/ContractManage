package com.seven.contract.manage.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.dao.SignatureDao;
import com.seven.contract.manage.model.Signature;
import com.seven.contract.manage.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class SignatureServiceImpl implements SignatureService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SignatureDao signatureDao;

	public void addSignature(Signature signature) {
		signatureDao.insert(signature);
	}

	public void updateSignature(Signature signature){
		signatureDao.update(signature);
	}
	public void deleteById(long id) {
		signatureDao.deleteById(id);
	}

	public Signature selectOneById(long id) {
		Signature signature = signatureDao.selectOne(id);
		return signature;
	}

	@Override
	public List<Signature> selectList(Map<String, Object> params) {
		return signatureDao.selectList(params);
	}

	public PageResult<Signature> getListByPage(Map<String, Object> params, int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);

		List<Signature> dataList = signatureDao.selectList(params);
		Page page=(Page) dataList;
		PageResult pageResult=new PageResult();
		pageResult.setRecords(page.getTotal());
		pageResult.setRows(dataList);
		pageResult.setPage(page.getPageNum());
		pageResult.setTotal(page.getPages());
		return pageResult;

	}

}
