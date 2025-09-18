# Jedis 和 Lettuce 都是 Java 语言下常用的 Redis 客户端。主要区别如下：
    Jedis：采用阻塞式 I/O，每个线程需要一个独立的 Jedis 连接实例，适合单线程或连接数较少的场景。实现简单，性能较高，但在高并发下连接资源消耗大。
    Lettuce：基于 Netty，支持异步、响应式和非阻塞 I/O。一个连接可被多个线程安全共享，适合高并发和响应式编程场景。功能更丰富，资源利用率更高。
    选择 Jedis 通常是因为项目简单、并发量不大，或者需要同步阻塞操作。Lettuce 更适合高并发、异步或响应式需求。

#     <artifactId>caffeine</artifactId> 
    这是 Caffeine 缓存库的 Maven 依赖。Caffeine 是一个高性能、近乎 LRU 策略的本地缓存库，常用于 Java 应用中提升数据访问速度，减少对数据库或远程服务的频繁访问。添加该依赖后，可以在项目中使用 Caffeine 实现本地缓存功能。

# 在 Spring Boot 集成消息队列中，最流行的依赖通常是：
    spring-boot-starter-kafka（Kafka，广泛应用于高吞吐、分布式场景）
    spring-boot-starter-amqp（RabbitMQ，易用性高，应用广泛）
    spring-boot-starter-pulsar 近年来也逐渐流行，但整体使用量和社区成熟度目前仍低于 Kafka 和 RabbitMQ。选择时建议优先考虑 Kafka 或 RabbitMQ，除非有特定的 Pulsar 需求。

#     <artifactId>micrometer-registry-prometheus</artifactId>
    这是用于将 Micrometer 监控指标导出到 Prometheus 的依赖。Micrometer 是 Spring Boot 默认的度量监控库，micrometer-registry-prometheus 让应用可以通过 HTTP 接口暴露 Prometheus 可采集的指标数据。
    management.endpoints.web.exposure.include=prometheus
    management.endpoint.prometheus.enabled=true访问 /actuator/prometheus，即可获取 Prometheus 格式的监控数据。
    import io.micrometer.core.instrument.MeterRegistry;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    @RestController
    public class DemoController {
    private final MeterRegistry meterRegistry;
    
        public DemoController(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }
    
        @GetMapping("/hello")
        public String hello() {
            meterRegistry.counter("custom.hello.count").increment();
            return "Hello, Prometheus!";
        }
    }

# mvc:
    servlet:
    path: /api
    这部分配置将 Spring MVC 的默认 Servlet 映射路径设置为 /api。默认情况下，Spring Boot 的所有请求路径会映射到根路径 /，通过此配置，所有的请求路径将以 /api 为前缀。例如，原本映射到 /users 的控制器现在会映射到 /api/users。


# profiles:
    active: local
    这部分配置指定了当前激活的 Spring 配置文件为 local。Spring Boot 支持多环境配置，通过 profiles 可以为不同环境（如开发、测试、生产）定义不同的配置文件。这里的 local 表示应用将加载 application-local.yml 或 application-local.properties 中的配置，用于本地开发环境
    如果没有 application-local.yml 或 application-local.properties 文件，Spring Boot 仍然可以正常启动，但不会加载任何属于 local profile 的特定配置。此时，应用只会使用默认的 application.yml 或 application.properties 中的配置。也就是说，profiles.active: local 只会生效于存在 local 配置文件时，否则不会报错，只是没有额外的本地环境配置被应用。

# Spring Boot 项目中使用这个 Lua 脚本进行点赞操作
    Lua 是一种轻量级、嵌入式的脚本语言，常用于 Redis 脚本编程。通过 Lua 脚本，可以在 Redis 服务器端实现原子性操作，提升并发安全性和性能。
