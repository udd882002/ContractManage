package com.seven.contract.manage.utils.ca;

import zjca.ws.scxxkj.biz.CertRequest;
import zjca.ws.scxxkj.util.Base64;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignUtil {
    public static String sign(CertRequest req) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidKeyException, SignatureException {
        req.setEncoding(System.getProperty("file.encoding"));
        String rawtxt = req.toXml().trim();
        rawtxt = new String(Base64.encode(rawtxt.getBytes()));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        KeySpec priKeySpec = new PKCS8EncodedKeySpec(
                Base64.decode(Base.priKey));
        PrivateKey priKey = kf.generatePrivate(priKeySpec);
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initSign(priKey);
        sign.update(rawtxt.getBytes());
        byte[] sig = sign.sign();
        return new String(Base64.encode(sig));
    }


    public static boolean verify(String rawtxt, String b64sign)
            throws NoSuchAlgorithmException, InvalidKeySpecException,
            SignatureException, InvalidKeyException {
        KeyFactory kf = KeyFactory.getInstance("RSA");

        KeySpec pubKeySpec = new X509EncodedKeySpec(
                Base64.decode(Base.pubKey));
        PublicKey pubKey = kf.generatePublic(pubKeySpec);
        Signature sign = Signature.getInstance("SHA1withRSA");
        sign.initVerify(pubKey);
        sign.update(rawtxt.getBytes());
        return sign.verify(Base64.decode(b64sign));

    }
}
