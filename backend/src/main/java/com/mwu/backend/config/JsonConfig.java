package com.mwu.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@JsonComponent
public class JsonConfig {
//    这个 jacksonObjectMapper Bean 会被 Spring Boot 自动使用。
//    Spring Boot 会自动检测并使用你自定义的 ObjectMapper，用于全局的 JSON 序列化和反序列化，无需额外配置。这样所有通过 Spring MVC 返回或接收的 JSON 数据都会应用你的自定义序列化规则（如 Long 转字符串）。
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
