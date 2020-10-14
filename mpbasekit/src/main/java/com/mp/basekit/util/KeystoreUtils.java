package com.mp.basekit.util;

import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import com.mp.basekit.core.MPApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * 使用ksyStore加密工具类
 */

public class KeystoreUtils {

    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private static KeystoreUtils mInstance;
    private KeyStore keyStore;

    public static KeystoreUtils getInstance() {
        synchronized (KeystoreUtils.class) {
            if (null == mInstance) {
                mInstance = new KeystoreUtils();
            }
        }
        return mInstance;
    }

    private KeystoreUtils() {
//        initKeyStore();
    }

    private void initKeyStore(String alias) {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createNewKeys(alias);
    }

    private void createNewKeys(String alias) {
        if (!"".equals(alias)) {
            try {
                // Create new key if needed
                if (!keyStore.containsAlias(alias)) {
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 10);
                    KeyPairGeneratorSpec spec = null;
                    spec = new KeyPairGeneratorSpec.Builder(MPApplication.getAppContext())
                            .setAlias(alias)
                            .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
                    generator.initialize(spec);

                    KeyPair keyPair = generator.generateKeyPair();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加密方法
     *
     * @param needEncryptWord 　需要加密的字符串
     * @param alias           　加密秘钥
     * @return
     */
    public String encrypt(String needEncryptWord, String alias) {
        if (!"".equals(alias) && !"".equals(needEncryptWord)) {
            initKeyStore(alias);
            String encryptStr = "";
            byte[] vals = null;
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
                if (needEncryptWord.isEmpty()) {
                    return encryptStr;
                }
                Cipher inCipher = Cipher.getInstance(TRANSFORMATION);
                inCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream = new CipherOutputStream(
                        outputStream, inCipher);
                cipherOutputStream.write(needEncryptWord.getBytes("UTF-8"));
                cipherOutputStream.close();
                vals = outputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Base64.encodeToString(vals, Base64.DEFAULT);
        }
        return "";
    }

    /**
     * 解密方法
     *
     * @param needDecryptWord 　需要解密的字符串
     * @param alias           　解密秘钥
     * @return
     */
    public String decrypt(String needDecryptWord, String alias) {
        if (!"".equals(alias) && !"".equals(needDecryptWord)) {
            initKeyStore(alias);
            String decryptStr = "";
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
                Cipher output = Cipher.getInstance(TRANSFORMATION);
                output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(needDecryptWord, Base64.DEFAULT)), output);
                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }

                byte[] bytes = new byte[values.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = values.get(i);
                }

                decryptStr = new String(bytes, 0, bytes.length, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return decryptStr;
        }
        return "";
    }
}