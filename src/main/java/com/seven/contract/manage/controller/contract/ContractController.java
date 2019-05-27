package com.seven.contract.manage.controller.contract;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.controller.contract.data.request.AddDraftRequest;
import com.seven.contract.manage.controller.contract.data.request.InitiateSigningRequest;
import com.seven.contract.manage.enums.ContractJoinRoleEnum;
import com.seven.contract.manage.enums.ContractJoinStatusEnum;
import com.seven.contract.manage.enums.ContractStatusEnum;
import com.seven.contract.manage.model.Contract;
import com.seven.contract.manage.model.ContractJoin;
import com.seven.contract.manage.model.Label;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.service.ContractJoinService;
import com.seven.contract.manage.service.ContractService;
import com.seven.contract.manage.service.LabelService;
import com.seven.contract.manage.service.MemberService;
import com.seven.contract.manage.utils.DateUtil;
import com.seven.contract.manage.utils.NumberUtil;
import com.seven.contract.manage.utils.SmsSendUtil;
import com.seven.contract.manage.vo.ContractVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/contract")
public class ContractController extends BaseController {

	private static final String SIGNING_VERIFY_CODE_SESSION_KEY = "signing_verify_code_session_key";

	@Autowired
	private ContractService contractService;

	@Autowired
	private ContractJoinService contractJoinService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private LabelService labelService;

