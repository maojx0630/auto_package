package com.github.maojx0630.automatic.model;

import lombok.Data;

/**
 * @author 毛家兴
 * @date 2021-03-24 10:30
 */
@Data
public class GitlabConfig {

  /** gitlab map的key */
  private String name;

  /** gitlab的url */
  private String uri;

  /** gitlab的账号 api token */
  private String token;
}
