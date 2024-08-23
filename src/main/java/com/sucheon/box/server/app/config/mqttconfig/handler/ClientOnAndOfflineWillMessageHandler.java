package com.sucheon.box.server.app.config.mqttconfig.handler;

import com.alibaba.fastjson.JSONObject;
import com.sucheon.box.server.app.model.device.Device;
import com.sucheon.box.server.app.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 设备上下线控制
 */
@Component
public class ClientOnAndOfflineWillMessageHandler implements MessageHandler {
    Logger logger = LoggerFactory.getLogger(ClientOnAndOfflineWillMessageHandler.class);
    @Autowired
    DeviceService deviceService;
    @Value("${emq.username}")
    private String EMQ_USERNAME;


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        JSONObject mqttMessage;
        MessageHeaders header = message.getHeaders();
        String state = header.get("mqtt_topic").toString().
                substring(header.get("mqtt_topic").toString().lastIndexOf("/") + 1);
        if (state.equals("connected")) {
            try {
                mqttMessage = (JSONObject) JSONObject.parse(message.getPayload().toString());
                String username = mqttMessage.getString("username");
                if (username.equals(EMQ_USERNAME)) {
                    logger.info("API服务器[" + username + "]连接成功!");
                } else {
                    Device device = deviceService.findADevice(Long.parseLong(username));
                    if (device != null) {
                        device.setOnline(true);
                        deviceService.save(device);
                        logger.info("设备:[" + device.getDeviceName() + "]状态更新为上线");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("解析消息时出现了格式错误!");
            }


        } else if (state.equals("disconnected")) {
            try {
                mqttMessage = (JSONObject) JSONObject.parse(message.getPayload().toString());
                String username = mqttMessage.getString("username");
                if (username.equals(EMQ_USERNAME)) {
                    logger.info("API服务器[" + username + "]断开连接!");
                } else {
                    Device device = deviceService.findADevice(Long.parseLong(username));
                    if (device != null) {
                        device.setOnline(false);
                        device.setLastActiveDate(new Date());
                        deviceService.save(device);
                        logger.info("设备:[" + device.getDeviceName() + "]状态更新为下线");
                    }
                }
            } catch (Exception e) {
                logger.error("解析消息时出现了格式错误!");
            }

        }
    }
}
