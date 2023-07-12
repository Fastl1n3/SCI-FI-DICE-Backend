package ru.levachev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.ZoneId;

@Configuration
@ComponentScan("ru.levachev")
@PropertySource("file:dataBase.properties")
public class Config {

    public static final int daysPerWeek=7;
    public static final int hoursPerDay=24;
    public static final ZoneId NSKZoneId = ZoneId.of("GMT+7");

    //@Value("${roomNumber}")
    public int roomNumber=5;

    @Value("${DBDriver}")
    public String DBDriver;

    @Value("${organisationDBUrl}")
    public String organisationDBUrl;

    @Value("${organisationDBUsername}")
    public String organisationDBUsername;

    @Value("${organisationDBPassword}")
    public String organisationDBPassword;

    @Value("${gamesDBUrl}")
    public String gamesDBUrl;

    @Value("${gamesDBUsername}")
    public String gamesDBUsername;

    @Value("${gamesDBPassword}")
    public String gamesDBPassword;

    @Bean
    @PostConstruct
    public DataSource dataSourceOrganisationDB(){
        //System.out.println(DBDriver);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/SCI-FIDICEOrganisationDataBase");
        dataSource.setUsername("postgres");
        dataSource.setPassword("21210");

        return dataSource;
    }

    @Bean
    @PostConstruct
    public DataSource dataSourceGamesDB(){
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
