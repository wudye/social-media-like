package com.mwu.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
//
//    redisTemplate Bean 用于你在代码中直接操作 Redis 时（如 redisTemplate.opsForValue()），它自定义了 key 和 value 的序列化方式。
//    springSessionDefaultRedisSerializer Bean 主要用于 Spring Session 管理 session 数据时的序列化，与 RedisTemplate 的配置互不影响。
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);


        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;

    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
//    如果你在 Controller 或 Service 中通过 session.setAttribute("username", "Tom") 操作 HttpSession，Spring Session 会自动将 Session 数据序列化并保存到 Redis。你无需手动注入或指定 RedisSerializer，Spring 会自动使用你配置的 springSessionDefaultRedisSerializer 进行序列化和反序列化。开发者只需像平常一样操作 HttpSession，底层的 Redis 存储和序列化细节由 Spring Session 自动处理。

}
