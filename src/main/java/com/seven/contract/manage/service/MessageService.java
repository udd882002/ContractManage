package com.seven.contract.manage.service;

import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.model.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {

	public void addMessage(Message message);

	public PageResult<Message> getListByPage(Map<String, Object> params, int pageNum, int pageSize);

	public void deleteById(long id);

	public void updateMessage(Message message);

	public Message selectOneById(long id);

	public List<Message> getList(Map<String, Object> params);
}
