package com.seven.contract.manage.service;

import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.model.Contact;

import java.util.List;
import java.util.Map;

public interface ContactService {

	public void addContact(Contact contact);

	public PageResult<Contact> getListByPage(Map<String, Object> params, int pageNum, int pageSize);

	public void deleteById(long id);

	public void updateContact(Contact contact);

	public Contact selectOneById(long id);

	public void save(Contact contact);

	public List<Map<String, Object>> getListForSearch(long mid, String search);

	public List<Contact> selectList(Map<String, Object> params);
}
