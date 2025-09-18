package com.mwu.backend.demo;

import com.mwu.backend.listener.thumb.msg.ThumbEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.core.PulsarTemplate;

@Configuration
@Slf4j
public class PulsarBootHelloWorld {

//    @Bean
//    ApplicationRunner runner(PulsarTemplate<ThumbEvent> pulsarTemplate) {
//        // 正确的做法：返回一个包含了业务逻辑的 ApplicationRunner
//        return args -> {
//            ThumbEvent thumbEvent = new ThumbEvent();
//            // 将循环逻辑移到这里
//            for (int i = 0; i < 10000; i++) {
//                pulsarTemplate.send("thumb-topic", thumbEvent);
//                log.info("Pulsar boot-hello-pulsar-topic" + (i + "----------------------------"));
//            }
//        };
//    }
//
//    @PulsarListener(subscriptionName = "hello-pulsar-sub", topics = "thumb-topic")
//        // 注意：监听器的参数类型也需要匹配，这里应该是 ThumbEvent
//    void listen(ThumbEvent message) {
//        log.info("Message Received: {}", message.toString());
//    }
}
