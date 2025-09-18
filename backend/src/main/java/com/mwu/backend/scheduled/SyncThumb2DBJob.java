package com.mwu.backend.scheduled;

import com.mwu.backend.pojo.entity.Thumb;
import com.mwu.backend.pojo.enums.ThumbTypeEnum;
import com.mwu.backend.pojo.mapper.BlogMapper;
import com.mwu.backend.repository.BlogRepository;
import com.mwu.backend.service.ThumbService;
import com.mwu.backend.utils.RedisKeyUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Slf4j
public class SyncThumb2DBJob {
    @Resource
    private ThumbService thumbService;



    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private BlogRepository blogRepository;


    @Scheduled(fixedRate =  10000)
    @Transactional(rollbackFor = Exception.class)
    public void  run() {
        log.info("start to achieve thumb data from redis to db");
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:");
        int secondGroup = nowDate.getSecond() / 10;
        secondGroup = secondGroup > 0 ? secondGroup - 1 : 0;
        String date = nowDate.format(formatter) + secondGroup * 10;
        syncThumb2DBByDate(date);
        log.info("临时数据同步完成");
    }


    public void syncThumb2DBByDate(String date) {
        // 获取到临时点赞和取消点赞数据
        // todo 如果数据量过大，可以分批读取数据
        String tempThumbKey = RedisKeyUtil.getTempThumbKey(date);
        Map<Object, Object> allTempThumbMap = redisTemplate.opsForHash().entries(tempThumbKey);
        boolean thumbMapEmpty = allTempThumbMap == null || allTempThumbMap.isEmpty();


        // 同步 点赞 到数据库
        // 构建插入列表并收集blogId
        Map<Long, Long> blogThumbCountMap = new HashMap<>();
        if (thumbMapEmpty) {
            return;
        }
        ArrayList<Thumb> thumbList = new ArrayList<>();
        List<Thumb> thumbsToRemove = new ArrayList<>();
        boolean needRemove = false;
        for (Object userIdBlogIdObj : allTempThumbMap.keySet()) {
            String userIdBlogId = (String) userIdBlogIdObj;
            String[] userIdAndBlogId = userIdBlogId.split(":");
            Long userId = Long.valueOf(userIdAndBlogId[0]);
            Long blogId = Long.valueOf(userIdAndBlogId[1]);
            // -1 取消点赞，1 点赞
            Integer thumbType = Integer.valueOf(allTempThumbMap.get(userIdBlogId).toString());
            if (thumbType == ThumbTypeEnum.INCR.getValue()) {
                Thumb thumb = new Thumb();
                thumb.setUserId(userId);
                thumb.setBlogId(blogId);
                thumbList.add(thumb);
            } else if (thumbType == ThumbTypeEnum.DECR.getValue()) {
                // 拼接查询条件，批量删除
                // todo 数据量过大，可以分批操作
                needRemove = true;
                Thumb thumbToRemove = new Thumb();
                thumbToRemove.setUserId(userId);
                thumbToRemove.setBlogId(blogId);
                thumbsToRemove.add(thumbToRemove);
            } else {
                if (thumbType != ThumbTypeEnum.NON.getValue()) {
                    log.warn("数据异常：{}", userId + "," + blogId + "," + thumbType);
                }
                continue;
            }
            // 计算点赞增量
            blogThumbCountMap.put(blogId, blogThumbCountMap.getOrDefault(blogId, 0L) + thumbType);

        }
        // 批量插入
        thumbService.saveBatch(thumbList);
        // 批量删除
        if (needRemove) {
            // This will be replaced with a JPA repository call
            // For now, we can iterate and delete, though it's inefficient
            // A better approach would be a custom repository method
            for (Thumb thumb : thumbsToRemove) {
                thumbService.remove(thumb);
            }
        }
        // 批量更新博客点赞量
        if (!blogThumbCountMap.isEmpty()) {
            blogThumbCountMap.forEach((blogId, count) -> {
                blogRepository.updateThumbCountByBlogId(blogId, count);
            });
        }
        // 异步删除
        Thread.startVirtualThread(() -> {
            redisTemplate.delete(tempThumbKey);
        });
    }

}
