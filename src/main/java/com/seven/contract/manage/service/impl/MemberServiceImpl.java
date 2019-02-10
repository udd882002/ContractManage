package com.seven.contract.manage.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.dao.MemberDao;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.service.MemberService;
import com.seven.contract.manage.utils.ca.CaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class MemberServiceImpl implements MemberService {

	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MemberDao memberDao;

	@Override
	public void addMember(Member member, String privateKeyPwd) throws Exception {

		//计算公私钥和CA认证
		Map<String, String> caMap = CaUtil.createKeys(privateKeyPwd, member);

		String privateKey = caMap.get("privateKey");
		String publicKey = caMap.get("publicKey");
		String caCert = caMap.get("caCert");

		member.setPrivateKeysFileUrl(privateKey);
		member.setPublicKeys(publicKey);
		member.setCaCert(caCert);
		memberDao.insert(member);
	}

	@Override
	public void updateMember(Member member){
		memberDao.update(member);
	}
	@Override
	public void deleteById(long id) {
		memberDao.deleteById(id);
	}
	@Override
	public Member selectOneById(long id) {
		Member member = memberDao.selectOne(id);
		return member;
	}
	@Override
	public PageResult<Member> getListByPage(Map<String, Object> params, int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);

		List<Member> dataList = memberDao.selectList(params);
		Page page=(Page) dataList;
		PageResult pageResult=new PageResult();
		pageResult.setRecords(page.getTotal());
		pageResult.setRows(dataList);
		pageResult.setPage(page.getPageNum());
		pageResult.setTotal(page.getPages());
		return pageResult;

	}

	@Override
	public Member selectOneByPhone(String phone) {
		return memberDao.selectOneByPhone(phone);
	}

	@Override
	public Member selectOneByPublicKeys(String publicKeys) {
		return memberDao.selectOneByPublicKeys(publicKeys);
	}
}
