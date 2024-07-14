package com.manbo.merchant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author manboyu
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class MerchantServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(MerchantServiceApp.class, args);
    }
}
