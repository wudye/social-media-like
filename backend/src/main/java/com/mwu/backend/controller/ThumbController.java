package com.mwu.backend.controller;

import com.mwu.backend.common.BaseResponse;
import com.mwu.backend.common.ResultUtils;
import com.mwu.backend.pojo.DoThumbRequest;
import com.mwu.backend.service.ThumbService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("thumb")
public class ThumbController {

    @Resource
    private ThumbService thumbService;


    @PostConstruct
    public void init() {
        System.out.println("当前使用的 ThumbService 实现类: " + thumbService.getClass().getName());
    }
//    这两个计数器通过 register(registry) 方法注册到 MeterRegistry 中，MeterRegistry 是 Micrometer 的核心组件，用于管理和导出指标数据。通过这种方式，应用程序可以监控点赞操作的成功和失败次数，并将这些指标导出到监控系统（如 Prometheus 或 Grafana）进行可视化和分析。
    private final Counter successCounter;
    private final Counter failureCounter;

    public ThumbController(MeterRegistry registry) {
        this.successCounter = Counter.builder("thumb.success.count")
                .description("Total successful thumb")
                .register(registry);
        this.failureCounter = Counter.builder("thumb.failure.count")
                .description("Total failed thumb")
                .register(registry);
    }


    @PostMapping("/do")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Boolean success;
        try {
            success = thumbService.doThumb(doThumbRequest,request);
            if(success) {
                successCounter.increment();
            } else {
                failureCounter.increment();
            }
        }catch (Exception e) {
            failureCounter.increment();
            throw e;
        }
        return ResultUtils.success(success);
    }
}
