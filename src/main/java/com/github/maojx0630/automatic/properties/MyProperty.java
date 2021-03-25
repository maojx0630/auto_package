package com.github.maojx0630.automatic.properties;

import com.github.maojx0630.automatic.model.GitlabConfig;
import com.github.maojx0630.automatic.model.ProjectInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/** 项目配置属性 */
@Data
@Configuration
@ConfigurationProperties(prefix = "customize")
public class MyProperty {

  /** gitlab配置 */
  private Map<String, GitlabConfig> gitlabInfoMap;

  /** 项目信息配置 */
  private List<ProjectInfo> projectInfoList;
}
