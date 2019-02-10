package com.seven.contract.manage.controller.member;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.controller.member.data.request.RegisteredRequest;
import com.seven.contract.manage.enums.MemberTypeEnum;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.service.MemberService;
import com.seven.contract.manage.utils.MobileUtils;
import com.seven.contract.manage.utils.NumberUtil;
import com.seven.contract.manage.utils.SmsSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/member")
public class MemberController extends BaseController {

	private static final String LOGIN_VERIFY_CODE_SESSION_KEY = "login_verify_code_session_key";
	private static final String REGISTERED_VERIFY_CODE_SESSION_KEY = "registered_verify_code_session_key";

	@Autowired
	private MemberService memberService;

	/**
	 * 获取登陆短信验证码
	 * @param request
	 * @param phone
     * @return
     */
	@PostMapping("/loginVerifyCode")
	public ApiResult getLoginVerifyCode(HttpServletRequest request, String phone) {

		this.printHttpHeader(request);

		int code = NumberUtil.randomCode();

		request.getSession().setAttribute(LOGIN_VERIFY_CODE_SESSION_KEY, String.valueOf(code));

		logger.debug("sessionId:{}, 登陆验证码为:{}", request.getSession().getId(), code);

		String msg = "您好，您的登陆验证码是" + code;
		boolean flag = SmsSendUtil.sendSms(phone, msg);

		if (!flag) {
			return ApiResult.fail(request, "短信发送失败,请稍后重试");
		}

		return ApiResult.success(request);
	}

	@PostMapping("/login")
	public ApiResult login(HttpServletRequest request, String phone, String verifyCode) {

		this.printHttpHeader(request);

		Object object = request.getSession().getAttribute(LOGIN_VERIFY_CODE_SESSION_KEY);

		if (object == null) {
			return ApiResult.fail(request, "请获取验证码");
		}

		logger.debug("sessionId:{}, 登陆验证码为:{}", request.getSession().getId(), verifyCode);

		if (!verifyCode.equals(object.toString())) {
			return ApiResult.fail(request, "验证码不正确");
		}

		request.getSession().removeAttribute(LOGIN_VERIFY_CODE_SESSION_KEY);

		Member member = memberService.selectOneByPhone(phone);
		if (member == null) {
			return ApiResult.fail(request, "用户不存在");
		}

		request.getSession().setAttribute(MEMBER_SESSION_KEY, member);

		return ApiResult.success(request);
	}

	/**
	 * 获取注册短信验证码
	 * @param request
	 * @param phone
	 * @return
	 */
	@PostMapping("/registeredVerifyCode")
	public ApiResult getRegisteredVerifyCode(HttpServletRequest request, String phone) {

		int code = NumberUtil.randomCode();

		request.getSession().setAttribute(REGISTERED_VERIFY_CODE_SESSION_KEY, String.valueOf(code));

		logger.debug("注册验证码为:{}", code);

		String msg = "您好，您的注册验证码是" + code;
		boolean flag = SmsSendUtil.sendSms(phone, msg);

		if (!flag) {
			return ApiResult.fail(request, "短信发送失败,请稍后重试");
		}

		return ApiResult.success(request);

	}

