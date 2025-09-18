package com.mwu.backend.service.impl;

import com.mwu.backend.constant.ThumbConstant;
import com.mwu.backend.manager.cache.CacheManager;
import com.mwu.backend.pojo.DoThumbRequest;
import com.mwu.backend.pojo.entity.Thumb;
import com.mwu.backend.pojo.entity.User;
import com.mwu.backend.repository.ThumbRepository;
import com.mwu.backend.service.BlogService;
import com.mwu.backend.service.ThumbService;
import com.mwu.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Service("thumbServiceLocalCache")
@RequiredArgsConstructor
@Slf4j
public class ThumbServiceImpl implements ThumbService {

    private final UserService userService;

    private final BlogService blogService;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;
    private  final ThumbRepository thumbRepository;

    private final CacheManager cacheManager;
    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);

        synchronized (loginUser.getId().toString().intern()) {
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                Boolean exists = this.hasThumb(blogId, loginUser.getId());
                if (exists) {
                    throw new RuntimeException("用户已点赞");
                }
                boolean update = blogService.updateThumbCount(blogId, 1);
                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(blogId);
                Object check = thumbRepository.findThumbIdByBlogIdAndUserId(blogId, loginUser.getId());
                boolean success = true;
                if (check != null) {
                    success = false;
                } else {
                    thumbRepository.save(thumb);
                }

                if (update && success) {
                    String hashKey = ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId();
                    String fieldKey = blogId.toString();
                    Long realThumbId = thumb.getId();

                    redisTemplate.opsForHash().put(hashKey, fieldKey, realThumbId);
                    cacheManager.putIfPresent(hashKey, fieldKey, realThumbId);


                }

                return success;


            });
        }


    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);

        synchronized (loginUser.getId().toString().intern()) {

            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                Object thumbIdObj = cacheManager.get(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogId.toString());


                if (thumbIdObj == null || thumbIdObj.equals(ThumbConstant.UN_THUMB_CONSTANT)) {
                    throw new RuntimeException("用户未点赞");
                }


                boolean update = blogService.updateThumbCount(blogId, -1);
                Thumb thumb = thumbRepository.findThumbByBlogIdAndUserId(blogId, loginUser.getId());
                boolean success = true;
                if (thumb == null) {
                    success = false;
                } else {
                    thumbRepository.delete(thumb);
                }

                if (update && success) {
                    String hashKey = ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId();
                    String fieldKey = blogId.toString();
                    redisTemplate.opsForHash().put(hashKey, fieldKey, ThumbConstant.UN_THUMB_CONSTANT);
                    cacheManager.putIfPresent(hashKey, fieldKey, ThumbConstant.UN_THUMB_CONSTANT);

                }

                return success;
            });
        }
    }

    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        Object thumbIdObj = cacheManager.get(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, blogId.toString());
        if (thumbIdObj == null) {
            return false;
        }
        Long thumbId = (Long) thumbIdObj;
        return !thumbId.equals(ThumbConstant.UN_THUMB_CONSTANT);
    }

    @Override
    public void saveBatch(ArrayList<Thumb> thumbList) {
        thumbRepository.saveAll(thumbList);

    }

    @Override
    public void remove(Thumb thumb) {
        thumbRepository.delete(thumb);
    }




}
