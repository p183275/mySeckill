package com.feng.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@MapperScan(basePackages = "com.feng.seckill.mapper")
@SpringBootApplication
public class MySeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySeckillApplication.class, args);
    }

}
