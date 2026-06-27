package com.clou.userdemo.task;

import com.clou.userdemo.service.UserRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RecommendCacheTask {

    @Autowired
    private UserRecommendService userRecommendService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshRecommendCache() {
        log.info("定时任务开始：重新计算推荐缓存");
        long startTime = System.currentTimeMillis();
        try {
            userRecommendService.refreshAllRecommendCache();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("定时任务完成：推荐缓存重新计算耗时{}ms", costTime);
        } catch (Exception e) {
            log.error("定时任务失败：推荐缓存重新计算异常", e);
        }
    }
}
