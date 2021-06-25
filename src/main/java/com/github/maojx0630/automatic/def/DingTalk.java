package com.github.maojx0630.automatic.def;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.github.maojx0630.automatic.config.SendMsgInterface;
import com.github.maojx0630.automatic.properties.DingTalkInfo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 毛家兴
 * @date 2021-03-09 14:53
 */
public class DingTalk implements SendMsgInterface {

  private DingTalkInfo dingTalkInfo;

  public DingTalk(DingTalkInfo dingTalkInfo) {
    this.dingTalkInfo = dingTalkInfo;
  }

  @Override
  public void send(String msg) throws Exception {
    long timestamp = System.currentTimeMillis();
    String secret = dingTalkInfo.getSecret();
    String stringToSign = timestamp + "\n" + secret;
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
    String sign = URLEncoder.encode(new String(Base64.getEncoder().encode(signData)), "UTF-8");
    DingTalkClient client =
        new DefaultDingTalkClient(
            "https://oapi.dingtalk.com/robot/send?"
                + "access_token="
                + dingTalkInfo.getToken()
                + "&timestamp="
                + timestamp
                + "&sign="
                + sign);
    OapiRobotSendRequest request = new OapiRobotSendRequest();
    request.setMsgtype("text");
    OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
    text.setContent(msg);
    request.setText(text);
    client.execute(request);
  }
}
