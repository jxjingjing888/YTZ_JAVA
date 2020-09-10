package com.ytz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author Bob
 */
//开启事务管理
@EnableTransactionManagement
@SpringBootApplication
public class YtzApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtzApplication.class, args);
    }

}
