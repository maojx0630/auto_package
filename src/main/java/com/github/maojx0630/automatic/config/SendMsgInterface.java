package com.github.maojx0630.automatic.config;

import java.util.List;

/**
 * 发送消息
 *
 * @author 毛家兴
 * @date 2021-03-25 09:02
 */
public interface SendMsgInterface {
  default void send(String msg) throws Exception {
    send(msg, false);
  }

  default void send(String msg, boolean isAtAll) throws Exception {
    send(msg, isAtAll, null);
  }

  default void send(String msg, List<String> list) throws Exception {
    send(msg, false, list);
  }

  /**
   * @param msg 消息详情
   * @param isAtAll 是否@所有人
   * @param list @指定人
   * @author 毛家兴
   * @date 2021-03-25 09:03
   */
  void send(String msg, boolean isAtAll, List<String> list) throws Exception;
}
