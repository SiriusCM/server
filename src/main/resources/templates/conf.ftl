package {{packageName}};

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record {{className}}(int sn{% for field in fieldList %}, {{field['type']}} {{field['name']}}{% endfor %}) {

    public static Map<Integer, {{className}}> map = new HashMap<>();

    public static void reload(JdbcTemplate jdbcTemplate) {
        map.put(1, new {{className}}(1, "test1"));
        map.put(2, new {{className}}(2, "test2"));
        map.put(3, new {{className}}(3, "test3"));

        map = jdbcTemplate.queryForList("select * from {{dbName}}", {{className}}.class).stream()
                .collect(Collectors.toMap({{className}}::sn, c -> c));
    }
}
