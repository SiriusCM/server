package org.sirius.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author gaoliandi
 * @time 2023/7/19
 */
@Configuration
public class DBConfigurer {
    @Primary
    @Bean("DataSource0")
    @ConfigurationProperties("spring.datasource.db0")
    public DataSource dataSource0() {
        return DataSourceBuilder.create().build();
    }

    @Bean("DataSource1")
    @ConfigurationProperties("spring.datasource.db1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("jdbcTemplate0")
    public JdbcTemplate jdbcTemplate0(@Qualifier("DataSource0") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("jdbcTemplate1")
    public JdbcTemplate jdbcTemplate1(@Qualifier("DataSource1") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
