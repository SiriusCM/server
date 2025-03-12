package {{packageName}};
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ExcelConfig {
    {% for name in nameList %}
    @Bean
    public Map<Integer, {{name['className']}}> map{{name['className']}}(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("select * from {{name['dbName']}}", {{name['className']}}.class).stream()
                .collect(Collectors.toMap({{name['className']}}::sn, c -> c));
    }
    {% endfor %}
}
