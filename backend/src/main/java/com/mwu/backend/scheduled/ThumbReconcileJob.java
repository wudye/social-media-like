package com.mwu.backend.scheduled;

import com.google.common.collect.Sets;
import com.mwu.backend.constant.ThumbConstant;
import com.mwu.backend.listener.thumb.msg.ThumbEvent;
import com.mwu.backend.pojo.entity.Thumb;
import com.mwu.backend.repository.ThumbRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ThumbReconcileJob {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ThumbRepository thumbRepository;

    @Resource
    private PulsarTemplate<ThumbEvent> pulsarTemplate;

    @Scheduled(cron = "0 0 2 * * ?")
    public void run() {
        long startTime = System.currentTimeMillis();
        Set<Long> userIds = new HashSet<>();
        String pattern = ThumbConstant.USER_THUMB_KEY_PREFIX + "*";
        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match(pattern).count(5000).build())) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                Long userId;
            try {
                userId = Long.valueOf(key.replace(ThumbConstant.USER_THUMB_KEY_PREFIX, ""));
            } catch (NumberFormatException e) {
                log.error("Failed to parse Redis key: " + key, e);
                continue;
            }
                userIds.add(userId);
            }
        }
        // 2. 逐用户比对
        userIds.forEach(userId -> {
            Set<Long> redisBlogIds = redisTemplate.opsForHash().keys(ThumbConstant.USER_THUMB_KEY_PREFIX + userId).stream()
                    .map(Object::toString)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            Set<Long> mysqlBlogIds = Optional.ofNullable(thumbRepository.findByUserId(userId))
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(Thumb::getBlogId)
                    .collect(Collectors.toSet());

            // 3. 计算差异（Redis有但MySQL无）
            Set<Long> diffBlogIds = Sets.difference(redisBlogIds, mysqlBlogIds);

            // 4. 发送补偿事件
            sendCompensationEvents(userId, diffBlogIds);
        });

        log.info("Redis扫描完成，共处理用户数: {}", userIds.size());
        log.info("对账任务完成，耗时 {}ms", System.currentTimeMillis() - startTime);
    }

    /**
     * 发送补偿事件到Pulsar
     */
    private void sendCompensationEvents(Long userId, Set<Long> blogIds) {
        blogIds.forEach(blogId -> {
            ThumbEvent thumbEvent = new ThumbEvent(userId, blogId, ThumbEvent.EventType.INCR, LocalDateTime.now());
            pulsarTemplate.sendAsync("thumb-topic", thumbEvent)
                    .exceptionally(ex -> {
                        log.error("补偿事件发送失败: userId={}, blogId={}", userId, blogId, ex);
                        return null;
                    });
        });
    }
}
