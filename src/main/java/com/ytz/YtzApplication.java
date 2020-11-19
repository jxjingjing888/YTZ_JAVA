package com.ytz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Bob
 */
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.ytz"})
@MapperScan("com.ytz.dao")
public class YtzApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtzApplication.class, args);
    }

}
