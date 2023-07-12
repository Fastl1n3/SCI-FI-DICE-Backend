package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.BookingMapper;
import ru.levachev.Mapper.RoomMapper;
import ru.levachev.Model.Booking;
import ru.levachev.Model.BufferedRoomData;
import ru.levachev.Model.Room;
import ru.levachev.Config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.levachev.Config.*;


@Component
public class AutoUpdatableDataBaseHandler{
    private static List<Booking> todayBookingList;

    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public AutoUpdatableDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB) {
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
    }

    public static void initTodayBookingList(){
        JdbcTemplate jdbcTemplate = new Config().jdbcTemplateOrganisationDB();
        todayBookingList = jdbcTemplate.
                query("SELECT * FROM Booking WHERE date=?",
                        new Object[]{1}, new BookingMapper());
    }

    public static void addToTodayBookingList(Booking booking){
        todayBookingList.add(booking);
    }

    public ArrayList<String> everyFifteenMin(){
        return checkTimeOut();
    }

    public void everyHour(){
        updateRoomData();
        deleteOverdueBooking();
    }

    public void everyDay(){
        updateRoomTablePerDay();
        initTodayBookingList();
    }

    public boolean checkPeople(int roomNumber, int actualPeopleNumber){
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).stream().findAny().
                orElse(null);
        if(room == null){
            return false;
        }
        return room.getCurrentPeopleNumber() == actualPeopleNumber;
    }


    private void deleteOverdueBooking(){
        int currentTime = LocalDateTime.now(NSKZoneId).getHour();
        for(Booking booking : todayBookingList){
            if(booking.getEndTime() == currentTime){
                deleteBooking(booking);
            }
        }
    }

    private void deleteBooking(Booking booking){
        jdbcTemplateOrganisationDB.update("DELETE FROM Booking WHERE bookingNumber=?",
                booking.getBookingNumber());
        todayBookingList.remove(booking);
    }

    private ArrayList<String> checkTimeOut(){
        ArrayList<String> goHome = new ArrayList<>();

        int currentTime = LocalDateTime.now(NSKZoneId).getHour();

        for(Booking booking : todayBookingList){
            if(booking.getEndTime() == (currentTime+1)){
                goHome.add(booking.getPhoneNumber());
            }
        }
        return goHome;
    }

    private void updateRoomTablePerDay(){
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

    public void updateRoomData(){
        List<BufferedRoomData> list =
                jdbcTemplateOrganisationDB.
                        query("SELECT * FROM bufferedRoomData WHERE isShouldChange=?",
                                new Object[]{true}, new BeanPropertyRowMapper<>());
        for (BufferedRoomData bufferedRoomData : list) {
            updateDataByRoom(bufferedRoomData.getRoomNumber(),
                    bufferedRoomData.getPeopleNumber());
        }
    }

    private void updateDataByRoom(int roomNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentPeopleNumber=? WHERE number=?",
                peopleNumber, roomNumber);
        jdbcTemplateOrganisationDB.update("UPDATE bufferedRoomData SET isShouldChange=DEFAULT WHERE number=?",
                roomNumber);
    }
}
