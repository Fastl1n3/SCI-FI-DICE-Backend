package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.levachev.Mapper.RoomMapper;
import scifidice.levachev.Model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ReceptionDataBaseHandler extends DataBaseEntityAdder{
    private Booking lastBooking;
    private boolean isLate;
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public ReceptionDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public ReceptionCodeAnswer isBookingNumberValid(int bookingNumber) {
        lastBooking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{bookingNumber}, new BeanPropertyRowMapper<>(Booking.class))
                .stream().findAny().orElse(null);
        if(lastBooking == null){
            return ReceptionCodeAnswer.INVALID_ID;
        }
        if(!isWrongTime()){
            return ReceptionCodeAnswer.WRONG_DATE;
        }
        return ReceptionCodeAnswer.SUCCESS;
    }

    private boolean isAfter(LocalDate date, int hour){
        LocalDateTime localDateTime = LocalDateTime.of(date,
                LocalTime.of(hour, 0, 0, 0));
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        return localDateTime.isAfter(localDateTimeNow);
    }

    private boolean isBefore(LocalDate date, int hour){
        LocalDateTime localDateTime = LocalDateTime.of(date,
                LocalTime.of(hour, 0, 0, 0));
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        return localDateTime.isBefore(localDateTimeNow);
    }

    private boolean isWrongTime(){
        LocalDateTime beginTime = LocalDateTime.of(lastBooking.getBeginDate(),
                LocalTime.of(lastBooking.getBeginTime(), 0, 0, 0));

        LocalDateTime endTime = LocalDateTime.of(lastBooking.getEndDate(),
                LocalTime.of(lastBooking.getEndTime(), 0, 0, 0));

        LocalDateTime nowTime = LocalDateTime.now();

        LocalDateTime under20BeginTime = beginTime.minusMinutes(20);

        if(nowTime.isBefore(under20BeginTime) || nowTime.isAfter(endTime)){
            return false;
        } else if(nowTime.isBefore(beginTime)){
            isLate = false;
            return true;
        } else {
            isLate = true;
            return true;
        }
    }

    /*private boolean isWrongTime(){
        if(lastBooking.getBeginTime() == 0){
            if(!lastBooking.getBeginDate().equals(LocalDate.now())){
                if(dayNumberRelativeToToday(lastBooking.getBeginDate()) == 1){
                    return isInTimeWindow();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else{
            if(lastBooking.getBeginDate().equals(LocalDate.now())){
                return isInTimeWindow();
            } else {
                return false;
            }
        }
    }

    private boolean isInTimeWindow(){
        int currentHour = LocalDateTime.now(NSKZoneId).getHour();
        int currentMinute = LocalDateTime.now(NSKZoneId).getMinute();

        if(currentHour == lastBooking.getBeginTime()){
            return currentHour <= 20;
        } else if((currentHour+1)%hoursPerDay == lastBooking.getBeginTime()){
            return currentMinute >= 40;
        }
        return false;
    }*/

    public ClientInformation payBooking(int gameID, int peopleNumber) {
        Room room = getRoom(lastBooking);
        if(room == null){
            return null;
        }

        takeGame(gameID);

        updateGameIDInBookingEntry(gameID);

        if(isLate) {
            updateCurrentPeopleNumberByRoomNumber(lastBooking.getRoomNumber(), peopleNumber);
        } else{
            updateRoomDataByRoomNumber(lastBooking.getRoomNumber(), peopleNumber);
        }

        if(!pay()){
            return null;
        }

        return new ClientInformation(lastBooking.getRoomNumber(),
                lastBooking.getBeginTime(), lastBooking.getEndTime(),
                room.getPassword());
    }

    private Room getRoom(Booking booking){
        return jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{booking.getRoomNumber()}, new RoomMapper()).
                stream().findAny().orElse(null);
    }

    private void updateGameIDInBookingEntry(int gameID){
        jdbcTemplateOrganisationDB.update("UPDATE Booking SET gameID=? WHERE bookingNumber=?",
                gameID, lastBooking.getBookingNumber());
    }

    private void updateRoomDataByRoomNumber(int roomNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE bufferRoomData SET peopleNumber=?, \"isShouldChange\"=? WHERE roomNumber=?",
                peopleNumber, true, roomNumber);
    }

    private void updateCurrentPeopleNumberByRoomNumber(int roomNumber, int currentPeopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentPeopleNumber=? WHERE number=?",
                currentPeopleNumber, roomNumber);
    }

    private void takeGame(int gameID){
        jdbcTemplateGamesDB.update("UPDATE Games SET isTaken=? WHERE id=?",
                true, gameID);
    }

    private boolean pay(){
        return true;
    }
}
