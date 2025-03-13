package com.sirius.server.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ExcelConfig implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        reload();
    }

    public static Map<Integer, ExcelTest> mapExcelTest = new HashMap<>();


    public void reload() {

        mapExcelTest = jdbcTemplate.queryForList("select * from excel_test", ExcelTest.class).stream()
                .collect(Collectors.toMap(ExcelTest::sn, c -> c));

    }
}