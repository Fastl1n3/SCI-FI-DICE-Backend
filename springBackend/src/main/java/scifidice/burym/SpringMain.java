package scifidice.burym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler;

@SpringBootApplication
public class SpringMain extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringMain.class, args);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        AutoUpdatableDataBaseHandler autoUpdatableDataBaseHandler = context.getBean(AutoUpdatableDataBaseHandler.class);
        autoUpdatableDataBaseHandler.initTodayBeginBookingList();
        autoUpdatableDataBaseHandler.initTodayEndBookingList();
        autoUpdatableDataBaseHandler.deleteOldBooking();
    }
}
