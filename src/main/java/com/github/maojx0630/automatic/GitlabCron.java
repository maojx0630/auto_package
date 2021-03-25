package com.github.maojx0630.automatic;

import com.alibaba.fastjson.JSON;
import com.github.maojx0630.automatic.config.ConfigInterface;
import com.github.maojx0630.automatic.config.SendMsgInterface;
import com.github.maojx0630.automatic.model.GitlabConfig;
import com.github.maojx0630.automatic.model.GitlabInfo;
import com.github.maojx0630.automatic.model.ProjectInfo;
import com.github.maojx0630.automatic.properties.JenkinsInfo;
import com.github.maojx0630.automatic.utils.OkHttpUtils;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 毛家兴
 * @date 2021-03-09 16:17
 */
@Slf4j
@Component
public class GitlabCron {

  private SendMsgInterface sendMsg;

  private Map<String, String> gitlabProjectCommitId;

  private JenkinsServer server;

  private ConfigInterface configInterface;
  // key jenkinsName value 打包id
  private Map<String, Integer> jobLastBuild;

  public GitlabCron(
      JenkinsInfo jenkinsInfo, SendMsgInterface sendMsg, ConfigInterface configInterface) {
    // 填入基本配置
    this.sendMsg = sendMsg;
    this.configInterface = configInterface;
    gitlabProjectCommitId = new HashMap<>();
    // init jenkins server
    try {
      server =
          new JenkinsServer(
              new URI(jenkinsInfo.getUrl()), jenkinsInfo.getUsername(), jenkinsInfo.getPassword());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    // init gitlab info
    for (ProjectInfo projectInfo : configInterface.getProjectInfoList()) {
      GitlabConfig gitlabConfig =
          configInterface.getGitlabInfoMap().get(projectInfo.getGitlabConfig());
      if (Objects.isNull(gitlabConfig)) {
        log.error("没有对应的gitlab配置");
        try {
          sendMsg.send(projectInfo.getGitlabName() + "的gitlab配置不正确!", true);
        } catch (Exception e) {
          e.printStackTrace();
        }
        throw new RuntimeException("没有对应的gitlab配置");
      }
      Call call = getCall(gitlabConfig, projectInfo);
      Response execute;
      try {
        execute = call.execute();
        GitlabInfo info = JSON.parseArray(execute.body().string(), GitlabInfo.class).get(0);
        gitlabProjectCommitId.put(projectInfo.getId(), info.getId());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    // init jenkins job
    try {
      jobLastBuild = new HashMap<>();
      for (Map.Entry<String, Job> next : server.getJobs().entrySet()) {
        Build lastBuild = next.getValue().details().getLastBuild();
        if (Objects.nonNull(lastBuild)) {
          jobLastBuild.put(next.getKey(), lastBuild.getNumber());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      try {
        sendMsg.send(e.getMessage(), true);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      throw new RuntimeException(e.getMessage());
    }
  }

  @Scheduled(cron = "*/2 * * * * ?")
  public void gitlabCommitCron() {
    configInterface
        .getProjectInfoList()
        .forEach(
            projectInfo -> {
              Call call =
                  getCall(
                      configInterface.getGitlabInfoMap().get(projectInfo.getGitlabConfig()),
                      projectInfo);
              Response execute;
              try {
                execute = call.execute();
                GitlabInfo info = JSON.parseArray(execute.body().string(), GitlabInfo.class).get(0);
                if (!info.getId().equals(gitlabProjectCommitId.get(projectInfo.getId()))) {
                  gitlabProjectCommitId.put(projectInfo.getId(), info.getId());
                  if (info.getMessage().contains(projectInfo.getKey())) {
                    Build lastBuild = server.getJob(projectInfo.getJenkinsName()).getLastBuild();
                    if (lastBuild == null) {
                      server.getJob(projectInfo.getJenkinsName()).build();
                      sendMsgByJenkinsName(
                          projectInfo.getJenkinsName(), "触发自动打包,commit信息 : \n" + info.getMessage());
                    }
                    if (lastBuild.details().isBuilding()) {
                      sendMsgByJenkinsName(projectInfo.getJenkinsName(), "触发自动打包,但当前项目已在打包中,请手动处理");
                    } else {
                      server.getJob(projectInfo.getJenkinsName()).build();
                      sendMsgByJenkinsName(
                          projectInfo.getJenkinsName(), "触发自动打包,commit信息 : \n" + info.getMessage());
                    }
                  }
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }
  // key jenkinsName value 打包id
  private Map<String, Integer> inBuilding = new HashMap<>();

  // 将开始构建的任务添加
  @Scheduled(cron = "*/2 * * * * ?")
  public void jenkinsJobCron() throws Exception {
    for (Map.Entry<String, Job> next : server.getJobs().entrySet()) {

      Build lastBuild = next.getValue().details().getLastBuild();
      if (Objects.nonNull(lastBuild)) {
        int buildNumber = lastBuild.getNumber();
        // 如果启动前有这个项目
        if (jobLastBuild.containsKey(next.getKey())) {
          // 有新的构建项目
          if (!jobLastBuild.get(next.getKey()).equals(buildNumber)) {
            jobLastBuild.put(next.getKey(), buildNumber);
            inBuilding.put(next.getKey(), buildNumber);
            sendMsgByJenkinsName(next.getKey(), "开始打包");
          }
        } else {
          jobLastBuild.put(next.getKey(), buildNumber);
        }
      }
    }
  }

  @Scheduled(cron = "*/2 * * * * ?")
  public void building() {
    Iterator<Map.Entry<String, Integer>> iterator = inBuilding.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Integer> next = iterator.next();
      try {
        JobWithDetails job = server.getJob(next.getKey());
        Build build = job.getBuildByNumber(next.getValue());
        BuildWithDetails details = build.details();
        if (details.isBuilding()) {
          continue;
        }
        String time = details.getDuration() + "";
        if (time.length() >= 4) {
          time = new StringBuilder(time).insert(time.length() - 3, ".").toString() + "s";
        } else {
          time += "ms";
        }
        String msg = "打包结束 { " + details.getResult().name() + " }\n 持续时间 : " + time;
        sendMsgByJenkinsName(next.getKey(), msg);
        iterator.remove();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMsgByJenkinsName(String jenkinsName, String lastMsg) throws Exception {
    Optional<ProjectInfo> projectInfo =
        configInterface.getProjectInfoList().stream()
            .filter(info -> info.getJenkinsName().equals(jenkinsName))
            .findFirst();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    StringBuilder msg = new StringBuilder("时间 : ").append(sdf.format(new Date())).append("\n");
    if (projectInfo.isPresent()) {
      ProjectInfo info = projectInfo.get();
      msg.append("gitlabName: ").append(info.getGitlabName()).append("\n");
      msg.append("projectName : ").append(info.getProjectName()).append("\n");
    }
    msg.append("jenkinsName : ").append(jenkinsName).append("\n");
    msg.append(lastMsg);
    if (projectInfo.isPresent()) {
      ProjectInfo info = projectInfo.get();
      // 0通知所有人 1不通知任何人 2通知列表中手机号
      int i = info.sendAll();
      switch (i) {
        case 0:
          sendMsg.send(msg.toString(), true);
          break;
        case 2:
          sendMsg.send(msg.toString(), info.getMobiles());
          break;
        default:
          sendMsg.send(msg.toString());
      }
    } else {
      sendMsg.send(msg.toString());
    }
  }

  private Call getCall(GitlabConfig gitlabConfig, ProjectInfo projectInfo) {
    return OkHttpUtils.getCall(
        new Request.Builder()
            .url(
                gitlabConfig.getUri()
                    + "/api/v4/projects/"
                    + projectInfo.getGitlabId()
                    + "/repository/commits")
            .header("Private-Token", gitlabConfig.getToken())
            .get()
            .build());
  }
}
