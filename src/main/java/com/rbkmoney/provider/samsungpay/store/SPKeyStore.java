package com.rbkmoney.provider.samsungpay.store;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Created by vpankrashkin on 09.04.18.
 */
public class SPKeyStore {
    private final Path serviceKeyDir;

    public SPKeyStore(String serviceKeyDir) {
           this.serviceKeyDir = Paths.get(serviceKeyDir);
    }

    public PKCS8EncodedKeySpec getKey(String serviceId) {
        return getKey(serviceKeyDir, serviceId, ".pem");
    }

    private PKCS8EncodedKeySpec getKey(Path baseDir, String serviceId, String suffix) {
        Path certPath = baseDir.resolve(buildCertFileName(serviceId, suffix));
        try {
            if (!Files.isRegularFile(certPath)) {
                certPath = baseDir.resolve(buildCertFileName(null, suffix));
                if (!Files.isRegularFile(certPath)) {
                    return null;
                }
            }
            return getPrivateKey(new String(Files.readAllBytes(certPath)));
        } catch (Exception e) {
            throw new CertStoreException(e);
        }
    }

    public static PKCS8EncodedKeySpec getPrivateKey(String pemKey) throws GeneralSecurityException {
        pemKey = pemKey.replace("-----BEGIN PRIVATE KEY-----\n", "");
        pemKey = pemKey.replace("-----END PRIVATE KEY-----", "");
        pemKey = pemKey.replace("\n", "");
        byte[] encoded = Base64.getDecoder().decode(pemKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keySpec;
    }

    private String buildCertFileName(String serviceId, String suffix) {
        return (serviceId != null ? serviceId : "")     + suffix;
    }

}
