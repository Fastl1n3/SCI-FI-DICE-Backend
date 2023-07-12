package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Model.Booking;
import ru.levachev.Model.Room;
import ru.levachev.Model.RoomScheduleForDay;
import ru.levachev.Mapper.RoomMapper;
import static ru.levachev.Config.hoursPerDay;
import static ru.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler.addToTodayBookingList;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingBotDataBaseHandler extends DataBaseEntityAdder {
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public BookingBotDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB){
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
    }

    public int book(Booking booking){
        updateRoomSchedule(booking.getRoomNumber(), booking.getDate(), booking.getBeginTime(), booking.getEndTime());
        int bookingNumber=generateBookingNumber();
        booking.setBookingNumber(bookingNumber);
        addBookingToTable(booking, jdbcTemplateOrganisationDB);

        if(booking.getDate()==1){
            addToTodayBookingList(booking);
        }

        return bookingNumber;
    }

    private int generateBookingNumber(){
        return 10;
    }

    public void updateRoomSchedule(int roomNumber, int dayNumber, int beginTime, int endTime){
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?", new Object[]{roomNumber}, new RoomMapper()).stream().findAny().orElse(null);
        assert room != null;
        Boolean[] tmpArray = room.getSchedule();
        for(int i=beginTime;i<=endTime;i++) {
            tmpArray[(dayNumber-1)*hoursPerDay+i] = true;
        }
        room.setSchedule(tmpArray);
        jdbcTemplateOrganisationDB.update("UPDATE Room SET schedule=? WHERE number=?", room.getSchedule(), roomNumber);
    }

    public ArrayList<RoomScheduleForDay> getScheduleForDate(int dayNumber){
        List<Room> list = jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper());
        ArrayList<RoomScheduleForDay> scheduleForDate = new ArrayList<>();
        for (Room room : list){
            scheduleForDate.add(getRoomScheduleForDayByRoom(room, dayNumber));
        }
        return scheduleForDate;
    }

    private RoomScheduleForDay getRoomScheduleForDayByRoom(Room room, int dayNumber){
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for(int i=0;i<hoursPerDay;i++){
            arrayList.add(room.getSchedule()[(dayNumber-1)*hoursPerDay+i]);
        }
        return new RoomScheduleForDay(arrayList);
    }
}
