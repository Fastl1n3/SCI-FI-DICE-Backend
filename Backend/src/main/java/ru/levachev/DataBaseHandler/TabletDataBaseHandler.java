package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.levachev.Mapper.RoomMapper;
import ru.levachev.Model.Booking;
import ru.levachev.Model.ClientInformation;
import ru.levachev.Model.Room;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class TabletDataBaseHandler implements DataBaseHandler{
    private Map<Integer, Integer> roomData;
    private final JdbcTemplate jdbcTemplateOrganisationDB;
    private int lastPersonBookingNumber;

    @Autowired
    public TabletDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB) {
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
        roomData = new HashMap<>();
    }

    public boolean isBookingNumberValid(int bookingNumber) {
        lastPersonBookingNumber = bookingNumber;
        Booking booking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{bookingNumber}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
        return booking != null;
    }

    public ClientInformation payBooking(int gameID, int numberOfPerson) {
        jdbcTemplateOrganisationDB.update("UPDATE Booking SET gameID=? WHERE bookingNumber=?", gameID, lastPersonBookingNumber);

        Booking booking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{lastPersonBookingNumber}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
        if (booking == null) {
            return null;
        }

        roomData.put(booking.getBookingNumber(), numberOfPerson);

        if(!pay()){
            return null;
        }

        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                new Object[]{booking.getBookingNumber()}, new RoomMapper()).
                stream().findAny().orElse(null);
        if(room == null){
            return null;
        }

        return new ClientInformation(booking.getRoomNumber(),
                booking.getBeginTime(), booking.getEndTime(),
                room.getPassword());
    }

    private boolean pay(){
        return true;
    }


    @Override
    public void truncateTable(String tableName){
        jdbcTemplateOrganisationDB.execute("TRUNCATE TABLE "+ tableName);
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

    public ArrayList<String> checkTimeOutByRoom(int roomNumber){
        ArrayList<String> phoneNumbers = new ArrayList<>();
        List<Booking> bookingList = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingDate=? ",
                new Object[]{1}, new BeanPropertyRowMapper<>());
        int currentTime = LocalDateTime.now(ZoneId.of("GMT+7")).getHour();
        for(Booking booking : bookingList){
            if(booking.getEndTime() == (currentTime+1)){
                phoneNumbers.add(booking.getPhoneNumber());
            }
        }
        return phoneNumbers;
    }

    public void updateRoomData(){
        for (Map.Entry<Integer, Integer> entry : roomData.entrySet()) {
            updateDataByRoom(entry.getKey(), entry.getValue());
        }
    }

    private void updateDataByRoom(int roomNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentPeopleNumber=? WHERE number=?",
                peopleNumber, roomNumber);
    }
}
