package com.mwu.backend.repository;

import com.mwu.backend.pojo.entity.Thumb;
import com.mwu.backend.pojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThumbRepository extends JpaRepository<Thumb, Long> {

    List<Thumb> findByUserId(Long userId);

    void deleteByUserIdAndBlogId(Long aLong, Long aLong1);

    Long findThumbIdByBlogIdAndUserId(Long blogId, Long userId);

    Thumb findThumbByBlogIdAndUserId(Long blogId, Long id);
}
