package com.seven.contract.manage.controller.member;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.model.Contact;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.service.ContactService;
import com.seven.contract.manage.service.MemberService;
import com.seven.contract.manage.utils.NumberUtil;
import com.seven.contract.manage.vo.ContactVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/contact")
public class ContactController extends BaseController {

	@Autowired
	private ContactService contactService;

	@Autowired
	private MemberService memberService;

	/**
	 * 联系人管理列表查询
	 * @param request
	 * @param search
     * @return
     */
	@PostMapping("/list/search")
	public ApiResult getListForSearch(HttpServletRequest request, String search) {

		Member member;

		//登陆检测
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		List<ContactVo> result = contactService.getListForSearch(mid, search);

		logger.debug("result = {}", JSON.toJSONString(result));

		return ApiResult.success(request, result);
	}

	/**
	 * 添加联系人
	 * @param request
	 * @param type 添加类型 PHONE:手机号 PUBLIC_KEYS:公钥
	 * @param search
     * @return
     */
	@PostMapping("/add")
	public ApiResult add(HttpServletRequest request, String type, String search) {

		Member member;

		//登陆检测
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(search)) {
			return ApiResult.fail(request, "入参不能为空");
		}

		Member contactMember = null;
		if (type.equals("PHONE")) {
			contactMember = memberService.selectOneByPhone(search);
		} else if (type.equals("PUBLIC_KEYS")) {
			contactMember = memberService.selectOneByPublicKeys(search);
		} else {
			return ApiResult.fail(request, "查询类型不正确");
		}

		if (contactMember == null) {
			return ApiResult.fail(request, "用户不存在");
		}

		if (contactMember.getId() == mid) {
			return ApiResult.fail(request, "联系人不能添加自己");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("contactMid", contactMember.getId());
		List<Contact> contacts = contactService.selectList(params);
		if (contacts != null && contacts.size() > 0) {
			return ApiResult.fail(request, "联系人已存在");
		}

		Contact contact = new Contact();
		contact.setMid(mid);
		contact.setContactMid(contactMember.getId());
		contact.setAddTime(new Date());
		contactService.save(contact);

		return ApiResult.success(request);
	}

	/**
	 * 修改联系人备注
	 * @param request
	 * @param id 联系人关系ID
	 * @param remark
     * @return
     */
	@PostMapping("/update")
	public ApiResult update(HttpServletRequest request, String id, String remark) {

		Member member;
		//登陆检测
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}
		long mid = member.getId();

		if (!NumberUtil.isNumeric(id) || StringUtils.isEmpty(remark)) {
			return ApiResult.fail(request, "入参错误");
		}

		Contact contact = contactService.selectOneById(Long.valueOf(id));
		if (contact == null || contact.getMid() != mid) {
			return ApiResult.fail(request, "非法操作");
		}

		contact.setRemark(remark);
		contactService.updateContact(contact);

		return ApiResult.success(request);
	}
}

