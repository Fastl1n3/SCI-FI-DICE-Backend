package scifidice.levachev.Model;

import java.util.Arrays;
import static scifidice.burym.config.SpringConfig.DAYS_PER_WEEK;
import static scifidice.burym.config.SpringConfig.HOURS_PER_DAY;

public class Room {
    private int number;
    private Boolean[] schedule = new Boolean[HOURS_PER_DAY * DAYS_PER_WEEK];

    private String password;

    private int currentPeopleNumber;

    public Room(){
        Arrays.fill(schedule, false);
    }
    public Room(int number, String password) {
        this.number = number;
        this.password=password;
        Arrays.fill(schedule, false);
        currentPeopleNumber=0;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Boolean[] getSchedule() {
        return schedule;
    }

    public void setSchedule(Boolean[] schedule){
        this.schedule = schedule;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCurrentPeopleNumber() {
        return currentPeopleNumber;
    }

    public void setCurrentPeopleNumber(int currentPeopleNumber) {
        this.currentPeopleNumber = currentPeopleNumber;
    }
}
