package com.seven.contract.manage.dao;

import com.seven.contract.manage.common.BaseDao;
import com.seven.contract.manage.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDao extends BaseDao<Member> {

    public Member selectOneByPhone(@Param("phone") String phone);

    public Member selectOneByPublicKeys(@Param("publicKeys") String publicKeys);

}

