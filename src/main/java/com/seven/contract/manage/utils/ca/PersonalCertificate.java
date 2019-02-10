package com.seven.contract.manage.utils.ca;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.conf.CaProperties;
import com.seven.contract.manage.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zjca.ws.scxxkj.ZjcaCertificateBuilder;
import zjca.ws.scxxkj.biz.CertRequest;
import zjca.ws.scxxkj.biz.CertResponse;
import zjca.ws.scxxkj.biz.Config;
import zjca.ws.scxxkj.biz.ICertBuilder;

public class PersonalCertificate {

    private static Logger logger = LoggerFactory.getLogger(PersonalCertificate.class);

        private static Config config = new Config();
        static {
                config.setUrl(CaProperties.url);
                config.setEncKey(CaProperties.pwd);
        }

    public static String personal(String SM2Req, Member member) throws Exception {
            ICertBuilder builderPerson = ZjcaCertificateBuilder.getUserCertBuilder(config, 3);
            CertRequest reqPerson = new CertRequest();
            reqPerson.setReq(SM2Req);
            reqPerson.setName(member.getName());  //姓名（必填）
            reqPerson.setLinkIdCode(member.getIdCard());  //身份证（必填）
            reqPerson.setLinkMobile(member.getPhone());  //手机号码（手机电话两者选一必填）
            reqPerson.setEnc(true);  //参数是否加密
            reqPerson.setCertPolicy(CaProperties.personalCertificate);  //证书策略（必填）
            reqPerson.setFeeScale(CaProperties.personalCharge);  //收费策略（必填）
            String appCode = "20190121_scxxkj";  //应用编号（必填）

            String signature = SignUtil.sign(reqPerson);

            CertResponse res = builderPerson.sign(reqPerson, signature, appCode);

            logger.debug("res = {}", JSON.toJSONString(res));

            if (!res.isSuccess()) {
                    throw new AppRuntimeException("认证失败");
            }

            /*  注销流程,暂时用不到
            ICertBuilder builderDes = ZjcaCertificateBuilder.getUserCertBuilder(config, 3);
            CertRequest reqDes = new CertRequest();
            reqDes.setCertId(res.getCertId());  //证书ID（必填）
            reqDes.setEnc(true);  //参数是否加密
            String appCodeDes = "20190121_scxxkj";  //应用编号（必填）

            String signatureDes = SignUtil.sign(reqDes);

            boolean resDes = builderDes.revoke(reqDes, signatureDes, appCodeDes);
            */

            return JSON.toJSONString(res);
    }
}
