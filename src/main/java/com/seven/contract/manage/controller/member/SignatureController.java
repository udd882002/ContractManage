package com.seven.contract.manage.controller.member;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.model.Signature;
import com.seven.contract.manage.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/signature")
public class SignatureController extends BaseController {

	@Autowired
	private SignatureService signatureService;

	/**
	 * 查询签章
	 * @param request
	 * @return
     */
	@PostMapping("/info")
	public ApiResult info(HttpServletRequest request) {

		Member member;

		//登陆检测
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		List<Signature> signatures = signatureService.selectList(params);
		logger.debug("signatures = {}", JSON.toJSONString(signatures));

		for (Signature signature : signatures) {
			String baseStr = signature.getUrl().replace(" ", "+");
			signature.setUrl(baseStr);
		}

		logger.debug("signatures = {}", JSON.toJSONString(signatures));

		return ApiResult.success(request, signatures);
	}

	/**
	 * 添加修改签章
	 * @param request
	 * @param url
     * @return
     */
	@PostMapping("/add")
	public ApiResult add(HttpServletRequest request, String url) {

		Member member;

		//登陆检测
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Signature signature = new Signature();
		signature.setMid(mid);
		signature.setUrl(url);
		signature.setAddTime(new Date());
		signatureService.addSignature(signature);

		return ApiResult.success(request);
	}
}

