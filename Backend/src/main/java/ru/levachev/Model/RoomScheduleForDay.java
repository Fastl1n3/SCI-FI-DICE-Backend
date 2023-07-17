package ru.levachev.Model;

import java.util.ArrayList;

public class RoomScheduleForDay {
    private ArrayList<Boolean> schedule;

    public RoomScheduleForDay(ArrayList<Boolean> schedule) {
        this.schedule = schedule;
    }

    public ArrayList<Boolean> getSchedule() {
        return schedule;
    }
}
