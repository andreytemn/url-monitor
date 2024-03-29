package com.github.andreytemn.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Provides a database schema on the application initialization. Uses the schema.sql file by default.
 */
@Configuration
@Slf4j
public class DatabaseConfig {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        ResourceDatabasePopulator populate = new ResourceDatabasePopulator();
        log.debug("Initializing schema");
        populate.addScript(new ClassPathResource("schema.sql"));
        log.debug("Seeding default users");
        populate.addScript(new ClassPathResource("data.sql"));
        populate.execute(dataSource);

        return dataSource;
    }

}