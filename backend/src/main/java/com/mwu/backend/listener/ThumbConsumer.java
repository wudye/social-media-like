package com.mwu.backend.listener;

import com.mwu.backend.listener.thumb.msg.ThumbEvent;
import com.mwu.backend.pojo.entity.Thumb;
import com.mwu.backend.repository.BlogRepository;
import com.mwu.backend.repository.ThumbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbConsumer {

    private final ThumbRepository thumbRepository;
    private final BlogRepository blogRepository;

    // 用于分组与删除的复合键（替代 Hutool Pair）
    private static record Key(Long userId, Long blogId) {}

    @PulsarListener(topics = "thumb-dlq-topic")
    public void consumerDlq(Message<ThumbEvent> message) {
        MessageId messageId = message.getMessageId();
        log.info("dlq message = {}", messageId);
        log.info("消息 {} 已入库", messageId);
        log.info("已通知相关人员 {} 处理消息 {}", "坤哥", messageId);
    }

    @PulsarListener(
            subscriptionName = "thumb-subscription",
            topics = "thumb-topic",
            schemaType = SchemaType.JSON,
            batch = true,
            subscriptionType = SubscriptionType.Shared,
            negativeAckRedeliveryBackoff = "negativeAckRedeliveryBackoff",
            ackTimeoutRedeliveryBackoff = "ackTimeoutRedeliveryBackoff",
            deadLetterPolicy = "deadLetterPolicy"
    )
    @Transactional(rollbackFor = Exception.class)
    public void processBatch(List<Message<ThumbEvent>> messages) {
        log.info("ThumbConsumer processBatch: {}", messages.size());

        Map<Long, Long> countMap = new HashMap<>();
        List<Thumb> toInsert = new ArrayList<>();
        Set<Key> toDelete = new HashSet<>();

        // 提取有效事件
        List<ThumbEvent> events = messages.stream()
                .map(Message::getValue)
                .filter(Objects::nonNull)
                .toList();

        // 分组取每组最新有效事件（偶数抵消为 null）
        Map<Key, ThumbEvent> latestEvents = events.stream()
                .collect(Collectors.groupingBy(
                        e -> new Key(e.getUserId(), e.getBlogId()),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    list.sort(Comparator.comparing(ThumbEvent::getEventTime));
                                    if (list.size() % 2 == 0) return null; // 抵消
                                    return list.get(list.size() - 1);
                                }
                        )
                ));

        // 聚合插入/删除与计数变更
        latestEvents.forEach((key, event) -> {
            if (event == null) return;
            if (event.getType() == ThumbEvent.EventType.INCR) {
                countMap.merge(event.getBlogId(), 1L, Long::sum);
                Thumb thumb = new Thumb();
                thumb.setBlogId(event.getBlogId());
                thumb.setUserId(event.getUserId());
                toInsert.add(thumb);
            } else {
                toDelete.add(key);
                countMap.merge(event.getBlogId(), -1L, Long::sum);
            }
        });

        // 删除
        if (!toDelete.isEmpty()) {
            for (Key k : toDelete) {
                thumbRepository.deleteByUserIdAndBlogId(k.userId(), k.blogId());
            }
        }

        // 更新博客点赞计数
        batchUpdateBlogs(countMap);

        // 插入
        batchInsertThumbs(toInsert);
    }

    private void batchUpdateBlogs(Map<Long, Long> countMap) {
        if (countMap.isEmpty()) return;
        countMap.forEach((blogId, delta) -> {
            if (delta != null && delta != 0) {
                blogRepository.incrementThumbCount(blogId, delta);
            }
        });
    }

    private void batchInsertThumbs(List<Thumb> thumbs) {
        if (thumbs.isEmpty()) return;
        thumbRepository.saveAll(thumbs);
    }
}
