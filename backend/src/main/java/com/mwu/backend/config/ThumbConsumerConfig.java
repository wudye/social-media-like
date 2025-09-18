package com.mwu.backend.config;

import org.apache.pulsar.client.api.BatchReceivePolicy;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.RedeliveryBackoff;
import org.apache.pulsar.client.impl.MultiplierRedeliveryBackoff;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.annotation.PulsarListenerConsumerBuilderCustomizer;

import java.util.concurrent.TimeUnit;

@Configuration

public class ThumbConsumerConfig<T> implements PulsarListenerConsumerBuilderCustomizer<T>{


//    timeout(10000, TimeUnit.MICROSECONDS) 表示每批最多等待 10 毫秒（即 0.01 秒）来收集消息，但这并不意味着 1 秒内一定能处理上亿条消息。
//    实际吞吐量取决于多种因素：
//    maxNumMessages(10000)：每批最多拉取 10000 条消息。
//    timeout(10 毫秒)：如果 10 毫秒内没有收满 10000 条消息，也会提前返回当前已收集的消息。
//    Pulsar 服务端性能、网络带宽、消费者处理能力等都会影响实际每秒能处理多少消息。
//    理论上，如果每 10 毫秒都能拉满 10000 条消息，1 秒最多能拉取 1000 * 10000 = 1,000,000 条消息（100 万），而不是 10 亿条。实际情况通常会低于这个上限。
//timeout(10000, TimeUnit.MICROSECONDS) 的最大值取决于 BatchReceivePolicy 的实现，理论上可以设置为 Long.MAX_VALUE 微秒，但实际应用中受限于 Pulsar 客户端和 JVM 的实现。
//    timeout 的单位是微秒（1 秒 = 1,000,000 微秒），最大值可以设置为很大，比如：
//    timeout(Long.MAX_VALUE, TimeUnit.MICROSECONDS)，约等于 292471 年
//    实际建议不要设置过大，通常几秒（几百万微秒）即可满足业务需求
//    如果设置过大，批量拉取会长时间等待，可能导致消息延迟增加。实际最大值建议参考 Pulsar 官方文档和你的业务场景。
//  “受限于 Pulsar 客户端和 JVM 的实现”意思是，虽然理论上 timeout 可以设置为非常大的值（比如 Long.MAX_VALUE 微秒），但在实际运行时，这个值会受到 Pulsar 客户端代码本身的限制，以及 Java 虚拟机（JVM）对定时、线程等待等机制的支持限制。比如：
//Pulsar 客户端内部可能对超时时间做了最大值校验或有默认上限，超过会报错或被截断。
//JVM 的定时器、线程等待等方法（如 Thread.sleep、ScheduledExecutorService）对最大等待时间有实际限制，超出可能导致异常或行为不可预期。
//因此，虽然参数类型允许很大，但实际可用范围要参考 Pulsar 官方文档和实际运行环境
    @Override
    public void customize(ConsumerBuilder<T> consumerBuilder) {
        consumerBuilder.batchReceivePolicy(
                BatchReceivePolicy.builder()
                        .maxNumMessages(10000)
                        .timeout(10000, TimeUnit.MICROSECONDS)
                        .build()
        );

    }
    @Bean
    public RedeliveryBackoff negativeAckRedeliveryBackoff() {
        return MultiplierRedeliveryBackoff.builder()
                .minDelayMs(1000)
                .maxDelayMs(60000)
                .multiplier(2.0)
                .build();
    }


    @Bean
    public RedeliveryBackoff ackTimeoutRedeliveryBackoff() {
        return MultiplierRedeliveryBackoff.builder()
                .minDelayMs(1000)
                .maxDelayMs(60000)
                .multiplier(2.0)
                .build();
    }

    @Bean
    public DeadLetterPolicy deadLetterPolicy() {
        return DeadLetterPolicy.builder()
                // 最大重试次数
                .maxRedeliverCount(3)
                // 死信主题名称
                .deadLetterTopic("thumb-dlq-topic")
                .build();
    }
}
