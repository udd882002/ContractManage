package com.seven.contract.manage.utils.ca;

import com.alibaba.fastjson.JSON;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.enums.MemberTypeEnum;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.utils.ca.cert.CommonUtil;
import com.seven.contract.manage.utils.ca.cert.SM2PublicKey;
import com.seven.contract.manage.utils.ca.cert.SM2X509CertMaker;
import com.seven.contract.manage.utils.ca.sm.BCECUtil;
import com.seven.contract.manage.utils.ca.sm.SM2Util;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2019/1/29.
 */
public class CaUtil {

    private static Logger logger = LoggerFactory.getLogger(CaUtil.class);

//    public static void main(String[] args) {
//        try {
//
//            // 生成公私钥
//            KeyPair subKP = SM2Util.generateKeyPair();
//            BCECPrivateKey a = (BCECPrivateKey) subKP.getPrivate();
//            BCECPublicKey b = (BCECPublicKey) subKP.getPublic();
//            System.out.println(a.getD().toString());
//            System.out.println("private key:");
//            printHexString(a.getD().toByteArray());
//            System.out.println("public key:");
//            printHexString(b.getQ().getEncoded(false));
//
//            // 公私钥加密、以及解密公私钥、签名
//            System.out.println("================================");
//            creatAccount(a.getD().toString());
//
//            // ca认证
//            /// 0、根据公私钥生成认证需要的csr数据
//            X500Name subDN = buildSubjectDN();
//            SM2PublicKey sm2SubPub = new SM2PublicKey(subKP.getPublic().getAlgorithm(), (BCECPublicKey) subKP.getPublic());
//            byte[] csr = CommonUtil.createCSR(subDN, sm2SubPub, subKP.getPrivate(), SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2).getEncoded();
//            String strCsr = Base64.toBase64String(csr);
//            /// 1、个人认证
//            PersonalCertificate.personal(strCsr, null);
//            /// 2、企业认证
//            EnterpriseCertification.enterprise(strCsr, null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static String printHexString(byte[] b) {
        String key = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            key += hex.toUpperCase();
        }

        return key;
    }


    public static X500Name buildSubjectDN() {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        return builder.build();
    }

    public static void main(String[] args) {
        try {
            // 生成公私钥
            KeyPair subKP = SM2Util.generateKeyPair();

            // ca认证
            /// 0、根据公私钥生成认证需要的csr数据
            X500Name subDN = buildSubjectDN();
            SM2PublicKey sm2SubPub = new SM2PublicKey(subKP.getPublic().getAlgorithm(), (BCECPublicKey) subKP.getPublic());
            byte[] csr = CommonUtil.createCSR(subDN, sm2SubPub, subKP.getPrivate(), SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2).getEncoded();
            String strCsr = Base64.toBase64String(csr);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> createKeys(String pwd, Member member) throws Exception {

        logger.debug("createKeys start");

        Map<String, String> keyMap = new HashMap<>();

        // 生成公私钥
        KeyPair subKP = SM2Util.generateKeyPair();
        // ca认证
        /// 0、根据公私钥生成认证需要的csr数据
        X500Name subDN = buildSubjectDN();
        SM2PublicKey sm2SubPub = new SM2PublicKey(subKP.getPublic().getAlgorithm(), (BCECPublicKey) subKP.getPublic());
        byte[] csr = CommonUtil.createCSR(subDN, sm2SubPub, subKP.getPrivate(), SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2).getEncoded();
        String strCsr = Base64.toBase64String(csr);

        String caCert;

        switch (MemberTypeEnum.valueOf(member.getType()))
        {
            case personal:
                /// 1、个人认证
                logger.debug("personal start");
                caCert = PersonalCertificate.personal(strCsr, member);
                break;
            case company:
                /// 2、企业认证
                logger.debug("enterprise start");
                caCert = EnterpriseCertification.enterprise(strCsr, member);
                break;
            default:
                throw new AppRuntimeException("用户类型不正确");
        }

        logger.debug("caCert = {}", caCert);
        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) subKP.getPrivate();
        BCECPublicKey bcecPublicKey = (BCECPublicKey) subKP.getPublic();

        //公钥
        String publicKey = printHexString(bcecPublicKey.getQ().getEncoded(false));
        logger.debug("publicKey = {}", publicKey);

        // 私钥加密
        BigInteger id = new BigInteger(bcecPrivateKey.getD().toByteArray());
        // 加密生成对象，需要转换成json存储(这个地方需要你这边写一下)
        CaWalletFileData file = keyStore.createKeyStore(pwd, id, 262144, 1, id.toByteArray().length);
        String privateKey = JSON.toJSONString(file);
        logger.debug("privateKey = {}", privateKey);

        keyMap.put("caCert", caCert);
        keyMap.put("publicKey", publicKey);
        keyMap.put("privateKey", privateKey);

        return keyMap;
    }

    public static String sign(String privateKey, String pwd, String hash) throws Exception {

        logger.debug("privateKey = {}, pwd = {}, hash = {}", privateKey, pwd, hash);

        CaWalletFileData file = JSON.parseObject(privateKey, CaWalletFileData.class);

        // 获取私钥
        ///  0、读取读对象的中的私钥数据
        byte[] pri = keyStore.decrypt(pwd, file);
        logger.debug("pri = {}", JSON.toJSONString(pri));

        //签名
        /// 1、私钥转换成对象
        ECPrivateKeyParameters keyParameters = BCECUtil.createECPrivateKeyParameters(new BigInteger(pri), SM2Util.DOMAIN_PARAMS);

        /// 2、私钥签名
        byte[] signByte = SM2Util.sign(keyParameters, hash.getBytes());
        String sign = printHexString(signByte);

        logger.debug("sign = {}", sign);

        return sign;
    }

//    public static void creatAccount(String privateKey) throws CipherException /*throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException*/ {
//        try {
//            // 私钥加密
//            BigInteger id = new BigInteger(privateKey);
//            // 加密生成对象，需要转换成json存储(这个地方需要你这边写一下)
//            CaWalletFileData file = keyStore.createKeyStore("123456", id, 262144, 1, id.toByteArray().length);
//
//            // 获取私钥
//            ///  0、读取读对象的中的私钥数据
//            byte[] pri = keyStore.decrypt("123456", file);
//            System.out.println(new BigInteger(pri));
//
//            //签名
//            /// 1、私钥转换成对象
//            ECPrivateKeyParameters aaa = BCECUtil.createECPrivateKeyParameters(new BigInteger(pri), SM2Util.DOMAIN_PARAMS);
//            printHexString(aaa.getD().toByteArray());
//
//            /// 2、私钥签名
//            String data = "hello world";
//            byte[] sign = SM2Util.sign(aaa, data.getBytes());
//            printHexString(sign);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }


}
