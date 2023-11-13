package scifidice.model;

import org.springframework.stereotype.Component;
import scifidice.Entity.HoursPair;

import java.time.LocalDate;
import java.util.HashMap;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;

@Component
public class BookingTime {
    private final HashMap<LocalDate, boolean[]> datesMap = new HashMap<>();

    public void initDatesMap() {
        //TODO response map from database
    }

    public void addNewReserving(LocalDate date, HoursPair hoursPair) {
        boolean[] hours = datesMap.get(date);
        if (hours == null) {
            hours = new boolean[24];
        }
        for (int i = 0; i < hours.length; i++) {
            if (hoursPair.getFirstHour() <= i && i < hoursPair.getSecondHour()) {
                hours[i] = true;
            }
        }
        datesMap.put(date, hours);
    }

    public void deletePastReserving() {
        for (LocalDate date: datesMap.keySet()) {
            if (date.isBefore(LocalDate.now(NSK_ZONE_ID))) {
                datesMap.remove(date);
            }
        }
    }
}
