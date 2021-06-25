package com.github.maojx0630.automatic.model;

import lombok.Data;

import java.util.UUID;

/**
 * @author 毛家兴
 * @date 2021-03-11 14:32
 */
@Data
public class ProjectInfo {
  /** id */
  private String id = UUID.randomUUID().toString();
  /** 项目名称 */
  private String projectName;
  /** gitlab项目id */
  private Integer gitlabId;
  /** gitlab项目名称 */
  private String gitlabName;
  /** 使用哪个gitlab配置(gitlab map的key) */
  private String gitlabConfig;
  /** jenkins项目名称 */
  private String jenkinsName;
  /** 触发打包的关键词 */
  private String key;
}
