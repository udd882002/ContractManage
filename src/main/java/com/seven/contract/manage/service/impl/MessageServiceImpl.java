package com.seven.contract.manage.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.dao.MessageDao;
import com.seven.contract.manage.model.Message;
import com.seven.contract.manage.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class MessageServiceImpl implements MessageService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MessageDao messageDao;

	@Override
	public void addMessage(Message message) {
		message.setReadFlag("N");
		message.setAddTime(new Date());
		messageDao.insert(message);
	}
	@Override
	public void updateMessage(Message message){
		messageDao.update(message);
	}
	@Override
	public void deleteById(long id) {
		messageDao.deleteById(id);
	}
	@Override
	public Message selectOneById(long id) {
		Message message = messageDao.selectOne(id);
		return message;
	}
	@Override
	public PageResult<Message> getListByPage(Map<String, Object> params, int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);

		List<Message> dataList = messageDao.selectList(params);
		Page page=(Page) dataList;
		PageResult pageResult=new PageResult();
		pageResult.setRecords(page.getTotal());
		pageResult.setRows(dataList);
		pageResult.setPage(page.getPageNum());
		pageResult.setTotal(page.getPages());
		return pageResult;

	}

	@Override
	public List<Message> getList(Map<String, Object> params) {
		return messageDao.selectList(params);
	}
}
