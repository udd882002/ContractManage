package com.seven.contract.manage.utils.ca;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.util.encoders.Hex;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.web3j.crypto.CipherException;
import org.web3j.utils.Numeric;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class keyStore {

    static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        CASecureRandomUtils.secureRandom().nextBytes(bytes);
        return bytes;
    }

    public static byte[] generateDerivedScryptKey(byte[] password, byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }
    // n:262144 p:1  length：privateKey.toByteArray().length
    public static CaWalletFileData createKeyStore(String password, BigInteger privateKey, int n, int p, int length) throws CipherException {
        byte[] salt = generateRandomBytes(32);
        byte[] derivedKey = generateDerivedScryptKey(password.getBytes(Charset.forName("UTF-8")), salt, n, 8, p, 32);
        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] iv = generateRandomBytes(16);

        byte[] privateKeyBytes = Numeric.toBytesPadded(privateKey, length);
        byte[] cipherText = performCipherOperation(1, iv, encryptKey, privateKeyBytes);
        byte[] mac = generateMac(derivedKey, cipherText);
        return createWalletFile(privateKey, cipherText, iv, salt, mac, n, p, length);
    }

    private static CaWalletFileData createWalletFile(
            BigInteger privateKey, byte[] cipherText, byte[] iv, byte[] salt, byte[] mac,
            int n, int p, int length) {

        CaWalletFileData walletFile = new CaWalletFileData();

        //id
        walletFile.setId(UUID.randomUUID().toString());
        walletFile.setVersion(1);
        CaWalletFileData.Crypto crypto = new CaWalletFileData.Crypto();
        crypto.setCipher("aes-128-ctr");
        crypto.setCiphertext(Numeric.toHexStringNoPrefix(cipherText));
        walletFile.setCrypto(crypto);

        CaWalletFileData.CipherParams cipherParams = new CaWalletFileData.CipherParams();
        cipherParams.setIv(Numeric.toHexStringNoPrefix(iv));
        crypto.setCipherparams(cipherParams);

        crypto.setKdf("scrypt");
        CaWalletFileData.ScryptKdfParams kdfParams = new CaWalletFileData.ScryptKdfParams();
        kdfParams.setDklen(32);
        kdfParams.setN(n);
        kdfParams.setP(p);
        kdfParams.setR(8);
        kdfParams.setSalt(Numeric.toHexStringNoPrefix(salt));
        crypto.setKdfparams(kdfParams);

        crypto.setMac(Numeric.toHexStringNoPrefix(mac));
        walletFile.setCrypto(crypto);

        return walletFile;
    }


    private static byte[] performCipherOperation(int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (NoSuchPaddingException e) {
            return throwCipherException(e);
        } catch (NoSuchAlgorithmException e) {
            return throwCipherException(e);
        } catch (InvalidAlgorithmParameterException e) {
            return throwCipherException(e);
        } catch (InvalidKeyException e) {
            return throwCipherException(e);
        } catch (BadPaddingException e) {
            return throwCipherException(e);
        } catch (IllegalBlockSizeException e) {
            return throwCipherException(e);
        }
    }

    private static byte[] throwCipherException(Exception e) throws CipherException {
        throw new CipherException("Error performing cipher operation", e);
    }

    public static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];
        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);
        return sha3256(result);
    }

    // SHA3-256 算法
    public static byte[] sha3256(byte[] bytes) {
        Digest digest = new SHA3Digest(256);
        digest.update(bytes, 0, bytes.length);
        byte[] rsData = new byte[digest.getDigestSize()];
        digest.doFinal(rsData, 0);
        return rsData;
    }

    /**
     * 基于 ETH的 解析keystore过程 其中Mac的校验 是基于Sha3-256加密法则
     *
     * @param password
     * @param walletFile
     * @return
     * @throws CipherException
     */
    public static byte[] decrypt(String password, CaWalletFileData walletFile)
            throws CipherException {
        validate(walletFile);
        CaWalletFileData.Crypto crypto = walletFile.getCrypto();
        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());
        byte[] derivedKey;

        CaWalletFileData.KdfParams kdfParams = crypto.getKdfparams();
        if (kdfParams instanceof CaWalletFileData.ScryptKdfParams) {
            CaWalletFileData.ScryptKdfParams scryptKdfParams =
                    (CaWalletFileData.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
            derivedKey = generateDerivedScryptKey(
                    password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
        } else if (kdfParams instanceof CaWalletFileData.Aes128CtrKdfParams) {
            CaWalletFileData.Aes128CtrKdfParams aes128CtrKdfParams =
                    (CaWalletFileData.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(
                    password.getBytes(Charset.forName("UTF-8")), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }

        byte[] derivedMac = generateMac(derivedKey,cipherText);
        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
        System.out.println("privateKey:" + Hex.toHexString(privateKey));
        return privateKey;
    }



    static void validate(CaWalletFileData walletFile) throws CipherException {
        CaWalletFileData.Crypto crypto = walletFile.getCrypto();

        if (walletFile.getVersion() != 1) {
            throw new CipherException("Wallet version is not supported");
        }

        if (!crypto.getCipher().equals("aes-128-ctr")) {
            throw new CipherException("Wallet cipher is not supported");
        }

        if (!crypto.getKdf().equals("pbkdf2") && !crypto.getKdf().equals("scrypt")) {
            throw new CipherException("KDF type is not supported");
        }
    }

    public static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        // Java 8 supports this, but you have to convert the password to a character array, see
        // http://stackoverflow.com/a/27928435/3211687

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }


}
