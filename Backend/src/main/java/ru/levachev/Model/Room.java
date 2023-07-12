package ru.levachev.Model;

import static ru.levachev.Config.daysPerWeek;
import static ru.levachev.Config.hoursPerDay;

import java.util.Arrays;

public class Room {
    private int number;
    private Boolean[] schedule = new Boolean[hoursPerDay* daysPerWeek];

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

    public void setSchedule(Boolean[] freeDays) {
        this.schedule = freeDays;
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
