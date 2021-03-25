package com.github.maojx0630.automatic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author MaoJiaXing
 * @date 2019-07-21 18:57
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
