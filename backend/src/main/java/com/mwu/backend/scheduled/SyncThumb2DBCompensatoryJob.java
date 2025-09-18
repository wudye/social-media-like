package com.mwu.backend.scheduled;

import com.mwu.backend.constant.ThumbConstant;
import com.mwu.backend.utils.RedisKeyUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 定时将 Redis 中的临时点赞数据同步到数据库的补偿措施
 * 当数据在 Redis 中，由于不可控因素停机导致没有成功同步到数据库时，通过该任务补偿
 */
// @Component
@Slf4j
public class SyncThumb2DBCompensatoryJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SyncThumb2DBJob syncThumb2DBJob;


    @Scheduled(cron = "0 0 2 * * *")
    public void run() {
        log.info("开始补偿数据");
        Set<String> thumbKeys = redisTemplate.keys(RedisKeyUtil.getTempThumbKey("") + "*");
        if (thumbKeys == null || thumbKeys.isEmpty()) {
            log.info("没有需要补偿的临时数据");
            return;
        }
        Set<String> needHandleDataSet = thumbKeys.stream()
                .filter(Objects::nonNull)
                .map(thumbKey -> thumbKey.replace(ThumbConstant.TEMP_THUMB_KEY_PREFIX.formatted(""), ""))
                .collect(Collectors.toSet());

        if (needHandleDataSet.isEmpty()) {
            log.info("没有需要补偿的临时数据");
            return;
        }
        // 补偿数据
        for (String date : needHandleDataSet) {
            syncThumb2DBJob.syncThumb2DBByDate(date);
        }
        log.info("临时数据补偿完成");
    }
}
