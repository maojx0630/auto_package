package com.github.maojx0630.automatic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 毛家兴
 * @date 2021-03-25 09:07
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsInfo {

  /** jenkins的url */
  private String url;

  /** jenkins账号 */
  private String username;

  /** jenkins密码 */
  private String password;
}
