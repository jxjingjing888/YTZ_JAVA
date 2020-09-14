package com.ytz;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Scanner;


/**
 * @author Bob
 */
//开启事务管理
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.ytz"})
@MapperScan("com.ytz.dao")
public class YtzApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtzApplication.class, args);
    }

}
