package com.sirius.server;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record ConfTest(int sn, String name) {

    public static Map<Integer, ConfTest> map = new HashMap<>();

    public static void reload(JdbcTemplate jdbcTemplate) {
        map.put(1, new ConfTest(1, "test1"));
        map.put(2, new ConfTest(2, "test2"));
        map.put(3, new ConfTest(3, "test3"));

        map = jdbcTemplate.queryForList("select * from conftest", ConfTest.class).stream()
                .collect(Collectors.toMap(ConfTest::sn, c -> c));
    }
}