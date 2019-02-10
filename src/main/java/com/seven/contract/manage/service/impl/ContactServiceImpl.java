package com.seven.contract.manage.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.dao.ContactDao;
import com.seven.contract.manage.model.Contact;
import com.seven.contract.manage.service.ContactService;
import com.seven.contract.manage.vo.ContactVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContactServiceImpl implements ContactService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ContactDao contactDao;

	@Override
	public void addContact(Contact contact) {
		contactDao.insert(contact);
	}

	@Override
	public void updateContact(Contact contact){
		contactDao.update(contact);
	}

	@Override
	public void deleteById(long id) {
		contactDao.deleteById(id);
	}

	@Override
	public Contact selectOneById(long id) {
		Contact contact = contactDao.selectOne(id);
		return contact;
	}

	@Override
	public void save(Contact contact) {
		contactDao.insert(contact);
	}

	@Override
	public PageResult<Contact> getListByPage(Map<String, Object> params, int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);

		List<Contact> dataList = contactDao.selectList(params);
		Page page=(Page) dataList;
		PageResult pageResult=new PageResult();
		pageResult.setRecords(page.getTotal());
		pageResult.setRows(dataList);
		pageResult.setPage(page.getPageNum());
		pageResult.setTotal(page.getPages());
		return pageResult;

	}

	@Override
	public List<ContactVo> getListForSearch(long mid, String search) {
		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}
		return contactDao.selectListForSearch(params);
	}

	@Override
	public List<Contact> selectList(Map<String, Object> params) {
		return contactDao.selectList(params);
	}
}
