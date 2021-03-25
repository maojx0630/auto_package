package com.github.maojx0630.automatic.config;

import com.github.maojx0630.automatic.model.GitlabConfig;
import com.github.maojx0630.automatic.model.ProjectInfo;

import java.util.List;
import java.util.Map;

/**
 * 获取gitlab配置与项目信息
 *
 * @author 毛家兴
 * @date 2021-03-25 08:53
 */
public interface ConfigInterface {

  List<ProjectInfo> getProjectInfoList();

  Map<String, GitlabConfig> getGitlabInfoMap();
}
