package scifidice.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

import static scifidice.config.SpringConfig.DAYS_PER_WEEK;
import static scifidice.config.SpringConfig.HOURS_PER_DAY;

@Getter
@Setter
@NoArgsConstructor
public class Room {
    private int number;
    private String password;
    private int maxPeopleNumber;

    public Room(int number, String password, int maxPeopleNumber) {
        this.number = number;
        this.password = password;
        this.maxPeopleNumber = maxPeopleNumber;
    }
}
