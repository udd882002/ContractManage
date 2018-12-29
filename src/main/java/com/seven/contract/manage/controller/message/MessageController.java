package com.seven.contract.manage.controller.message;

import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.model.Message;
import com.seven.contract.manage.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/message")
public class MessageController extends BaseController {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MessageService messageService;

	/**
	 * 查询未读信息列表
	 * @param request
	 * @return
     */
	@PostMapping("/unread")
	public ApiResult getUnread(HttpServletRequest request, String type) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		if (!StringUtils.isEmpty(type)) {
			params.put("type", type);
		}
		params.put("readFlag", "N");
		List<Message> messages = messageService.getList(params);

		List<Map<String, Object>> list = new ArrayList<>();
		for (Message message : messages) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", message.getId());
			map.put("content", message.getContent());
			map.put("remark", message.getRemark());
			map.put("type", message.getType());
			list.add(map);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("list", list);

		return ApiResult.success(request, result);
	}

	/**
	 * 修改信息为已读
	 * @param request
	 * @param
     * @return
     */
	@PostMapping("/updateRead")
	public ApiResult updateRead(HttpServletRequest request) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("readFlag", "N");
		List<Message> messages = messageService.getList(params);

		for (Message message : messages) {
			message.setReadFlag("Y");
			messageService.updateMessage(message);
		}

		return ApiResult.success(request);
	}
}