	/**
	 * 注册
	 * @param request
	 * @param registeredRequest
     * @return
     */
	@PostMapping("/registered")
	public ApiResult registered(HttpServletRequest request, RegisteredRequest registeredRequest) {

		if (StringUtils.isEmpty(registeredRequest.getType())
				|| MemberTypeEnum.valueOf(registeredRequest.getType()) == null) {
			return ApiResult.fail(request, "用户类型不正确");
		}

		if (StringUtils.isEmpty(registeredRequest.getVerifyCode())) {
			return ApiResult.fail(request, "验证码不能为空");
		}

		if (!MobileUtils.isMobileNO(registeredRequest.getPhone())) {
			return ApiResult.fail(request, "手机号格式不正确");
		}

		if (StringUtils.isEmpty(registeredRequest.getName())) {
			return ApiResult.fail(request, "姓名不能为空");
		}

		if (registeredRequest.getType().equals(MemberTypeEnum.company.toString())) {
			if (StringUtils.isEmpty(registeredRequest.getCreditCode())) {
				return ApiResult.fail(request, "统一社会信用代码不能为空");
			}
		}

		if (StringUtils.isEmpty(registeredRequest.getPrivateKeyPwd())) {
			return ApiResult.fail(request, "密码不能为空");
		}

		Object object = request.getSession().getAttribute(REGISTERED_VERIFY_CODE_SESSION_KEY);

		if (object == null) {
			return ApiResult.fail(request, "请获取验证码");
		}

		logger.debug("session注册验证码为:{}", object.toString());

		if (!registeredRequest.getVerifyCode().equals(object.toString())) {
			return ApiResult.fail(request, "验证码不正确");
		}

		request.getSession().removeAttribute(REGISTERED_VERIFY_CODE_SESSION_KEY);

		Member member = memberService.selectOneByPhone(registeredRequest.getPhone());
		if (member != null) {
			return ApiResult.fail(request, "用户已存在");
		}


		member = new Member();
		member.setType(registeredRequest.getType());
		member.setName(registeredRequest.getName());
		member.setPhone(registeredRequest.getPhone());
		member.setIdCard(registeredRequest.getIdCard());
		member.setMemberImg(registeredRequest.getMemberImg());
		member.setIdCardFrontImg(registeredRequest.getIdCardFrontImg());
		member.setIdCardBackImg(registeredRequest.getIdCardBackImg());
		member.setCreditCode(registeredRequest.getCreditCode());
		member.setAddTime(new Date());
		try {
			memberService.addMember(member, registeredRequest.getPrivateKeyPwd());
		} catch (Exception e) {
			logger.error("注册失败", e);
			return ApiResult.fail(request, e.getMessage());
		}

		request.getSession().setAttribute(LOGIN_VERIFY_CODE_SESSION_KEY, member);

		return ApiResult.success(request);
	}

	/**
	 * 校验手机号是否存在
	 * @param phone
	 * @return
     */
	@PostMapping("/checkPhoneExist")
	public ApiResult checkPhoneExist(HttpServletRequest request, String phone) {

		Member member = memberService.selectOneByPhone(phone);

		Map<String, Object> result = new HashMap<>();
		if (member != null) {
			result.put("isExist", true);
		} else {
			result.put("isExist", false);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 校验用户是否已登陆
	 * @param
	 * @return
	 */
	@PostMapping("/isLogin")
	public ApiResult isLogin(HttpServletRequest request) {

		printHttpHeader(request);

		Object object = request.getSession().getAttribute(MEMBER_SESSION_KEY);

		logger.debug("object = {}", JSON.toJSONString(object));

		Map<String, Object> map = new HashMap<>();
		if (object == null) {
			map.put("isLogin", false);
		} else {
			Member member = (Member) object;
			map.put("isLogin", true);

			Map<String, Object> memberMap = new HashMap();
			memberMap.put("id", member.getId());
			memberMap.put("name", member.getName());
			memberMap.put("phone", member.getPhone());
			memberMap.put("publicKeys", member.getPublicKeys());
			memberMap.put("memberImg", member.getMemberImg());
			map.put("member", memberMap);
		}

		return ApiResult.success(request, map);
	}

	/**
	 * 校验获取用户信息
	 * @param
	 * @return
	 */
	@PostMapping("/info")
	public ApiResult getMemberInfo(HttpServletRequest request) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		member = memberService.selectOneById(mid);

		return ApiResult.success(request, member);
	}

	/**
	 * 修改用户信息
	 * @param request
	 * @param company
	 * @param position
     * @return
     */
	@PostMapping("/update")
	public ApiResult update(HttpServletRequest request, String company, String position) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		member = memberService.selectOneById(mid);

		if (!StringUtils.isEmpty(company)) {
			member.setCompany(company);
		}

		if (!StringUtils.isEmpty(position)) {
			member.setPosition(position);
		}

		memberService.updateMember(member);

		request.getSession().setAttribute(LOGIN_VERIFY_CODE_SESSION_KEY, member);

		return ApiResult.success(request);
	}

	@PostMapping("/loginOut")
	public ApiResult loginOut(HttpServletRequest request) {

		try {
			this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		request.getSession().removeAttribute(MEMBER_SESSION_KEY);

		return ApiResult.success(request);
	}

}

