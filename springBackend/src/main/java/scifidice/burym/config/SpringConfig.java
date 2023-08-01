package scifidice.burym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Configuration
@ComponentScan("scifidice")
@PropertySource("classpath:dataBase.properties")
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    @Autowired
    public SpringConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    public static final int DAYS_PER_WEEK = 7;
    public static final int HOURS_PER_DAY =24;

    public static final ZoneId NSK_ZONE_ID = ZoneId.of("GMT+7");
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    public static final String NOTIFY_TIME_MESSAGE = "У вас осталось 10 минут, пожалуйста по истечению времени покиньте комнату "
            + "и положите на место игры. Надеемся, вам все понравилось, приходите еще!";

    public static final String NOTIFY_MANY_PEOPLE = "В комнате больше человек, чем было заявлено. Во избежание проблем,"
            + "доплатите на ресепшене за новых гостей.";

    public static final String WARNING_TIME_MESSAGE = "Ваше время вышло!";

    @Value("${DBDriver}")
    private String DBDriver;

    @Value("${organisationDBUrl}")
    private String organisationDBUrl;

    @Value("${organisationDBUsername}")
    private String organisationDBUsername;

    @Value("${organisationDBPassword}")
    private String organisationDBPassword;

    @Value("${gamesDBUrl}")
    private String gamesDBUrl;

    @Value("${gamesDBUsername}")
    private String gamesDBUsername;

    @Value("${gamesDBPassword}")
    private String gamesDBPassword;

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("512KB"));
        factory.setMaxRequestSize(DataSize.parse("512KB"));
        return factory.createMultipartConfig();
    }

    @Bean
    public DataSource dataSourceOrganisationDB(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DBDriver);
        dataSource.setUrl(organisationDBUrl);
        dataSource.setUsername(organisationDBUsername);
        dataSource.setPassword(organisationDBPassword);
        return dataSource;
    }

    @Bean
    public DataSource dataSourceGamesDB(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DBDriver);
        dataSource.setUrl(gamesDBUrl);
        dataSource.setUsername(gamesDBUsername);
        dataSource.setPassword(gamesDBPassword);
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