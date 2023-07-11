package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Model.Booking;
import ru.levachev.Model.Person;
import ru.levachev.Model.Room;
import ru.levachev.Model.RoomScheduleForDay;
import ru.levachev.Mapper.RoomMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingBotDataBaseHandler implements DataBaseHandler {
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    public static final int daysPerWeek=7;
    public static final int hoursPerDay=24;

    @Autowired
    public BookingBotDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB){
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
    }

    public int book(Booking booking){
        updateRoomSchedule(booking.getRoomNumber(), booking.getDate(), booking.getBeginTime(), booking.getEndTime());
        int bookingNumber=generateBookingNumber();
        booking.setBookingNumber(bookingNumber);
        insertBooking(booking);
        return bookingNumber;
    }

    private int generateBookingNumber(){
        return 10;
    }

    private void insertBooking(Booking booking){
        jdbcTemplateOrganisationDB.update("INSERT INTO Booking VALUES(?, ?, ?, ?, ?, ?, ?)",
                booking.getPhoneNumber(), booking.getDate(), booking.getBeginTime(), booking.getEndTime(),
                booking.getBookingNumber(), booking.getRoomNumber(),
                booking.getGameID());
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

    public void updateRoomTablePerDay(){
        List<Room> list = jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper());
        for (Room room : list){
            updateRoomSchedulePerDay(room);
        }
    }

    private void updateRoomSchedulePerDay(Room room){
        Boolean[] tmpArray = room.getSchedule();
        for(int i=0;i<(daysPerWeek-1);i++) {
            for (int j = 0; j < hoursPerDay; j++) {
                tmpArray[i*hoursPerDay+j]=tmpArray[(i+1)*hoursPerDay+j];
            }
        }
        for(int i=0;i<hoursPerDay;i++) {
                tmpArray[(daysPerWeek-1)*hoursPerDay+i]=false;
        }
        room.setSchedule(tmpArray);
    }

    public ArrayList<RoomScheduleForDay> getScheduleForDate(int dayNumber){
        List<Room> list = jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper());
        ArrayList<RoomScheduleForDay> scheduleForDate = new ArrayList<RoomScheduleForDay>();
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

    public void setDefaultSchedule(){
        for(int i=0;i<5;i++) {
            Room room = new Room(i+1, null);
            addRoomToTable(room);
        }
    }

    private void addRoomToTable(Room room){
        jdbcTemplateOrganisationDB.update("INSERT INTO Room VALUES(?, ?, ?, ?)",
                room.getNumber(), room.getSchedule(), room.getPassword(),
                room.getCurrentPeopleNumber());
    }

    @Override
    public void truncateTable(String tableName){
        jdbcTemplateOrganisationDB.execute("TRUNCATE TABLE "+ tableName);
    }

    public void addUser(Person person){
        jdbcTemplateOrganisationDB.update("INSERT INTO Person(phoneNumber) VALUES(?)",
                person.getPhoneNumber());
    }

    public void markPerson(String phoneNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Person SET blackMark=? WHERE phoneNumber=?",
                true, phoneNumber);
    }

}
