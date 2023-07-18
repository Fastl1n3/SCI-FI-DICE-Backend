package scifidice.burym.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.ZoneId;

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

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        registry.viewResolver(resolver);
    }

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
  //  @PostConstruct
    public DataSource dataSourceOrganisationDB(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(DBDriver);
        dataSource.setUrl(organisationDBUrl);
        dataSource.setUsername(organisationDBUsername);
        dataSource.setPassword(organisationDBPassword);

        return dataSource;
    }

    @Bean
 //   @PostConstruct
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