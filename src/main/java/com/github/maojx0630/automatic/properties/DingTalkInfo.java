package com.github.maojx0630.automatic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 毛家兴
 * @date 2021-03-25 09:08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ding")
public class DingTalkInfo {

  /** 钉钉secret */
  private String secret;

  /** 钉钉token */
  private String token;
}
