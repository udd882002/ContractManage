package com.seven.contract.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.dao.ContractDao;
import com.seven.contract.manage.dao.ContractJoinDao;
import com.seven.contract.manage.enums.ContractJoinRoleEnum;
import com.seven.contract.manage.enums.ContractJoinStatusEnum;
import com.seven.contract.manage.enums.ContractStatusEnum;
import com.seven.contract.manage.enums.MessageTypeEnum;
import com.seven.contract.manage.model.Contract;
import com.seven.contract.manage.model.ContractJoin;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.model.Message;
import com.seven.contract.manage.service.ContractService;
import com.seven.contract.manage.service.MemberService;
import com.seven.contract.manage.service.MessageService;
import com.seven.contract.manage.uploader.path.FileSavePath;
import com.seven.contract.manage.utils.BytomUtil;
import com.seven.contract.manage.utils.Hash;
import com.seven.contract.manage.utils.SerialNumber;
import com.seven.contract.manage.vo.ContractVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContractServiceImpl implements ContractService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ContractDao contractDao;

	@Autowired
	private ContractJoinDao contractJoinDao;

	@Autowired
	private MemberService memberService;

	@Autowired
	private FileSavePath fileSavePath;

	@Autowired
	private MessageService messageService;

	@Override
	public int addContract(int mid, String contractName, Date endTime, String remark, String secretContract, String contractUrl, ContractStatusEnum status, List<Integer> contactMids) throws Exception {

		Contract contract = new Contract();
		contract.setContractName(contractName);
		contract.setContractNo(SerialNumber.createSerial("CON", 6));
		contract.setStartTime(new Date());
		contract.setEndTime(endTime);
		contract.setStatus(status.toString());
		contract.setSecretContract(secretContract);
		contract.setContractUrl(contractUrl);
		contract.setAddTime(new Date());

		contractDao.insert(contract);

		logger.debug("add contract = {}", JSON.toJSONString(contract));

		int sort = 1;

		Member member = memberService.selectOneById(mid);
		ContractJoin contractJoin = new ContractJoin();
		contractJoin.setContractId(contract.getId());
		contractJoin.setMid(mid);
		contractJoin.setName(member.getName());
		contractJoin.setPhone(member.getPhone());
		if (status == ContractStatusEnum.signing) {
			contractJoin.setSignTime(new Date());
			contractJoin.setStatus(ContractJoinStatusEnum.waitother.toString());
		} else if (status == ContractStatusEnum.draft) {
			contractJoin.setStatus(ContractJoinStatusEnum.draft.toString());
		} else {
			logger.error("添加合同信息状态不正确, status = {}", status.toString());
			throw new AppRuntimeException("9999", "合同状态不正确");
		}
		contractJoin.setRole(ContractJoinRoleEnum.initiator.toString());
		contractJoin.setSort(sort);
		contractJoinDao.insert(contractJoin);

		sort++;

		for (Integer contactMid : contactMids) {
			if (contactMid != null && contactMid != 0) {
				member = memberService.selectOneById(contactMid);
				if (member == null) {
					logger.debug("用户信息不存在, contactMid = {}", contactMid);
					throw new AppRuntimeException("9999", "用户信息不存在");
				}
				contractJoin = new ContractJoin();
				contractJoin.setContractId(contract.getId());
				contractJoin.setMid(contactMid);
				contractJoin.setName(member.getName());
				contractJoin.setPhone(member.getPhone());
				if (status == ContractStatusEnum.signing) {
					if (sort == 2) {
						contractJoin.setStatus(ContractJoinStatusEnum.waitmine.toString());
					} else {
						contractJoin.setStatus(ContractJoinStatusEnum.waitother.toString());
					}
				} else if (status == ContractStatusEnum.draft) {
					contractJoin.setStatus(ContractJoinStatusEnum.draft.toString());
				}
				contractJoin.setRole(ContractJoinRoleEnum.join.toString());
				contractJoin.setSort(sort);
				contractJoinDao.insert(contractJoin);

				sort++;
			}
		}

		return contract.getId();
	}

	@Override
	public long updateContract(long id, int mid, String contractUrl) throws AppRuntimeException {

		Contract contract = contractDao.selectOne(id);

		logger.debug("contract = {}", JSON.toJSONString(contract));

		if (contract == null) {
			throw new AppRuntimeException("9999", "合同信息不存在");
		}

		if (!contract.getStatus().equals(ContractStatusEnum.draft.toString())) {
			throw new AppRuntimeException("9999", "合同状态不正确");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contract.getId());
		params.put("mid", mid);
		params.put("status", ContractJoinStatusEnum.draft.toString());
		params.put("role", ContractJoinRoleEnum.initiator.toString());
		List<ContractJoin> contractJoins = contractJoinDao.selectList(params);

		if (contractJoins == null || contractJoins.size() < 1) {
			throw new AppRuntimeException("9999", "非法操作");
		}

		contract.setContractUrl(contractUrl);
		contract.setStartTime(new Date());
		contractDao.update(contract);

		return id;
	}

	@Override
	public void deleteById(long id) {

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", id);
		List<ContractJoin> contractJoins = contractJoinDao.selectList(params);
		for (ContractJoin contractJoin : contractJoins) {
			contractJoinDao.deleteById(contractJoin.getId());
		}

		contractDao.deleteById(id);
	}

	@Override
	public Contract selectOneById(long id) {
		Contract contract = contractDao.selectOne(id);
		return contract;
	}

	@Override
	public PageResult<Contract> getListByPage(Map<String, Object> params, int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);

		List<Contract> dataList = contractDao.selectList(params);
		Page page=(Page) dataList;
		PageResult pageResult=new PageResult();
		pageResult.setRecords(page.getTotal());
		pageResult.setRows(dataList);
		pageResult.setPage(page.getPageNum());
		pageResult.setTotal(page.getPages());
		return pageResult;

	}

	@Override
	public void save(Contract contract) {
		contractDao.insert(contract);
	}

	@Override
	public List<Contract> selectList(Map<String, Object> params) {
		return contractDao.selectList(params);
	}

	@Override
	public List<ContractVo> selectListByManage(Map<String, Object> params) {
		return contractDao.selectListByManage(params);
	}

	@Override
	public void draftToSigning(long id, int mid, String contractUrl) throws AppRuntimeException {

		Contract contract = contractDao.selectOne(id);

		logger.debug("contract = {}", JSON.toJSONString(contract));

		if (contract == null) {
			throw new AppRuntimeException("9999", "合同信息不存在");
		}

		if (!contract.getStatus().equals(ContractStatusEnum.draft.toString())) {
			throw new AppRuntimeException("9999", "合同状态不正确");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contract.getId());
		params.put("mid", mid);
		params.put("status", ContractJoinStatusEnum.draft.toString());
		params.put("role", ContractJoinRoleEnum.initiator.toString());
		List<ContractJoin> contractJoins = contractJoinDao.selectList(params);

		if (contractJoins == null || contractJoins.size() < 1) {
			throw new AppRuntimeException("9999", "非法操作");
		}

		contract.setContractUrl(contractUrl);
		contract.setStatus(ContractStatusEnum.signing.toString());
		contract.setStartTime(new Date());
		contractDao.update(contract);

		params = new HashMap<>();
		params.put("contractId", contract.getId());
		contractJoins = contractJoinDao.selectList(params);
		for (ContractJoin contractJoin : contractJoins) {
			if (contractJoin.getRole().equals(ContractJoinRoleEnum.initiator.toString())) {
				contractJoin.setSignTime(new Date());
			}
			if (contractJoin.getSort() == 2) {
				contractJoin.setStatus(ContractJoinStatusEnum.waitmine.toString());
			} else {
				contractJoin.setStatus(ContractJoinStatusEnum.waitother.toString());
			}
			contractJoinDao.update(contractJoin);
		}
	}

	@Override
	public Map<String, Object> signing(long id, int mid, String signFlag, String contractUrl) throws Exception {

		logger.debug("id = {}, mid = {}, signFlag = {}, contractUrl = {}", id, mid, signFlag, contractUrl);

		Contract contract = contractDao.selectOne(id);

		if (contract == null) {
			throw new AppRuntimeException("9999", "合同信息不存在");
		}

		if (!contract.getStatus().equals(ContractStatusEnum.signing.toString())) {
			throw new AppRuntimeException("9999", "合同状态不正确");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contract.getId());
		List<ContractJoin> contractJoins = contractJoinDao.selectList(params);

		if (contractJoins == null || contractJoins.size() < 1) {
			throw new AppRuntimeException("9999", "合同信息不正确");
		}

		Map<String, Object> result = new HashMap<>();

		boolean isFlag = false;
		if (signFlag.equals("Y")) {
			int sort = 0;
			boolean isComplete = false;  //合同是否已完成
			//同意
			for (ContractJoin contractJoin : contractJoins) {

				if (contractJoin.getMid() == mid) {
					if (!contractJoin.getStatus().equals(ContractJoinStatusEnum.waitmine.toString())) {
						throw new AppRuntimeException("9999", "非法操作");
					}

					isFlag = true;

					logger.debug("contractJoin.getSort() = {}, contractJoins.size() = {}", contractJoin.getSort(), contractJoins.size());
					if (contractJoin.getSort() == contractJoins.size()) {
						//最后一个签约,修改成完成
						contractJoin.setStatus(ContractJoinStatusEnum.complete.toString());
						contractJoin.setSignTime(new Date());
						contractJoinDao.update(contractJoin);
						isComplete = true;
						logger.debug("agree mid contractJoin = {}", JSON.toJSONString(contractJoin));
					} else if (contractJoin.getSort() < contractJoins.size()){
						//不是最后一个,将当前签约修改成待其它人签,将下个人状态修改成待我签
						contractJoin.setStatus(ContractJoinStatusEnum.waitother.toString());
						contractJoin.setSignTime(new Date());
						logger.debug("agree mid contractJoin = {}", JSON.toJSONString(contractJoin));
						contractJoinDao.update(contractJoin);

						sort = contractJoin.getSort() + 1; //处理下一个
					} else {
						throw new AppRuntimeException("9999", "合同参于人数不正确");
					}

					break;
				}
			}

			if (isFlag) {
				logger.debug("isComplete = {}", isComplete);
				if (isComplete) {  //合同完成,将所有参于者状态都改成完成,并计算文件Hash

					//计算文件hash值
					String hase;

					//拿到的地址是带http的访问地址,必须转化成本地地址
					String fullPath = fileSavePath.relativePath2fullPath(contractUrl);

					File file = new File(fullPath);
					byte[] hashByte = Hash.sha256(file);

					// 将 byte 转化为 string
					String fileHash = Hash.toString(hashByte);

					logger.debug("fileHash = {}", fileHash);

					for (ContractJoin contractJoin : contractJoins) {
						contractJoin.setStatus(ContractJoinStatusEnum.complete.toString());
						contractJoin.setFileHash(fileHash);
						contractJoinDao.update(contractJoin);
					}

					//将合同状态改成注册区块链
					contract.setStatus(ContractStatusEnum.registered.toString());
					contract.setContractUrl(contractUrl);
					logger.debug("signing contract = {}", JSON.toJSONString(contract));
					contractDao.update(contract);

					result.put("status", ContractStatusEnum.registered.toString());
					result.put("hash", fileHash);

					logger.debug("signing result = {}", JSON.toJSONString(result));
					return result;

				} else {  //将下一个参于者修改成待我签

					for (ContractJoin contractJoin : contractJoins) {

						if (contractJoin.getSort() == sort) {
							contractJoin.setStatus(ContractJoinStatusEnum.waitmine.toString());
							contractJoinDao.update(contractJoin);
							logger.debug("change contractJoin to waitmine = {}", JSON.toJSONString(contractJoin));
							break;
						}

					}

					result.put("status", ContractStatusEnum.signing.toString());
					result.put("hash", "");

					logger.debug("signing result = {}", JSON.toJSONString(result));
					return result;
				}
			} else {
				throw new AppRuntimeException("9999", "合同信息有误");
			}

		} else {
			//拒签,将签署者状态修改成拒签,其它参于者修改成被拒签
			for (ContractJoin contractJoin : contractJoins) {
				if (contractJoin.getMid() == mid) {
					if (!contractJoin.getStatus().equals(ContractJoinStatusEnum.waitmine.toString())) {
						throw new AppRuntimeException("9999", "非法操作");
					}

					isFlag = true;

					contractJoin.setStatus(ContractJoinStatusEnum.refuse.toString());
					contractJoin.setSignTime(new Date());
					logger.debug("refuse mid contractJoin = {}", JSON.toJSONString(contractJoin));
					contractJoinDao.update(contractJoin);
					break;
				}
			}

			logger.debug("contractJoins = {}", JSON.toJSONString(contractJoins));

			if (isFlag) {
				//将其它参于者状态修改成被拒签
				for (ContractJoin contractJoin : contractJoins) {
					if (contractJoin.getStatus().equals(ContractJoinStatusEnum.waitother.toString())) {
						logger.debug("refuse other mid contractJoin = {}", JSON.toJSONString(contractJoin));
						contractJoin.setStatus(ContractJoinStatusEnum.refuseother.toString());
						contractJoinDao.update(contractJoin);

						//给发起者发送一条拒签消息
						if (contractJoin.getRole().equals(ContractJoinRoleEnum.initiator.toString())) {
							Message message = new Message();
							message.setMid(contractJoin.getMid());
							message.setType(MessageTypeEnum.contractRefuse.toString());
							message.setRemark(contract.getContractNo());
							message.setContent("您有一份合同被拒签");
							messageService.addMessage(message);
						}
					}
				}

				//将合同状态改成失败
				contract.setStatus(ContractStatusEnum.fail.toString());
				contract.setCompleteTime(new Date());
				if (!StringUtils.isEmpty(contractUrl)) {
					contract.setContractUrl(contractUrl);
				}
				logger.debug("refuse contract = {}", JSON.toJSONString(contract));
				contractDao.update(contract);

				result.put("status", ContractStatusEnum.fail.toString());
				result.put("hash", "");

				logger.debug("signing result = {}", JSON.toJSONString(result));
				return result;
			} else {
				throw new AppRuntimeException("9999", "合同信息有误");
			}
		}

	}

	@Override
	public boolean saveSignature(int mid, long id, String signature) throws Exception {

		logger.debug("mid = {}, id = {}, signature = {}", mid, id, signature);

		//判断合同状态是否是registered
		Contract contract = contractDao.selectOne(id);
		if (contract == null) {
			throw new AppRuntimeException("9999", "合同信息有误");
		}

		if (!contract.getStatus().equals(ContractStatusEnum.registered.toString())) {
			throw new AppRuntimeException("9999", "合同状态不正确");
		}

		//判断用户是否参于了这个合同的签署
		Map<String, Object> params = new HashMap<>();
		params.put("contractId", contract.getId());
		List<ContractJoin> contractJoins = contractJoinDao.selectList(params);
		if (contractJoins == null || contractJoins.size() < 1) {
			throw new AppRuntimeException("9999", "合同参于者信息错误");
		}

		boolean isMine = false;  //判断这个合同是不是参于
		String fileHash = "";
		List<String> signatures = new ArrayList<>();
		for (ContractJoin contractJoin : contractJoins) {
			logger.debug("contractJoin = {}", JSON.toJSONString(contractJoin));
			if (contractJoin.getMid() == mid) {  //保存签名
				contractJoin.setPrivateKeys(signature);
				contractJoinDao.update(contractJoin);
				isMine = true;
				fileHash = contractJoin.getFileHash();
			}

			if (!StringUtils.isEmpty(contractJoin.getPrivateKeys())) {	//记录已生成所有signature
				signatures.add(contractJoin.getPrivateKeys());
			}

		}

		if (isMine) {
			if (contractJoins.size() == signatures.size()) {  //所有用户都生成signatures
				logger.debug("进行区块链交易 signatures = {}", JSON.toJSONString(signatures));
				signatures.add(fileHash);  //添加文件hash

				String program = BytomUtil.compile(signatures);
				String txId = BytomUtil.locked(program);

				logger.debug("program = {}, txId = {}", program, txId);

				//记录program和txId
				contract.setProgram(program);
				contract.setTxId(txId);
				contractDao.update(contract);

				logger.debug("return true contract = {}", JSON.toJSONString(contract));

				return true;
			}
		} else {
			throw new AppRuntimeException("9999", "非法操作");
		}

		return false;
	}

	@Override
	public void unlock(long id) throws Exception {
		logger.debug("unlock id = {}", id);

		Contract contract = contractDao.selectOne(id);
		if (contract == null) {
			throw new AppRuntimeException("9999", "合同信息不存在");
		}

		String outputId = BytomUtil.getOutputId(contract.getTxId(), contract.getProgram());

		logger.debug("outputId = {}", outputId);

		if (!StringUtils.isEmpty(outputId)) {
			List<String> publicKeys = new ArrayList<>();

			Map<String, Object> params = new HashMap<>();
			params.put("contractId", contract.getId());
			List<ContractJoin> contractJoins = contractJoinDao.selectList(params);
			for (ContractJoin contractJoin : contractJoins) {
				logger.debug("contractJoin = {}", JSON.toJSONString(contractJoin));
				Member member = memberService.selectOneById(contractJoin.getMid());

				if (member == null) {
					logger.debug("用户信息不存在! mid = {}", contractJoin.getMid());
					throw new AppRuntimeException("9999", "用户不存在");
				}

				publicKeys.add(member.getPublicKeys());
			}

			String txs = BytomUtil.unlock(publicKeys, outputId);
			logger.debug("txs = {}", txs);

			contract.setStatus(ContractStatusEnum.complete.toString());
			contract.setCompleteTime(new Date());
			contract.setContractEndBlockUrl(txs);
			contractDao.update(contract);

			logger.debug("contract = {}", JSON.toJSONString(contract));
		}

	}
}
