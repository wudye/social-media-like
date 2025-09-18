package com.mwu.backend.pojo.mapper;

import com.mwu.backend.pojo.BlogVO;
import com.mwu.backend.pojo.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    BlogVO toBlogVO(Blog blog);

    List<BlogVO> toBlogVOList(List<Blog> blogList);


}
