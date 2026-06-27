package com.clou.shopdemo.task;

import com.clou.shopdemo.service.ShopSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ShopSyncTask {

    @Autowired
    private ShopSyncService shopSyncService;

    /**
     * 每两天凌晨0点执行全量同步
     */
    @Scheduled(cron = "0 0 0 */2 * ?")
    public void syncEsDataTask() {
        log.info("开始执行定时任务：ES 数据全量同步...");
        try {
            shopSyncService.syncAllToEs();
            log.info("定时任务执行成功：ES 数据全量同步完成");
        } catch (IOException e) {
            log.error("定时任务执行失败：ES 数据全量同步异常", e);
        }
    }
}
