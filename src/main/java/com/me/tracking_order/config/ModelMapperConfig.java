package com.me.tracking_order.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                // tự map khi thuộc tính khớp rõ ràng
                .setMatchingStrategy(MatchingStrategies.STRICT)
                // không ghi đè thuộc tính hiện tại bằng null khi update
                .setSkipNullEnabled(true)
                // hạn chế Model mapper tự đi sâu vào qhe jpa và gặp vòng lặp
                .setPreferNestedProperties(false);

        return modelMapper;
    }
}
