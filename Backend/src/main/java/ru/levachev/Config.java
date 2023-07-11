package ru.levachev;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("ru.levachev")
@PropertySource("classpath:dataBase.properties")
public class Config {
    @Bean
    public DataSource dataSourceOrganisationDB(){
        System.out.println("init");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/SCI-FIDICEOrganisationDataBase");
        dataSource.setUsername("postgres");
        dataSource.setPassword("21210");

        return dataSource;
    }

    @Bean
    public DataSource dataSourceGamesDB(){
        System.out.println("init");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/SCI-FIDICEGamesDataBase");
        dataSource.setUsername("postgres");
        dataSource.setPassword("21210");

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplateOrganisationDB(){
        return new JdbcTemplate(dataSourceOrganisationDB());
    }

    @Bean
    public JdbcTemplate jdbcTemplateGamesDB(){
        return new JdbcTemplate(dataSourceGamesDB());
    }
}
