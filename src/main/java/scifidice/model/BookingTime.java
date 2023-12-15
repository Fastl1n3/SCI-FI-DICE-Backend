package scifidice.model;

import org.springframework.beans.factory.annotation.Autowired;
import scifidice.Entity.HoursPair;
import scifidice.db.dao.BookingDao;
import scifidice.db.entities.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;


public class BookingTime {
    private final ArrayList<HashMap<LocalDate, boolean[]>> datesMap = new ArrayList<>();

    @Autowired
    private BookingDao bookingDao;

    public BookingTime(int maxRooms) {
        for (int i = 0; i < maxRooms; i++) {
            datesMap.add(new HashMap<>());
        }
    }

    public void initDatesMap() {
        List<Booking> bookingList = bookingDao.getAll();
        for (Booking booking : bookingList) {
            int room = booking.getRoomNumber();
            int beginTime = booking.getBeginTime();
            int endTime = booking.getEndTime();
            LocalDate date = booking.getBeginDate();
            HashMap<LocalDate, boolean[]> timeMap = datesMap.get(room - 1);
            boolean[] hours = timeMap.get(date);
            if (hours == null) {
                hours = new boolean[24];
            }
            for (int i = beginTime; i < endTime; i++) {
                hours[i] = true;
            }
            timeMap.put(date, hours);
        }
    }

    public int addNewReserving(LocalDate date, HoursPair hoursPair, int room) {
        room--;
        boolean[] hours = datesMap.get(room).get(date);
        if (hours == null) {
            hours = new boolean[24];
        }
        if (!check(hours, hoursPair)) {
            return -1;
        }
        for (int i = hoursPair.getFirstHour(); i < hoursPair.getSecondHour(); i++) {
            hours[i] = true;
        }
        datesMap.get(room).put(date, hours);
        return 0;
    }

    private boolean check(boolean[] hours, HoursPair hoursPair) {
        for (int i = hoursPair.getFirstHour(); i < hoursPair.getSecondHour(); i++) {
            if (hours[i]) {
                return false;
            }
        }
        return true;
    }

    public void deletePastReserving() {
        LocalDate current = LocalDate.now(NSK_ZONE_ID);
        for (HashMap<LocalDate, boolean[]> roomMap : datesMap) {
            for (LocalDate date: roomMap.keySet()) {
                if (date.isBefore(current)) {
                    roomMap.remove(date);
                }
            }
        }
    }

    public ArrayList<HoursPair> getFreeTimeByDate(LocalDate date, int roomNumber) {
        HashMap<LocalDate, boolean[]> schedule = datesMap.get(roomNumber);
        ArrayList<HoursPair> hoursPairs = new ArrayList<>();
        boolean[] time =  schedule.get(date);
        if (time == null) {
            hoursPairs.add(new HoursPair(0, 24));
            return hoursPairs;
        }
        boolean isOnStreak = false;
        int firstHour = 0;
        int secondHour = 0;
        int nowHours = LocalTime.now(NSK_ZONE_ID).getHour();
        for (int i = 0; i < 24; i++) {
            if (i < nowHours && date.isEqual(LocalDate.now(NSK_ZONE_ID))) {
                continue;
            }
            if (!time[i]) {
                secondHour = i + 1;
                if (!isOnStreak) {
                    isOnStreak = true;
                    firstHour = i;
                }
            } else {
                if (isOnStreak) {
                    hoursPairs.add(new HoursPair(firstHour, secondHour));
                    isOnStreak = false;
                }
            }
        }
        if (isOnStreak) {
            hoursPairs.add(new HoursPair(firstHour, secondHour));
        }
        return hoursPairs;
    }
}
