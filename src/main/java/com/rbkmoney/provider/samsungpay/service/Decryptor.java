package com.rbkmoney.provider.samsungpay.service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Created by vpankrashkin on 05.07.18.
 */
public class Decryptor {
    public static String getDecryptedData(String encPayload, PKCS8EncodedKeySpec prvKeySpec) throws Exception {
        String delims = "[.]";
        String[] tokens = encPayload.split(delims);
        Base64.Decoder urlDecoder = Base64.getUrlDecoder();
        byte[] encKey = urlDecoder.decode(tokens[1]);
        byte[] iv = urlDecoder.decode(tokens[2]);
        byte[] cipherText = urlDecoder.decode(tokens[3]);
        byte[] tag = urlDecoder.decode(tokens[4]);
        byte[] plainText = new byte[cipherText.length];

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privKey = keyFactory.generatePrivate(prvKeySpec);
        Cipher decryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] plainEncKey = decryptCipher.doFinal(encKey);
        final Cipher aes128Cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * Byte.SIZE, iv);
        final SecretKeySpec keySpec = new SecretKeySpec(plainEncKey, "AES");
        aes128Cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
        int offset = aes128Cipher.update(cipherText, 0, cipherText.length, plainText, 0);
        aes128Cipher.update(tag, 0, tag.length, plainText, offset);
        aes128Cipher.doFinal(plainText, offset);
        return new String(plainText);
    }
}
