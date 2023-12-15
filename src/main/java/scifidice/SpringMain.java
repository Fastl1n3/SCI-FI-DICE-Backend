package scifidice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import scifidice.db.dataBaseHandler.AutoUpdatableDataBaseHandler;
import scifidice.db.dataBaseHandler.InitializationDBHandler;
import scifidice.model.BookingTime;

import java.io.IOException;

@SpringBootApplication
public class SpringMain extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(SpringMain.class, args);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        AutoUpdatableDataBaseHandler autoUpdatableDataBaseHandler = context.getBean(AutoUpdatableDataBaseHandler.class);
        InitializationDBHandler initializationDBHandler = context.getBean(InitializationDBHandler.class);
        context.getBean(BookingTime.class).initDatesMap();
        initializationDBHandler.defaultInitialization();
    }
}
