package {{packageName}};
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ExcelConfig {
    {% for name in nameList %}
    public static Map<Integer, {{name['className']}}> map{{name['className']}} = new HashMap<>();
    {% endfor %}

    public static void reload(JdbcTemplate jdbcTemplate) {
        {% for name in nameList %}
        map{{name['className']} = jdbcTemplate.queryForList("select * from {{name['dbName']}}", {{name['className']}.class).stream()
        .collect(Collectors.toMap({{name['className']}::sn, c -> c));
        {% endfor %}
    }
    {% for name in nameList %}
    @Bean
    public Map<Integer, {{name['className']}}> map{{name['className']}}(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("select * from {{name['dbName']}}", {{name['className']}}.class).stream()
                .collect(Collectors.toMap({{name['className']}}::sn, c -> c));
    }
    {% endfor %}
}
