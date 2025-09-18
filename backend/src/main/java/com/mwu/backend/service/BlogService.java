package com.mwu.backend.service;

import com.mwu.backend.pojo.BlogVO;
import com.mwu.backend.pojo.entity.Blog;
import com.mwu.backend.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BlogService  {

    BlogVO getBlogVOById(long blogId, HttpServletRequest request);

    BlogVO getBlogVO(Blog blog, User loginUser);

    List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request);

    List<Blog> findall();

    boolean updateThumbCount(Long blogId, int i);
}
