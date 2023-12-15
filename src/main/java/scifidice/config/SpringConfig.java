package scifidice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import scifidice.service.BookingTime;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

@Configuration
@ComponentScan("scifidice")
@PropertySource("classpath:dataBase.properties")
@PropertySource("classpath:business.properties")
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;
    private final Environment environment;

    @Autowired
    public SpringConfig(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
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
        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("DBDriver")));
        dataSource.setUrl(environment.getProperty("organisationDBUrl"));
        dataSource.setUsername(environment.getProperty("organisationDBUsername"));
        dataSource.setPassword(environment.getProperty("organisationDBPassword"));
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplateOrganisationDB(){
        return new JdbcTemplate(dataSourceOrganisationDB());
    }

    @Bean
    public BookingTime bookingTime() {
        return new BookingTime(Integer.parseInt(Objects.requireNonNull(environment.getProperty("maxRooms"))));
    }
}