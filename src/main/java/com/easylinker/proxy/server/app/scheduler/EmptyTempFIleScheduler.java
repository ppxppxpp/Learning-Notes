package com.easylinker.proxy.server.app.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时清空临时文件
 */
@Component
public class EmptyTempFIleScheduler {
    private Logger logger = LoggerFactory.getLogger(EmptyTempFIleScheduler.class);

    @Scheduled(cron = "0 0 0,12 * * ? ")
    public void emptyTempFIleSchedule() {
        logger.info("Empty Temp File!");
    }


}
