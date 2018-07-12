package com.rbkmoney.provider.samsungpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Created by vpankrashkin on 04.07.18.
 */
@SpringBootApplication(scanBasePackages = "com.rbkmoney.provider.samsungpay")
@ServletComponentScan
public class SamsungPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamsungPayApplication.class, args);
    }
}
