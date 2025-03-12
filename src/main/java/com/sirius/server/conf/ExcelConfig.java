package com.sirius.server.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ExcelConfig {
    
    @Bean
    public Map<Integer, ExcelTest> mapExcelTest(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("select * from excel_test", ExcelTest.class).stream()
                .collect(Collectors.toMap(ExcelTest::sn, c -> c));
    }
    
}