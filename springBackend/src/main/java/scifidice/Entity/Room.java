package scifidice.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import static scifidice.config.SpringConfig.DAYS_PER_WEEK;
import static scifidice.config.SpringConfig.HOURS_PER_DAY;
@Getter
@Setter
public class Room {
    private int number;
    private Boolean[] schedule = new Boolean[HOURS_PER_DAY * DAYS_PER_WEEK];

    private String password;

    private int currentPersonNumber;

    private int maxPersonNumber;

    public Room(){
        Arrays.fill(schedule, false);
    }
    public Room(int number, String password,int currentPersonNumber, int maxPersonNumber) {
        this.number = number;
        this.password=password;
        Arrays.fill(schedule, false);
        this.currentPersonNumber=currentPersonNumber;
        this.maxPersonNumber=maxPersonNumber;
    }
}