# pulsar define(multiple two ways)
## 1. 使用 Spring Boot Starter
    // Producer 示例
    @Component
    public class ProducerA {
    @Autowired
    private PulsarTemplate<String> pulsarTemplate;
    
        public void sendToTopicA(String msg) {
            pulsarTemplate.send("topic-a", msg);
        }
    }
    
    @Component
    public class ProducerB {
    @Autowired
    private PulsarTemplate<String> pulsarTemplate;
    
        public void sendToTopicB(String msg) {
            pulsarTemplate.send("topic-b", msg);
        }
    }
    
    // Listener 示例
    @Component
    public class ListenerA {
    @PulsarListener(subscriptionName = "sub-a", topics = "topic-a")
    public void listenA(String msg) {
    System.out.println("A收到: " + msg);
    }
    }
    
    @Component
    public class ListenerB {
    @PulsarListener(subscriptionName = "sub-b", topics = "topic-b")
    public void listenB(String msg) {
    System.out.println("B收到: " + msg);
    }
    }

    }
   ##  spring:
    pulsar:
    client:
    service-url: pulsar://192.168.80.128:6650
    producer:
    topic-name: topic-a # 默认 producer 主题
    consumer:
    topic-names:
    - topic-a # 默认 listener 主题
    subscription-name: sub-a # 默认订阅名
    
    # 如需为不同 producer/listener 配置不同 topic，可用多 profile 或自定义配置
    custom:
    pulsar:
    producer-b:
    topic-name: topic-b
    consumer-b:
    topic-name: topic-b
    subscription-name: sub-b
    ## 

# Caffeine 和 Redis Cache 的主要区别在于它们的位置和用途。
    Caffeine：是一个本地内存缓存（In-Process Cache）。它作为库运行在您的应用程序内部，数据直接存储在应用程序的堆内存中。
    Redis：是一个分布式/远程缓存（Out-of-Process Cache）。它作为一个独立的服务运行，您的应用程序通过网络连接来访问它。
    以下是它们的核心差异对比：
    特性
    Caffeine (本地缓存)
    Redis (分布式缓存)
    速度
    极快。直接从内存读取，无网络开销。
    快。但受网络延迟影响。
    数据共享
    不共享。每个应用实例都有自己独立的缓存。
    共享。所有连接到 Redis 的应用实例共享同一份缓存数据。
    存储位置
    应用程序的 JVM 堆内存中。
    独立的 Redis 服务器内存中。
    容量
    受限于单个应用的堆内存大小，通常较小。
    受限于 Redis 服务器的内存大小，通常较大。
    数据一致性
    数据在应用重启后丢失。
    可以配置持久化，重启后数据可恢复。
    典型用途
    一级缓存 (L1)：缓存最热点的数据，减少对二级缓存的访问。
    二级缓存 (L2)：作为应用和数据库之间的缓存层，跨应用共享数据。
    <hr></hr>
    示例：多级缓存架构
    这正是您代码中 CacheManager 所实现的架构。假设您部署了两个相同的应用实例（实例 A 和实例 B）来处理用户请求。
    场景：获取用户信息
    首次请求：
    用户请求到达实例 A，需要获取用户 "user123" 的信息。
    实例 A 的 Caffeine 缓存（本地）中没有数据。
    实例 A 查询 Redis 缓存（分布式）中也没有数据。
    实例 A 从数据库中查询到 "user123" 的信息。
    实例 A 将用户信息存入 Redis 缓存，以便其他实例也能使用。
    实例 A 同时将用户信息存入自己的 Caffeine 缓存。
    后续请求到同一实例：
    另一个请求到达实例 A，同样需要获取 "user123" 的信息。
    实例 A 直接从其 Caffeine 缓存中命中数据并立即返回。这是最快的路径，无需访问网络。
    后续请求到不同实例：
    一个请求到达实例 B，需要获取 "user123" 的信息。
    实例 B 的 Caffeine 缓存中没有数据（因为它是实例 B 自己的本地缓存）。
    实例 B 查询 Redis 缓存，成功命中数据并返回。这避免了查询数据库，速度依然很快。
    如果 "user123" 成为热点数据，实例 B 也会将其存入自己的 Caffeine 缓存中。
    总结：
    Caffeine 和 Redis 在您的架构中协同工作：Caffeine 作为每个应用实例的“口袋缓存”，提供极致的访问速度；Redis 作为所有实例共享的“中央仓库”，减少对后端数据库的压力并保证数据在不同实例间的可用性。