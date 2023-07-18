package scifidice.burym.bookingBot;

import org.springframework.stereotype.Component;
import scifidice.levachev.Model.RoomScheduleForDay;

import java.util.ArrayList;

@Component
public class Handler {
    public StringBuilder dateHandler(ArrayList<RoomScheduleForDay> rooms) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (RoomScheduleForDay room: rooms) {
            stringBuilder.append("ROOM #").append(i).append("\n");
            int begin = 0;
            int end = 1;
            for (Boolean hour: room.getSchedule()) {
                if (!hour) {
                    stringBuilder.append(begin).append("-").append(end).append(", ");
                }
                begin++;
                end++;
            }
            stringBuilder.append("\n");
            i++;
        }
        return stringBuilder;
    }
}
