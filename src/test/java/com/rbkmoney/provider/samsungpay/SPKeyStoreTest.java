package com.rbkmoney.provider.samsungpay;

import com.rbkmoney.provider.samsungpay.store.SPKeyStore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertNotNull;

public class SPKeyStoreTest {

    ClassPathResource classPathResource = new ClassPathResource("stg-privkey.pem");

    @Test
    public void testGetKeyByService() throws IOException {
        Path path = Files.createTempFile("key", ".pem");
        try {
            Files.copy(classPathResource.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            SPKeyStore spKeyStore = new SPKeyStore(path.getParent().toString());
            assertNotNull(spKeyStore.getKey(path.getFileName().toString().replace(".pem", "")));
        } finally {
            Files.deleteIfExists(path);
        }
    }

}
