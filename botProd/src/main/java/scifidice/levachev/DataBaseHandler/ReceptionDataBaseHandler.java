package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import scifidice.levachev.Mapper.RoomMapper;
import scifidice.levachev.Mapper.BookingMapper;
import scifidice.levachev.Mapper.GameMapper;

import scifidice.levachev.Model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;

@Component
public class ReceptionDataBaseHandler extends DataBaseEntityAdder{
    private Booking lastBooking;
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public ReceptionDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB) {
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    public ReceptionCodeAnswer isBookingNumberValid(int bookingNumber) {
        lastBooking = jdbcTemplateOrganisationDB.query("SELECT * FROM Booking WHERE bookingNumber=?",
                        new Object[]{bookingNumber}, new BookingMapper())
                .stream().findAny().orElse(null);
        if(lastBooking == null){
            return ReceptionCodeAnswer.INVALID_ID;
        }
        if(!isWrongTime()){
            return ReceptionCodeAnswer.WRONG_DATE;
        }
        return ReceptionCodeAnswer.SUCCESS;
    }

    private boolean isWrongTime(){
        LocalDateTime beginTime = LocalDateTime.of(lastBooking.getBeginDate(),
                LocalTime.of(lastBooking.getBeginTime(), 0, 0, 0));

        LocalDateTime endTime = LocalDateTime.of(lastBooking.getEndDate(),
                LocalTime.of(lastBooking.getEndTime(), 0, 0, 0));

        LocalDateTime nowTime = LocalDateTime.now(NSK_ZONE_ID);
	
        LocalDateTime under20BeginTime = beginTime.minusMinutes(20);
	    System.out.println("under20BeginTime: " + under20BeginTime);
	    System.out.println("nowTime: " + nowTime );
	    System.out.println("endTime: " + endTime );
	    System.out.println("boolean " + !nowTime.isBefore(under20BeginTime) + " " + !nowTime.isAfter(endTime));
 

        return !nowTime.isBefore(under20BeginTime) && !nowTime.isAfter(endTime);
    }

  
    private boolean isGameIDValid(int id){
        Game game = jdbcTemplateGamesDB.query("SELECT * FROM Games WHERE id=?",
                        new Object[]{id}, new GameMapper())
                .stream().findAny().orElse(null);
        return game != null;
    }

    public ClientInformation payBooking(int gameID, int peopleNumber) {
        if(!isGameIDValid(gameID)){
            return new ClientInformation(
                    ReceptionCodeAnswer.INVALID_GAME_ID, 0, 0, 0, null
            );
        }

        if(!pay()){
            return new ClientInformation(
                    ReceptionCodeAnswer.FAILED_PAY, 0, 0, 0, null
            );
        }

        Room room = getRoom(lastBooking);

        takeGame(gameID);

        updateGameIDInBookingEntry(gameID);

        if(isLate()) {
            updateCurrentPeopleNumberByRoomNumber(lastBooking.getRoomNumber(), peopleNumber);
        } else{
            updateRoomDataByRoomNumber(lastBooking.getRoomNumber(), peopleNumber);
        }

        return new ClientInformation(ReceptionCodeAnswer.SUCCESS, lastBooking.getRoomNumber(),
                lastBooking.getBeginTime(), lastBooking.getEndTime(),
                room.getPassword());
    }

    private boolean isLate(){
        LocalDateTime beginTime = LocalDateTime.of(lastBooking.getBeginDate(),
                LocalTime.of(lastBooking.getBeginTime(), 0, 0, 0));

        LocalDateTime endTime = LocalDateTime.of(lastBooking.getEndDate(),
                LocalTime.of(lastBooking.getEndTime(), 0, 0, 0));

        LocalDateTime nowTime = LocalDateTime.now(NSK_ZONE_ID);

        return !nowTime.isBefore(beginTime);
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
        jdbcTemplateOrganisationDB.update("UPDATE bufferRoomData SET peopleNumber=?, isShouldChange=? WHERE roomNumber=?",
                peopleNumber, true, roomNumber);
    }

    private void updateCurrentPeopleNumberByRoomNumber(int roomNumber, int currentPeopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentPersonNumber=? WHERE number=?",
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
