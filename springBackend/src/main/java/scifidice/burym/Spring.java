package scifidice.burym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler;

import java.io.IOException;

@SpringBootApplication
public class Spring extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(Spring.class, args);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        AutoUpdatableDataBaseHandler autoUpdatableDataBaseHandler = context.getBean(AutoUpdatableDataBaseHandler.class);
        autoUpdatableDataBaseHandler.initTodayBeginBookingList();
        autoUpdatableDataBaseHandler.initTodayEndBookingList();
    }
}
