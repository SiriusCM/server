package {{packageName}};

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
    {% for name in nameList %}
    public static Map<Long, {{name['className']}}> map{{name['className']}} = new HashMap<>();
    {% endfor %}

    public void reload() {
        {% for name in nameList %}
        map{{name['className']}} = jdbcTemplate.queryForList("select * from {{name['table']}}", {{name['className']}}.class).stream()
        .collect(Collectors.toMap({{name['className']}}::sn, c -> c));
        {% endfor %}
    }
}
