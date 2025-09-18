package com.mwu.backend.service.impl;

import com.mwu.backend.pojo.BlogVO;
import com.mwu.backend.pojo.entity.Blog;
import com.mwu.backend.pojo.entity.User;
import com.mwu.backend.pojo.mapper.BlogMapper;
import com.mwu.backend.repository.BlogRepository;
import com.mwu.backend.service.BlogService;
import com.mwu.backend.service.ThumbService;
import com.mwu.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Resource
    private BlogRepository blogRepository;
    @Resource
    private BlogMapper blogMapper;


    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public BlogVO getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        BlogVO blogVO = blogMapper.toBlogVO(blog);

        return getBlogVO(blog, userService.getLoginUser(request));




    }

    @Override
    public BlogVO getBlogVO(Blog blog, User loginUser) {
        BlogVO blogVO = blogMapper.toBlogVO(blog);
        if (loginUser != null) {
            Boolean exist = thumbService.hasThumb(blog.getId(), loginUser.getId());
            blogVO.setHasThumb(exist);

        }
        return blogVO;
    }

    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        List<BlogVO> blogVOList = blogMapper.toBlogVOList(blogList);
        if (loginUser != null) {
            for (BlogVO blogVO : blogVOList) {
                Boolean exist = thumbService.hasThumb(blogVO.getId(), loginUser.getId());
                blogVO.setHasThumb(exist);
            }
        }

        return blogVOList;
    }

    @Override
    public List<Blog> findall() {

        return blogRepository.findAll();
    }

    @Override
    public boolean updateThumbCount(Long blogId, int i) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog != null) {
            blog.setThumbCount(blog.getThumbCount() + i);
            blogRepository.save(blog);
            return true;
        }
        return false;
    }
}
