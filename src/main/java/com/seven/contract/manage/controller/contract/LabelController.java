package com.seven.contract.manage.controller.contract;

import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.model.Label;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.service.LabelService;
import com.seven.contract.manage.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/label")
public class LabelController extends BaseController {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LabelService labelService;

	@PostMapping("/list")
	public ApiResult getLabelList(HttpServletRequest request) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		List<Label> labels = labelService.selectList(params);

		Map<String, Object> result = new HashMap<>();
		result.put("labels", labels);

		return ApiResult.success(request, result);
	}

	@PostMapping("/add")
	public ApiResult addLabel(HttpServletRequest request, String labelName) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if(StringUtils.isEmpty(labelName)) {
			return ApiResult.fail(request, "标签名称不能为空");
		}

		Map<String, Object> result = new HashMap<>();
		try{
			int id = labelService.addLabel(mid, labelName);
			result.put("id", id);
		} catch (Exception e) {
			return ApiResult.fail(request, e.getMessage());
		}
		return ApiResult.success(request, result);
	}

	@PostMapping("/update")
	public ApiResult updateLabel(HttpServletRequest request, String id, String labelName) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "标签ID错误");
		}

		if (StringUtils.isEmpty(labelName)) {
			ApiResult.fail(request, "标签名称不能为空");
		}

		Label label = labelService.selectOneById(Long.valueOf(id));
		if (label == null) {
			ApiResult.fail(request, "标签信息不存在");
		}

		if (label.getMid() != mid) {
			ApiResult.fail(request, "非法操作");
		}

		label.setLabelName(labelName);
		label.setAddTime(new Date());
		try{
			labelService.updateLabel(label);
		} catch (Exception e) {
			e.printStackTrace();
			return  ApiResult.fail(request, "操作失败");
		}

		return ApiResult.success(request);
	}

	@PostMapping("/delete")
	public ApiResult deleteLabel(HttpServletRequest request, String id) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "标签ID错误");
		}

		Label label = labelService.selectOneById(Long.valueOf(id));
		if (label == null) {
			ApiResult.fail(request, "标签信息不存在");
		}

		if (label.getMid() != mid) {
			ApiResult.fail(request, "非法操作");
		}

		try{
			labelService.deleteById(Long.valueOf(id));
		} catch (Exception e) {
			return  ApiResult.fail(request, "操作失败");
		}

		return ApiResult.success(request);
	}

}

