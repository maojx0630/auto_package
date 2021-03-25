package com.github.maojx0630.automatic.model;

import lombok.Data;

import java.util.List;
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
  /** 通知手机号 为空通知所有人若不想通知任何人 请填入11111111111 */
  private List<String> mobiles;

  // 0通知所有人 1不通知任何人 2通知列表中手机号
  public int sendAll() {
    if (mobiles == null || mobiles.isEmpty()) {
      return 0;
    }
    if (mobiles.contains("11111111111")) {
      return 1;
    }
    return 2;
  }
}
