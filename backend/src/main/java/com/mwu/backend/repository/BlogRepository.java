package com.mwu.backend.repository;

import com.mwu.backend.pojo.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BlogRepository  extends JpaRepository<Blog, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Blog b SET b.thumbCount = :count WHERE b.id = :blogId")
    void updateThumbCountByBlogId(@Param("blogId") Long blogId, @Param("count") Long count);


    @Modifying
    @Query("update Blog b set b.thumbCount = b.thumbCount + :delta where b.id = :id")
    int incrementThumbCount(@Param("id") Long id, @Param("delta") Long delta);
}
