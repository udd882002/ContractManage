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

public class EnterpriseCertification {

    private static Logger logger = LoggerFactory.getLogger(EnterpriseCertification.class);

    private static Config config = new Config();
    static {
        config.setUrl(CaProperties.url);
        config.setEncKey(CaProperties.pwd);
    }

    /**
     * 以下为企业证书需求项
     */
    public static String enterprise(String SM2Req, Member member) throws Exception {

        ICertBuilder builder = ZjcaCertificateBuilder.getEntCertBuilder(config, 3);
        CertRequest req = new CertRequest();

        req.setName(member.getName());  //企业名称
        req.setUnitedCode(member.getCreditCode());  //统一社会信用代码
        req.setLinkMan(member.getName());  //联系人（必填）
        req.setLinkIdCode(member.getIdCard());  //身份证（必填）
        // 手机号、电话填写一个就行
        req.setLinkMobile(member.getPhone());  //手机号码
        req.setReq(SM2Req);  //申请码（必填）【测试例子： req.setReq(TestClient.RSA_PKCS10_REQ); 】
        req.setEnc(true);  //是否加密传输数据（ture：加密； false： 不加密）
        req.setCertPolicy(CaProperties.businessCertificate);  //证书策略（必填）
        req.setFeeScale(CaProperties.businessCharge);  //收费策略（必填）
        String appCode = "20190121_scxxkj";  //应用编号（必填）

        String signature = SignUtil.sign(req);  //对参数签名
        CertResponse res = builder.sign(req, signature, appCode);

        logger.debug("res = {}", JSON.toJSONString(res));

        if (!res.isSuccess()) {
            throw new AppRuntimeException("认证失败");
        }

        /* 注销
        ICertBuilder builderDes = ZjcaCertificateBuilder.getEntCertBuilder(config, 3);
        CertRequest reqDes = new CertRequest();
        reqDes.setCertId(res.getCertId());
        reqDes.setEnc(true);
        String appCodeDes = "20190121_scxxkj";  //应用编号（必填）

        String signatureDes = SignUtil.sign(reqDes);

        boolean resDes = builderDes.revoke(reqDes, signatureDes, appCodeDes);
        */
        return JSON.toJSONString(res);
    }
}
