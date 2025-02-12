package com.swang.acmojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.swang.acmojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.swang")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.swang.acmojbackendserviceclient.service"})
public class AcmojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcmojBackendQuestionServiceApplication.class, args);
    }

}