	/**
	 * 全部文件
	 * @param request
	 * @return
     */
	@PostMapping("/all")
	public ApiResult getAll(HttpServletRequest request,
							@RequestParam(value = "search", required = false) String search,
							@RequestParam(value = "startTime", required = false) String startTime,
							@RequestParam(value = "endTime", required = false) String endTime,
							@RequestParam(value = "validTime", required = false) String validTime,
							@RequestParam(value = "labelId", required = false) String labelId) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "ALL");

		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}

		if (NumberUtil.isNumeric(labelId)) {
			params.put("labelId", Long.valueOf(labelId));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		params = queryTimeParse(startTime, endTime, validTime, null, params);

		logger.debug("params = {}", JSON.toJSONString(params));

		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			if (vo.getRole().equals(ContractJoinRoleEnum.initiator.toString())) {
				map.put("initiator", "我");
			} else {
				params = new HashMap<>();
				params.put("contractId", vo.getId());
				params.put("role", "initiator");
				List<ContractJoin> contractJoins = contractJoinService.selectList(params);
				map.put("initiator", contractJoins.get(0).getName());
			}

			map.put("startTime", sdf.format(vo.getStartTime()));
			map.put("endTime", sdf.format(vo.getEndTime()));

			if (!StringUtils.isEmpty(vo.getValidTime())) {
				map.put("validTime", sdf.format(vo.getValidTime()));
			} else {
				map.put("validTime", "");
			}

			if (vo.getStatus().equals(ContractJoinStatusEnum.waitmine.toString())) {
				map.put("status", "待我签");
			} else if(vo.getStatus().equals(ContractJoinStatusEnum.complete.toString())) {
				map.put("status", "已完成");
			} else if(vo.getStatus().equals(ContractJoinStatusEnum.refuse.toString())
					|| vo.getStatus().equals(ContractJoinStatusEnum.refuseother.toString())) {
				map.put("status", "已拒签/被拒签");
			} else {
				map.put("status", "待对方签");
			}

			String labelName = "无";
			int labelIdQuery = vo.getLabelId();
			if (labelIdQuery != 0) {
				Label label = labelService.selectOneById(labelIdQuery);
				if (label != null) {
					labelName = label.getLabelName();
				}
			}
			map.put("labelName", labelName);

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 即将截止
	 * @param request
	 * @return
	 */
	@PostMapping("/expire")
	public ApiResult getExpire(HttpServletRequest request,
							@RequestParam(value = "search", required = false) String search,
							@RequestParam(value = "startTime", required = false) String startTime,
							@RequestParam(value = "endTime", required = false) String endTime,
							@RequestParam(value = "validTime", required = false) String validTime,
							   @RequestParam(value = "labelId", required = false) String labelId) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "EXPIRE");

		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}

		if (NumberUtil.isNumeric(labelId)) {
			params.put("labelId", Long.valueOf(labelId));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		params = queryTimeParse(startTime, endTime, validTime, null, params);

		logger.debug("params = {}", JSON.toJSONString(params));

		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			if (vo.getRole().equals(ContractJoinRoleEnum.initiator.toString())) {
				map.put("initiator", "我");
			} else {
				params = new HashMap<>();
				params.put("contractId", vo.getId());
				params.put("role", "initiator");
				List<ContractJoin> contractJoins = contractJoinService.selectList(params);
				map.put("initiator", contractJoins.get(0).getName());
			}

			map.put("startTime", sdf.format(vo.getStartTime()));
			map.put("endTime", sdf.format(vo.getEndTime()));

			if (!StringUtils.isEmpty(vo.getValidTime())) {
				map.put("validTime", sdf.format(vo.getValidTime()));
			} else {
				map.put("validTime", "");
			}

			if (vo.getStatus().equals(ContractJoinStatusEnum.waitmine.toString())) {
				map.put("status", "待我签");
			} else if(vo.getStatus().equals(ContractJoinStatusEnum.complete.toString())) {
				map.put("status", "已完成");
			} else if(vo.getStatus().equals(ContractJoinStatusEnum.refuse.toString())
					|| vo.getStatus().equals(ContractJoinStatusEnum.refuseother.toString())) {
				map.put("status", "已拒签/被拒签");
			} else {
				map.put("status", "待对方签");
			}

			String labelName = "无";
			int labelIdQuery = vo.getLabelId();
			if (labelIdQuery != 0) {
				Label label = labelService.selectOneById(labelIdQuery);
				if (label != null) {
					labelName = label.getLabelName();
				}
			}
			map.put("labelName", labelName);

			long remainDays = DateUtil.until(vo.getEndTime());
			if (remainDays < 0) {
				map.put("remainTime", "已截止");
			} else {
				map.put("remainTime", "还有" + remainDays + "天截止");
			}

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 待我签
	 * @param request
	 * @return
	 */
	@PostMapping("/waitmine")
	public ApiResult getWaitMine(HttpServletRequest request,
								 @RequestParam(value = "search", required = false) String search,
								 @RequestParam(value = "startTime", required = false) String startTime,
								 @RequestParam(value = "endTime", required = false) String endTime,
								 @RequestParam(value = "validTime", required = false) String validTime,
								 @RequestParam(value = "labelId", required = false) String labelId) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "MINE");

		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}

		if (NumberUtil.isNumeric(labelId)) {
			params.put("labelId", Long.valueOf(labelId));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		params = queryTimeParse(startTime, endTime, validTime, null, params);
		logger.debug("params = {}", JSON.toJSONString(params));

		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			if (vo.getRole().equals(ContractJoinRoleEnum.initiator)) {
				map.put("initiator", "我");
			} else {
				params = new HashMap<>();
				params.put("contractId", vo.getId());
				params.put("role", "initiator");
				List<ContractJoin> contractJoins = contractJoinService.selectList(params);
				map.put("initiator", contractJoins.get(0).getName());
			}

			String labelName = "无";
			int labelIdQuery = vo.getLabelId();
			if (labelIdQuery != 0) {
				Label label = labelService.selectOneById(labelIdQuery);
				if (label != null) {
					labelName = label.getLabelName();
				}
			}
			map.put("labelName", labelName);

			map.put("startTime", sdf.format(vo.getStartTime()));
			map.put("endTime", sdf.format(vo.getEndTime()));

			if (!StringUtils.isEmpty(vo.getValidTime())) {
				map.put("validTime", sdf.format(vo.getValidTime()));
			} else {
				map.put("validTime", "");
			}

			long remainDays = DateUtil.until(vo.getEndTime());
			if (remainDays < 0) {
				map.put("remainTime", "已截止");
			} else {
				map.put("remainTime", "还有" + remainDays + "天截止");
			}

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 待对方签
	 * @param request
	 * @return
	 */
	@PostMapping("/waitother")
	public ApiResult getWaitOther(HttpServletRequest request,
								  @RequestParam(value = "search", required = false) String search,
								  @RequestParam(value = "startTime", required = false) String startTime,
								  @RequestParam(value = "endTime", required = false) String endTime,
								  @RequestParam(value = "validTime", required = false) String validTime,
								  @RequestParam(value = "labelId", required = false) String labelId) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "OTHER");

		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}

		if (NumberUtil.isNumeric(labelId)) {
			params.put("labelId", Long.valueOf(labelId));
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		params = queryTimeParse(startTime, endTime, validTime, null, params);
		logger.debug("params = {}", JSON.toJSONString(params));

		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			String signinArticle = "";
			params = new HashMap<>();
			params.put("contractId", vo.getId());
			params.put("status", ContractJoinStatusEnum.waitmine.toString());
			List<ContractJoin> contractJoins = contractJoinService.selectList(params);
			for(ContractJoin contractJoin : contractJoins) {
				if (signinArticle.equals("")) {
					signinArticle = contractJoin.getName();
				} else {
					signinArticle = signinArticle + "," + contractJoin.getName();
				}
			}
			map.put("signinArticle", signinArticle);

			map.put("startTime", sdf.format(vo.getStartTime()));
			map.put("endTime", sdf.format(vo.getEndTime()));

			if (!StringUtils.isEmpty(vo.getValidTime())) {
				map.put("validTime", sdf.format(vo.getValidTime()));
			} else {
				map.put("validTime", "");
			}

			String labelName = "无";
			int labelIdQuery = vo.getLabelId();
			if (labelIdQuery != 0) {
				Label label = labelService.selectOneById(labelIdQuery);
				if (label != null) {
					labelName = label.getLabelName();
				}
			}
			map.put("labelName", labelName);

			long remainDays = DateUtil.until(vo.getEndTime());
			if (remainDays < 0) {
				map.put("remainTime", "已截止");
			} else {
				map.put("remainTime", "还有" + remainDays + "天截止");
			}

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 已完成
	 * @param request
	 * @return
	 */
	@PostMapping("/complete")
	public ApiResult getComplete(HttpServletRequest request,
								 @RequestParam(value = "search", required = false) String search,
								 @RequestParam(value = "startTime", required = false) String startTime,
								 @RequestParam(value = "endTime", required = false) String endTime,
								 @RequestParam(value = "validTime", required = false) String validTime,
								 @RequestParam(value = "completeTime", required = false) String completeTime,
								 @RequestParam(value = "labelId", required = false) String labelId) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "COMPLETE");
		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}

		if (NumberUtil.isNumeric(labelId)) {
			params.put("labelId", Long.valueOf(labelId));
		}

		params = queryTimeParse(startTime, endTime, validTime, completeTime, params);
		logger.debug("params = {}", JSON.toJSONString(params));
		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			String signin = "";
			String fileHash = "";
			String signatureFlag = "N";
			String isShowArchive = "N";

			if (vo.getContractStatus().equals(ContractStatusEnum.complete.toString())
					|| vo.getContractStatus().equals(ContractStatusEnum.fail.toString())) {
				isShowArchive = "Y";
			}

			params = new HashMap<>();
			params.put("contractId", vo.getId());
			List<ContractJoin> contractJoins = contractJoinService.selectList(params);
			for(ContractJoin contractJoin : contractJoins) {
				if (contractJoin.getMid() == mid) {
					if (StringUtils.isEmpty(contractJoin.getPrivateKeys())) {
						signatureFlag = "Y";
					}
				}

				if (StringUtils.isEmpty(signin)) {
					signin = contractJoin.getName();
				} else {
					if (contractJoin.getMid() == mid) {
						signin = signin + ",我";
					} else {
						signin = signin + "," + contractJoin.getName();
					}
				}
				fileHash = contractJoin.getFileHash();
			}

			String labelName = "无";
			int labelIdQuery = vo.getLabelId();
			if (labelIdQuery != 0) {
				Label label = labelService.selectOneById(labelIdQuery);
				if (label != null) {
					labelName = label.getLabelName();
				}
			}

			map.put("signin", signin);

			map.put("startTime", sdf.format(vo.getStartTime()));
			map.put("endTime", sdf.format(vo.getEndTime()));

			if (!StringUtils.isEmpty(vo.getCompleteTime())) {
				map.put("completeTime", sdf.format(vo.getCompleteTime()));
			} else {
				map.put("completeTime", "");
			}

			if (!StringUtils.isEmpty(vo.getValidTime())) {
				map.put("validTime", sdf.format(vo.getValidTime()));
			} else {
				map.put("validTime", "");
			}

			map.put("fileHash", fileHash);
			map.put("flag", signatureFlag);
			map.put("labelName", labelName);
			map.put("isShowArchive", isShowArchive);

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	private Map<String, Object> queryTimeParse(String startTime, String endTime, String validTime, String completeTime, Map<String, Object> params) {

		logger.debug("queryTimeParse begin startTime = {}, endTime = {}, validTime = {}, completeTime = {}, params = {}", startTime, endTime, validTime, completeTime, JSON.toJSON(params));

		if (!StringUtils.isEmpty(startTime)) {
			String[] startTimes = startTime.split("-");
			if (startTimes.length == 2) {
				if (!StringUtils.isEmpty(startTimes[0]) && !StringUtils.isEmpty(startTimes[1])) {
					params.put("startTimeBegin", startTimes[0]);
					params.put("startTimeEnd", startTimes[1]);
				}
			}
		}

		if (!StringUtils.isEmpty(endTime)) {
			String[] endTimes = endTime.split("-");
			if (endTimes.length == 2) {
				if (!StringUtils.isEmpty(endTimes[0]) && !StringUtils.isEmpty(endTimes[1])) {
					params.put("endTimeBegin", endTimes[0]);
					params.put("endTimeEnd", endTimes[1]);
				}
			}
		}

		if (!StringUtils.isEmpty(validTime)) {
			String[] validTimes = validTime.split("-");
			if (validTimes.length == 2) {
				if (!StringUtils.isEmpty(validTimes[0]) && !StringUtils.isEmpty(validTimes[1])) {
					params.put("validTimeBegin", validTimes[0]);
					params.put("validTimeEnd", validTimes[1]);
				}
			}
		}

		if (!StringUtils.isEmpty(completeTime)) {
			String[] completeTimes = completeTime.split("-");
			if (completeTimes.length == 2) {
				if (!StringUtils.isEmpty(completeTimes[0]) && !StringUtils.isEmpty(completeTimes[1])) {
					params.put("completeTimeBegin", completeTimes[0]);
					params.put("completeTimeEnd", completeTimes[1]);
				}
			}
		}

		logger.debug("queryTimeParse end params = {}", JSON.toJSON(params));

		return params;
	}

	/**
	 * 已拒签/被拒签
	 * @param request
	 * @return
	 */
	@PostMapping("/refuse")
	public ApiResult getRefuse(HttpServletRequest request,
							   @RequestParam(value = "search", required = false) String search,
							   @RequestParam(value = "labelId", required = false) String labelId) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "REFUSE");
		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}

		if (NumberUtil.isNumeric(labelId)) {
			params.put("labelId", Long.valueOf(labelId));
		}

		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			String isShowArchive = "N";
			if (vo.getContractStatus().equals(ContractStatusEnum.complete.toString())
					|| vo.getContractStatus().equals(ContractStatusEnum.fail.toString())) {
				isShowArchive = "Y";
			}

			map.put("isShowArchive", isShowArchive);

			params = new HashMap<>();
			params.put("contractId", vo.getId());
			List<ContractJoin> contractJoins = contractJoinService.selectList(params);
			for (ContractJoin contractJoin : contractJoins) {
				if (contractJoin.getRole().equals(ContractJoinRoleEnum.initiator.toString())) {
					if(contractJoin.getMid() == mid) {
						map.put("initiator", "我");
					} else {
						map.put("initiator", contractJoin.getName());
					}
				}

				if (contractJoin.getStatus().equals(ContractJoinStatusEnum.refuse.toString())) {
					if (vo.getMid() == mid) {
						map.put("denied", "我");
					} else {
						map.put("denied", contractJoin.getName());
					}

					map.put("deniedTime", sdf.format(vo.getSignTime()));
				}

			}

			String labelName = "无";
			int labelIdQuery = vo.getLabelId();
			if (labelIdQuery != 0) {
				Label label = labelService.selectOneById(labelIdQuery);
				if (label != null) {
					labelName = label.getLabelName();
				}
			}
			map.put("labelName", labelName);

			map.put("startTime", sdf.format(vo.getStartTime()));

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 草稿箱
	 * @param request
	 * @return
	 */
	@PostMapping("/draft")
	public ApiResult getDraft(HttpServletRequest request, @RequestParam(value = "search", required = false) String search) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "DRAFT");
		if (!StringUtils.isEmpty(search)) {
			params.put("search", search);
		}
		List<ContractVo> list = contractService.selectListByManage(params);

		List<Map<String, Object>> result = new ArrayList<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		for (ContractVo vo : list) {

			Map<String, Object> map = new HashMap<>();
			map.put("id", vo.getId());
			map.put("contract", vo.getContractName());

			String signUser = "";

			params = new HashMap<>();
			params.put("contractId", vo.getId());
			List<ContractJoin> contractJoins = contractJoinService.selectList(params);
			for(ContractJoin contractJoin : contractJoins) {
				if (contractJoin.getRole().equals(ContractJoinRoleEnum.join.toString())) {
					if (StringUtils.isEmpty(signUser)) {
						signUser = contractJoin.getName();
					} else {
						signUser = signUser + "," + contractJoin.getName();
					}
				}
			}

			map.put("signUser", signUser);

			map.put("lastEditTime", sdf.format(vo.getStartTime()));

			result.add(map);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 删除
	 * @param request
	 * @param id
     * @return
     */
	@PostMapping("/delete")
	public ApiResult delete(HttpServletRequest request, String id) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			return ApiResult.fail(request, "入参格式错误");
		}

		Contract contract = contractService.selectOneById(Long.valueOf(id));

		if (contract == null) {
			return ApiResult.fail(request, "合同信息不存在");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", Long.valueOf(id));
		params.put("role", ContractJoinRoleEnum.initiator.toString());
		List<ContractJoin> contractJoins = contractJoinService.selectList(params);

		long initiatorId = contractJoins.get(0).getMid();

		if (initiatorId != mid || !contract.getStatus().equals(ContractJoinStatusEnum.draft.toString())) {
			return ApiResult.fail(request, "非法删除");
		}

		contractService.deleteById(Long.valueOf(id));

		return ApiResult.success(request);
	}

	/**
	 * 首页统计
	 * @param request
	 * @return
     */
	@PostMapping("/count")
	public ApiResult getCount(HttpServletRequest request) {



		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Map<String, Object> result = new HashMap<>();

		Map<String, Object> params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "MINE");
		List<ContractVo> waitmineList = contractService.selectListByManage(params);

		result.put("waitmine", waitmineList.size());

		params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "OTHER");
		List<ContractVo> waitotherList = contractService.selectListByManage(params);
		result.put("waitother", waitotherList.size());

		params = new HashMap<>();
		params.put("mid", mid);
		params.put("type", "EXPIRE");
		List<ContractVo> expireList = contractService.selectListByManage(params);
		result.put("expire", expireList.size());

		return ApiResult.success(request, result);
	}

	/**
	 * 查询合同信息
	 * @param request
	 * @param id
     * @return
     */
	@PostMapping("/info")
	public ApiResult getInfo(HttpServletRequest request, String id) {


		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			return ApiResult.fail(request, "入参不正确");
		}

		Contract contract = contractService.selectOneById(Long.valueOf(id));
		if (contract == null) {
			return ApiResult.fail(request, "合同信息不存在");
		}

		List<Map<String, Object>> roles = new ArrayList<>();

		List<Map<String, Object>> proofs = new ArrayList<>();

		String initiatorName = "";
		String initiatorPhone = "";

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contract.getId());
		List<ContractJoin> contractJoins = contractJoinService.selectList(params);
		boolean flag = false;
		Date signTime = null;
		for(ContractJoin contractJoin : contractJoins) {

			if (contractJoin.getMid() == mid) {
				flag = true;
				signTime = contractJoin.getSignTime();
				if (contractJoin.getIsArchive().equals('Y')) {
					return ApiResult.fail(request, "合同已归档");
				}
			}

			if (contractJoin.getRole().equals(ContractJoinRoleEnum.initiator.toString())) {
				initiatorName = contractJoin.getName();
				initiatorPhone = contractJoin.getPhone();
			}

			Map<String, Object> role = new HashMap<>();
			if (contractJoin.getMid() == mid) {
				role.put("name", "我");
			} else {
				role.put("name", contractJoin.getName());
			}
			role.put("phone", contractJoin.getPhone());

			Member contractMember = memberService.selectOneById(contractJoin.getMid());
			role.put("publicKeys", contractMember.getPublicKeys());
			roles.add(role);

			Map<String, Object> proof = new HashMap<>();
			if (contractJoin.getStatus().equals(ContractJoinStatusEnum.waitother.toString()) && !StringUtils.isEmpty(contractJoin.getSignTime())) {
				proof.put("status", "已签署");
			} else {
				proof.put("status", ContractJoinStatusEnum.valueOf(contractJoin.getStatus()).getDesc());
			}
			proof.put("fileHash", contractJoin.getFileHash());
			proof.put("privateKeys", contractJoin.getPrivateKeys());
			proofs.add(proof);
		}

		Map<String, Object> result = new HashMap<>();
		if (flag) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			result.put("contractName", contract.getContractName());
			result.put("contractStatus", ContractStatusEnum.valueOf(contract.getStatus()).getDesc());
			result.put("contractUrl", contract.getContractUrl());
			result.put("contractSourceUrl", contract.getContractSourceUrl());
			result.put("contractNo", contract.getContractNo());
			result.put("startTime", sdf.format(contract.getStartTime()));
			result.put("endTime", sdf.format(contract.getEndTime()));
			result.put("completeTime", contract.getCompleteTime() != null ? sdf.format(contract.getCompleteTime()) : "");
			result.put("signTime", signTime != null ? sdf.format(signTime) : "");
			result.put("initiatorName", initiatorName);
			result.put("initiatorPhone", initiatorPhone);
			result.put("remark", contract.getRemark());
			result.put("contractStartBlockUrl", contract.getContractStartBlockUrl());
			result.put("contractEndBlockUrl", contract.getContractEndBlockUrl());
			result.put("remark", contract.getRemark());
			result.put("roleList", roles);
			result.put("proofList", proofs);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 查询我发起合同信息
	 * @param request
	 * @param contractNo
     * @return
     */
	@PostMapping("/initiate")
	public ApiResult getInitiate(HttpServletRequest request, String contractNo) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if(StringUtils.isEmpty(contractNo)) {
			return ApiResult.fail(request, "入参不能为空");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("contractNo", contractNo);
		List<Contract> contracts = contractService.selectList(params);
		if (contracts.size() < 1) {
			return ApiResult.fail(request, "合同信息不存在");
		}
		Contract contract = contracts.get(0);

		params = new HashMap<>();
		params.put("contractId", contract.getId());
		params.put("mid", mid);
		params.put("role", ContractJoinRoleEnum.initiator.toString());
		List<ContractJoin> contractJoins = contractJoinService.selectList(params);
		if (contractJoins.size() < 1) {
			return ApiResult.fail(request, "查询合同信息失败");
		}
		ContractJoin contractJoin = contractJoins.get(0);

		if (contractJoin.getIsArchive().equals("Y")) {
			return ApiResult.fail(request, "合同已归档");
		}

		Map<String, Object> result = new HashMap<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		switch (ContractJoinStatusEnum.valueOf(contractJoin.getStatus()))
		{
			case complete:

				result.put("id", contract.getId());
				result.put("status", contractJoin.getStatus());
				result.put("message", "您发起的合同已完成");
				result.put("contractName", contract.getContractName());
				result.put("endTime", sdf.format(contract.getEndTime()));

				List<Map<String, Object>> joinInfo = new ArrayList<>();
				params = new HashMap<>();
				params.put("contractId", contract.getId());
				List<ContractJoin> joins = contractJoinService.selectList(params);
				for (ContractJoin join : joins) {
					Map<String, Object> map = new HashMap<>();
					if (join.getMid() == mid) {
						map.put("name", "我");
					} else {
						map.put("name", join.getName());
					}
					map.put("phone", join.getPhone());
					map.put("time", sdf.format(join.getSignTime()));
					joinInfo.add(map);
				}
				result.put("joinInfo", joinInfo);
				break;
			case refuseother:

				result.put("id", contract.getId());
				result.put("status", contractJoin.getStatus());
				result.put("message", "您发起的合同被拒签");
				result.put("contractName", contract.getContractName());
				result.put("endTime", sdf.format(contract.getEndTime()));

				joinInfo = new ArrayList<>();
				Map<String, Object> map = new HashMap<>();
				if (contractJoin.getMid() == mid) {
					map.put("name", "我");
				} else {
					map.put("name", contractJoin.getName());
				}
				map.put("phone", contractJoin.getPhone());
				map.put("time", sdf.format(contractJoin.getSignTime()));
				joinInfo.add(map);
				result.put("joinInfo", joinInfo);
				break;
			case waitother:
				result.put("id", contract.getId());
				result.put("status", contractJoin.getStatus());
				result.put("message", "您的签约请求已发出,请等待对方签署");
				result.put("contractName", contract.getContractName());
				result.put("endTime", sdf.format(contract.getEndTime()));

				joinInfo = new ArrayList<>();
				params = new HashMap<>();
				params.put("contractId", contract.getId());
				joins = contractJoinService.selectList(params);
				for (ContractJoin join : joins) {
					map = new HashMap<>();
					if (join.getMid() == mid) {
						map.put("name", "我");
					} else {
						map.put("name", join.getName());
					}
					map.put("phone", join.getPhone());

					if (join.getSignTime() == null) {
						map.put("time", "待签");
					} else {
						map.put("time", sdf.format(join.getSignTime()));
					}

					joinInfo.add(map);
				}
				result.put("joinInfo", joinInfo);
				break;
			default:

				break;
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 查询用户简单合同信息
	 * @param request
	 * @return
     */
	@PostMapping("/simpleInfo")
	public ApiResult getSimpleInfo(HttpServletRequest request, String id) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			return ApiResult.fail(request, "入参不正确");
		}

		Contract contract = contractService.selectOneById(Long.valueOf(id));
		if (contract == null) {
			return ApiResult.fail(request, "合同信息不存在");
		}

		List<Map<String, Object>> roles = new ArrayList<>();

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contract.getId());
		List<ContractJoin> contractJoins = contractJoinService.selectList(params);
		boolean flag = false;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		for(ContractJoin contractJoin : contractJoins) {

			if (contractJoin.getMid() == mid) {
				flag = true;
				if (contractJoin.getIsArchive().equals("Y")) {
					return ApiResult.fail(request, "合同已归档");
				}
			}

			Map<String, Object> role = new HashMap<>();
			if (contractJoin.getMid() == mid) {
				role.put("name", "我");
			} else {
				role.put("name", contractJoin.getName());
			}
			role.put("phone", contractJoin.getPhone());

			role.put("signTime", contractJoin.getSignTime() != null ? sdf.format(contractJoin.getSignTime()) : "");
			roles.add(role);

		}

		Map<String, Object> result = new HashMap<>();
		if (flag) {
			result.put("contractName", contract.getContractName());
			result.put("endTime", sdf.format(contract.getEndTime()));
			result.put("roleList", roles);
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 增加草稿合同
	 * @param request
	 * @param addDraftRequest
     * @return
     */
	@PostMapping("/addDraft")
	public ApiResult addDraft(HttpServletRequest request, AddDraftRequest addDraftRequest) {

		logger.debug("入参addDraftRequest = {}", JSON.toJSONString(addDraftRequest));

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (StringUtils.isEmpty(addDraftRequest.getContractName())) {
			return ApiResult.fail(request, "合同名称不能为空");
		}

		if (StringUtils.isEmpty(addDraftRequest.getEndTime())) {
			return ApiResult.fail(request, "截止日期不能为空");
		}

		if (StringUtils.isEmpty(addDraftRequest.getContractUrl())) {
			return ApiResult.fail(request, "合同地址不能为空");
		}

		if (StringUtils.isEmpty(addDraftRequest.getContactMids())) {
			return ApiResult.fail(request, "联系人MID不能为空");
		}

		if (StringUtils.isEmpty(addDraftRequest.getContractSourceUrl())) {
			return ApiResult.fail(request, "原合同地址不能为空");
		}

		String[] contractMids = addDraftRequest.getContactMids().split(",");
		List<Integer> conMids = new ArrayList<>();
		for (String contactMid : contractMids) {
			if (NumberUtil.isNumeric(contactMid)) {
				conMids.add(Integer.parseInt(contactMid));
			} else {
				return ApiResult.fail(request, "入参联系人错误");
			}
		}

		String contractName = null;
		try {
			contractName = URLDecoder.decode(addDraftRequest.getContractName(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String remark = null;
		if (!StringUtils.isEmpty(addDraftRequest.getRemark())) {
			try {
				remark = URLDecoder.decode(addDraftRequest.getRemark(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		int labelId = 0;
		if (NumberUtil.isNumeric(addDraftRequest.getLabelId())) {
			Label label = labelService.selectOneById(Integer.valueOf(addDraftRequest.getLabelId()));
			if (label != null && label.getMid() == mid) {
				labelId = Integer.valueOf(addDraftRequest.getLabelId());
			}
		}

		Map<String, Object> result = new HashMap<>();
		try {
			int id = contractService.addContract((int) mid, contractName, addDraftRequest.getEndTime(), addDraftRequest.getValidTime(), labelId,
					remark, addDraftRequest.getSecretContract(), addDraftRequest.getContractUrl(),
					ContractStatusEnum.draft, addDraftRequest.getContractSourceUrl(), null, conMids);
			result.put("id", id);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResult.fail(request, "添加草稿合同失败");
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 修改草稿合同,只能修改合同文件地址
	 * @param request
	 * @param id
	 * @param contractUrl
     * @return
     */
	@PostMapping("/updateDraft")
	public ApiResult updateDraft(HttpServletRequest request, String id, String contractUrl, String contractSourceUrl) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "入参合同id不能为空");
		}

		if (StringUtils.isEmpty(contractUrl)) {
			return ApiResult.fail(request, "入参合同地址不能为空");
		}

		if (StringUtils.isEmpty(contractSourceUrl)) {
			return ApiResult.fail(request, "入参原合同地址不能为空");
		}

		try {
			contractService.updateContract(Integer.parseInt(id), (int) mid, contractUrl, contractSourceUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResult.fail(request, e.getMessage());
		}

		return ApiResult.success(request);
	}

	/**
	 * 获取合同签约短信验证码
	 * @param request
	 * @return
	 */
	@PostMapping("/signingVerifyCode")
	public ApiResult getSigningVerifyCode(HttpServletRequest request) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		String phone = member.getPhone();

		int code = NumberUtil.randomCode();

		request.getSession().setAttribute(SIGNING_VERIFY_CODE_SESSION_KEY, String.valueOf(code));

		logger.debug("sessionId:{}, 签约验证码为:{}", request.getSession().getId(), code);

		String msg = "【区块链合同】您的验证码：" + code + "，您正在对合同进行签名，请谨慎操作！";
		boolean flag = SmsSendUtil.sendSms(phone, msg);

		if (!flag) {
			return ApiResult.fail(request, "短信发送失败,请稍后重试");
		}

		return ApiResult.success(request);

//		Map<String, Object> result = new HashMap<>();
//		result.put("code", code);
//
//		return ApiResult.success(request, result);

	}

	/**
	 * 发起签署
	 * @param request
	 * @param initiateSigningRequest
     * @return
     */
	@PostMapping("/initiateSigning")
	public ApiResult initiateSigning(HttpServletRequest request, InitiateSigningRequest initiateSigningRequest) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Object object = request.getSession().getAttribute(SIGNING_VERIFY_CODE_SESSION_KEY);

		if (object == null) {
			return ApiResult.fail(request, "请获取验证码");
		}

		logger.debug("sessionId:{}, 签约验证码为:{}", request.getSession().getId(), initiateSigningRequest.getVerifyCode());

		if (!object.toString().equals(initiateSigningRequest.getVerifyCode())) {
			return ApiResult.fail(request, "验证码不正确");
		}

		request.getSession().removeAttribute(SIGNING_VERIFY_CODE_SESSION_KEY);

		if (StringUtils.isEmpty(initiateSigningRequest.getPrivateKeyPwd())) {
			return ApiResult.fail(request, "密码不能为空");
		}

		Map<String, Object> result = new HashMap<>();
		if (!NumberUtil.isNumeric(initiateSigningRequest.getId()) || initiateSigningRequest.getId().equals("0")) {
			//新增签约
			if (StringUtils.isEmpty(initiateSigningRequest.getContractName())) {
				return ApiResult.fail(request, "合同名称不能为空");
			}

			if (StringUtils.isEmpty(initiateSigningRequest.getEndTime())) {
				return ApiResult.fail(request, "截止日期不能为空");
			}

			if (StringUtils.isEmpty(initiateSigningRequest.getContractUrl())) {
				return ApiResult.fail(request, "合同地址不能为空");
			}

			if (StringUtils.isEmpty(initiateSigningRequest.getContactMids())) {
				return ApiResult.fail(request, "联系人MID不能为空");
			}

			if (StringUtils.isEmpty(initiateSigningRequest.getContractSourceUrl())) {
				return ApiResult.fail(request, "原合同地址不能为空");
			}

			String[] contractMids = initiateSigningRequest.getContactMids().split(",");
			List<Integer> conMids = new ArrayList<>();
			for (String contactMid : contractMids) {
				if (NumberUtil.isNumeric(contactMid)) {
					conMids.add(Integer.parseInt(contactMid));
				} else {
					return ApiResult.fail(request, "入参联系人错误");
				}
			}

			int labelId = 0;
			if (NumberUtil.isNumeric(initiateSigningRequest.getLabelId())) {
				Label label = labelService.selectOneById(Integer.valueOf(initiateSigningRequest.getLabelId()));
				if (label != null && label.getMid() == mid) {
					labelId = Integer.valueOf(initiateSigningRequest.getLabelId());
				}
			}

			try {
				int id = contractService.addContract((int) mid, initiateSigningRequest.getContractName(), initiateSigningRequest.getEndTime(), initiateSigningRequest.getValidTime(), labelId,
						initiateSigningRequest.getRemark(), initiateSigningRequest.getSecretContract(), initiateSigningRequest.getContractUrl(),
						ContractStatusEnum.signing, initiateSigningRequest.getContractSourceUrl(), initiateSigningRequest.getPrivateKeyPwd(), conMids);
				result.put("id", id);
			} catch (Exception e) {
				logger.error("发起签约失败", e);
				return ApiResult.fail(request, "发起签约失败");
			}

		} else {
			//草稿转签约
			if (StringUtils.isEmpty(initiateSigningRequest.getContractUrl())) {
				return ApiResult.fail(request, "合同地址不能为空");
			}

			try {
				contractService.draftToSigning(Long.parseLong(initiateSigningRequest.getId()), (int) mid, initiateSigningRequest.getContractUrl(), initiateSigningRequest.getContractSourceUrl(), initiateSigningRequest.getPrivateKeyPwd());
				result.put("id", initiateSigningRequest.getId());
			} catch (Exception e) {
				e.printStackTrace();
				return ApiResult.fail(request, "发起签约失败");
			}
		}

		return ApiResult.success(request, result);
	}


	/**
	 * 同意或拒绝签署
	 * @param request
	 * @param id	合同ID
	 * @param contractUrl	合同地址
	 * @param signFlag	Y:同意  N:拒签
     * @return
     */
	@PostMapping("/signing")
	public ApiResult signing(HttpServletRequest request, String id, @RequestParam(value="contractUrl", required = false) String contractUrl, String signFlag, String verifyCode, String privateKeyPwd) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		Object object = request.getSession().getAttribute(SIGNING_VERIFY_CODE_SESSION_KEY);

		if (object == null) {
			return ApiResult.fail(request, "请获取验证码");
		}

		logger.debug("sessionId:{}, 签约验证码为:{}", request.getSession().getId(), verifyCode);

		if (!object.toString().equals(verifyCode)) {
			return ApiResult.fail(request, "验证码不正确");
		}

		request.getSession().removeAttribute(SIGNING_VERIFY_CODE_SESSION_KEY);

		if (!NumberUtil.isNumeric(id)) {
			return ApiResult.fail(request, "入参合同ID不正确");
		}

		if (signFlag.equals("Y") && StringUtils.isEmpty(privateKeyPwd)) {
			return ApiResult.fail(request, "密码不能为空");
		}

		if (StringUtils.isEmpty(signFlag) || (!signFlag.equals("Y") && !signFlag.equals("N"))) {
			return ApiResult.fail(request, "同意或拒签标示错误");
		}

		if (signFlag.equals("Y") && StringUtils.isEmpty(contractUrl)) {
			return ApiResult.fail(request, "合同地址不能为空");
		}

		Map<String, Object> result;
		try {
			result = contractService.signing(Long.valueOf(id), (int) mid, signFlag, contractUrl, privateKeyPwd);
		} catch (Exception e) {
			logger.error("签署失败", e);
			return ApiResult.fail(request, e.getMessage());
		}

		return ApiResult.success(request, result);
	}

	/**
	 * 注册区块链,锁定交易
	 * @param request
	 * @param id  合同ID
	 * @param signature 用户加密签名,用逗号分割
     * @return
     */
/*
	@PostMapping("/saveSignature")
	public ApiResult saveSignature(HttpServletRequest request, String id, String signature) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		int mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "入参合同ID不正确");
		}

		if (StringUtils.isEmpty(signature)) {
			ApiResult.fail(request, "加密签名不能为空");
		}

		Map<String, Object> result = new HashMap<>();
		try {
			boolean isBlockchain = contractService.saveSignature(mid, Long.valueOf(id), signature);
			result.put("isBlockchain", isBlockchain);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResult.fail(request, e.getMessage());
		}

		return ApiResult.success(request, result);
	}
*/

	/**
	 * 查询合同状态
	 * @param request
	 * @param id
     * @return
     */
	@PostMapping("/status")
	public ApiResult getStatus(HttpServletRequest request, String id) {

		try {
			this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		if (!NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "入参合同ID不正确");
		}

		Contract contract = contractService.selectOneById(Long.valueOf(id));

		if (contract == null) {
			ApiResult.fail(request, "合同不存在");
		}

		logger.debug("contract = {}", JSON.toJSONString(contract));

		Map<String, Object> result = new HashMap<>();
		result.put("status", contract.getStatus());

		return ApiResult.success(request, result);
	}

	/**
	 * 修改合同标签
	 * @param request
	 * @param id
	 * @return
	 */
	@PostMapping("/updateLabel")
	public ApiResult updateLabel(HttpServletRequest request, String id, String labelId) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "入参合同ID不正确");
		}

		if (!NumberUtil.isNumeric(labelId)) {
			ApiResult.fail(request, "入参标签ID不正确");
		}

		Contract contract = contractService.selectOneById(Long.valueOf(id));

		logger.debug("contract = {}", JSON.toJSONString(contract));

		if (contract == null) {
			ApiResult.fail(request, "合同不存在");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", id);
		params.put("mid", mid);
		List<ContractJoin> contractJoins = contractJoinService.selectList(params);

		if (contractJoins.size() < 1) {
			return ApiResult.fail(request, "合同信息有误");
		}

		Label label = labelService.selectOneById(Long.valueOf(labelId));
		if (label == null) {
			return ApiResult.fail(request, "标签不存在");
		}

		if (label.getMid() != mid) {
			return ApiResult.fail(request, "非法操作");
		}

		ContractJoin contractJoin = contractJoins.get(0);
		logger.debug("contractJoin = {}", JSON.toJSONString(contractJoin));

		contractJoin.setLabelId(Integer.valueOf(labelId));
		contractJoinService.updateContractJoin(contractJoin);

		return ApiResult.success(request);
	}

	/**
	 * 归档合同
	 * @param request
	 * @param id  合同ID
     * @return
     */
	@PostMapping("/archive")
	public ApiResult archive(HttpServletRequest request, String id) {

		Member member;
		try {
			member = this.checkLogin(request);
		} catch (AppRuntimeException e) {
			return ApiResult.fail(request, e.getReqCode(), e.getMsg());
		}

		long mid = member.getId();

		if (!NumberUtil.isNumeric(id)) {
			ApiResult.fail(request, "入参合同ID不正确");
		}

		Contract contract = contractService.selectOneById(Long.valueOf(id));

		if (contract == null) {
			return ApiResult.fail(request, "合同信息不存在");
		}

		if (contract.getStatus().equals(ContractStatusEnum.complete.toString())
				|| contract.getStatus().equals(ContractStatusEnum.fail.toString())) {
			Map<String, Object> params = new HashMap<>();
			params.put("mid", mid);
			params.put("contractId", Long.valueOf(id));
			List<ContractJoin> contractJoins = contractJoinService.selectList(params);
			if (contractJoins.size() < 1) {
				return ApiResult.fail(request, "非法操作");
			}

			ContractJoin contractJoin = contractJoins.get(0);
			contractJoin.setIsArchive("Y");
			contractJoinService.updateContractJoin(contractJoin);

		} else {
			return ApiResult.fail(request, "合同状态不正确");
		}

		return ApiResult.success(request);
	}

	/**
	 * 定时任务,查询待解锁交易合同
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void transaction() {
		logger.debug("transaction start");
		//获取已锁定交易的合同数据
		Map<String, Object> params = new HashMap<>();
		params.put("status", ContractStatusEnum.registered.toString());
		List<Contract> contracts = contractService.selectList(params);
		for (Contract contract : contracts) {
			logger.debug("transaction contract = {}", JSON.toJSONString(contract));
			try {
				contractService.transaction(contract.getId());
			} catch (Exception e) {
				logger.error("上链失败 id = " + contract.getId(), e);
			}
		}

		logger.debug("transaction end");
	}
}

