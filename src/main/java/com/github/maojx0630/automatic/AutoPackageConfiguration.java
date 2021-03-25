package com.github.maojx0630.automatic;

import com.github.maojx0630.automatic.config.ConfigInterface;
import com.github.maojx0630.automatic.config.SendMsgInterface;
import com.github.maojx0630.automatic.def.DefaultConfigInterface;
import com.github.maojx0630.automatic.def.DingTalk;
import com.github.maojx0630.automatic.properties.DingTalkInfo;
import com.github.maojx0630.automatic.properties.MyProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 毛家兴
 * @date 2021-03-25 08:58
 */
@Configuration
public class AutoPackageConfiguration {

  @Bean
  @ConditionalOnMissingBean(ConfigInterface.class)
  public ConfigInterface configInterface(MyProperty myProperty) {
    return new DefaultConfigInterface(
        myProperty.getProjectInfoList(), myProperty.getGitlabInfoMap());
  }

  @Bean
  @ConditionalOnMissingBean(SendMsgInterface.class)
  public SendMsgInterface sendMsgInterface(DingTalkInfo dingTalkInfo) {
    return new DingTalk(dingTalkInfo);
  }
}
