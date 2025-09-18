package com.mwu.backend.controller;

import com.mwu.backend.common.BaseResponse;
import com.mwu.backend.common.ResultUtils;
import com.mwu.backend.pojo.BlogVO;
import com.mwu.backend.pojo.entity.Blog;
import com.mwu.backend.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/get")
    public BaseResponse<BlogVO> get(long id, HttpServletRequest httpServletRequest) {
        BlogVO blogVO = blogService.getBlogVOById(id, httpServletRequest);
        return ResultUtils.success(blogVO);
    }

    @GetMapping("/list")
    public BaseResponse<List<BlogVO>> list(HttpServletRequest request) {
        List<Blog> blogList = blogService.findall();
        List<BlogVO> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }


}
