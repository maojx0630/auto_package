package com.github.maojx0630.automatic.def;

import com.github.maojx0630.automatic.config.ConfigInterface;
import com.github.maojx0630.automatic.model.GitlabConfig;
import com.github.maojx0630.automatic.model.ProjectInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author 毛家兴
 * @date 2021-03-25 08:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultConfigInterface implements ConfigInterface {

  private List<ProjectInfo> projectInfoList;

  private Map<String, GitlabConfig> gitlabInfoMap;
}
