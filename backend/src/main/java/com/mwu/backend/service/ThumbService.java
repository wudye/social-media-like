package com.mwu.backend.service;

import com.mwu.backend.pojo.DoThumbRequest;
import com.mwu.backend.pojo.entity.Thumb;
import jakarta.servlet.http.HttpServletRequest;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

public interface ThumbService {


    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);

    Boolean hasThumb(Long blogId, Long userId);

    void saveBatch(ArrayList<Thumb> thumbList);

    void remove(Thumb thumb);

}
