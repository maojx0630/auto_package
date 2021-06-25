package com.github.maojx0630.automatic.config;

/**
 * 发送消息
 *
 * @author 毛家兴
 * @date 2021-03-25 09:02
 */
public interface SendMsgInterface {

  /**
   * @param msg 消息详情
   * @author 毛家兴
   * @date 2021-03-25 09:03
   */
  void send(String msg) throws Exception;
}
