package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.BookingMapper;
import ru.levachev.Mapper.BufferRoomDataMapper;
import ru.levachev.Mapper.PersonMapper;
import ru.levachev.Mapper.RoomMapper;
import ru.levachev.Model.Booking;
import ru.levachev.Model.BufferRoomData;
import ru.levachev.Model.Person;
import ru.levachev.Model.Room;
import ru.levachev.Config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

import static ru.levachev.Config.*;


@Component
public class AutoUpdatableDataBaseHandler{
    private static List<Booking> todayEndBookingList;
    private static List<Booking> todayBeginBookingList;
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public AutoUpdatableDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public static void initTodayEndBookingList(){
        JdbcTemplate jdbcTemplate = new Config().jdbcTemplateOrganisationDB();
        todayEndBookingList = jdbcTemplate.
                query("SELECT * FROM Booking WHERE endDate=?",
                        new Object[]{Date.valueOf(LocalDate.now())},
                        new BookingMapper());
    }

    public static void initTodayBeginBookingList(){
        JdbcTemplate jdbcTemplate = new Config().jdbcTemplateOrganisationDB();
        todayBeginBookingList = jdbcTemplate.
                query("SELECT * FROM Booking WHERE beginDate=?",
                        new Object[]{Date.valueOf(LocalDate.now())},
                        new BookingMapper());
    }

    public static void addToTodayEndBookingList(Booking booking){
        todayEndBookingList.add(booking);
    }
    public static void addToTodayBeginBookingList(Booking booking){
        todayBeginBookingList.add(booking);
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
        initTodayEndBookingList();
        initTodayBeginBookingList();
    }

    public String checkPeople(int roomNumber, int actualPeopleNumber){
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).stream().findAny().
                orElse(null);
        if(room == null){
            return null;
        }
        if(room.getCurrentPeopleNumber() < actualPeopleNumber) {
            String phoneNumber = getPhoneNumberByRoom(roomNumber);
            if(phoneNumber == null){
                return null;/////////////////////////////////////////
            }
            return getInfoBotChatIDByPhoneNumber(phoneNumber);
        } else {
            return null;
        }
    }

    private String getPhoneNumberByRoom(int roomNumber){
        for(Booking booking : todayBeginBookingList){
            int currentHour = LocalDateTime.now().getHour();
            if(booking.getRoomNumber() == roomNumber &&
            booking.getBeginTime()<=currentHour &&
                    booking.getEndTime()>=currentHour){
                return booking.getPhoneNumber();
            }
        }
        return null;
    }


    private void deleteOverdueBooking(){
        int currentTime = LocalDateTime.now(NSKZoneId).getHour();
        for(Booking booking : todayEndBookingList){
            if(booking.getEndTime() == currentTime){
                deleteBooking(booking);
            }
        }
    }

    private void deleteBooking(Booking booking){
        jdbcTemplateOrganisationDB.update("DELETE FROM Booking WHERE bookingNumber=?",
                booking.getBookingNumber());
        todayEndBookingList.remove(booking);
        jdbcTemplateGamesDB.update("UPDATE Games SET isTaken=? WHERE id=?",
                false, booking.getGameID());
    }

    private ArrayList<String> checkTimeOut(){
        ArrayList<String> goHome = new ArrayList<>();

        int currentTime = LocalDateTime.now(NSKZoneId).getHour();

        for(Booking booking : todayEndBookingList){
            if(booking.getEndTime() == (currentTime+1)%hoursPerDay){
                goHome.add(
                        getInfoBotChatIDByPhoneNumber(booking.getPhoneNumber())
                );
            }
        }
        return goHome;
    }

    private String getInfoBotChatIDByPhoneNumber(String phoneNumber){
        Person person = jdbcTemplateOrganisationDB.query("SELECT * FROM Person WHERE phoneNumber=?",
                        new Object[]{phoneNumber}, new PersonMapper()).stream().findAny().
                orElse(null);
        if(person == null){
            return null;
        }
        return person.getInfoBotChatID();
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
        List<BufferRoomData> list =
                jdbcTemplateOrganisationDB.
                        query("SELECT * FROM bufferRoomData WHERE isShouldChange=?",
                                new Object[]{true}, new BufferRoomDataMapper());
        for (BufferRoomData bufferRoomData : list) {
            updateDataByRoom(bufferRoomData.getRoomNumber(),
                    bufferRoomData.getPeopleNumber());
        }
    }

    private void updateDataByRoom(int roomNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentPeopleNumber=? WHERE number=?",
                peopleNumber, roomNumber);
        jdbcTemplateOrganisationDB.update("UPDATE bufferedRoomData SET isShouldChange=DEFAULT WHERE number=?",
                roomNumber);
    }
}
