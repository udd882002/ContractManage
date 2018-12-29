package com.seven.contract.manage.service;

import com.seven.contract.manage.common.PageResult;
import com.seven.contract.manage.model.Member;

import java.util.Map;

public interface MemberService {

	public void addMember(Member member);

	public PageResult<Member> getListByPage(Map<String, Object> params, int pageNum, int pageSize);

	public void deleteById(long id);

	public void updateMember(Member member);

	public Member selectOneById(long id);

	public Member selectOneByPhone(String phone);

	public Member selectOneByPublicKeys(String publicKeys);
}
