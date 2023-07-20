package scifidice.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import static scifidice.burym.config.SpringConfig.*;

import scifidice.burym.infoBot.Notification;
import scifidice.levachev.Mapper.BookingMapper;
import scifidice.levachev.Mapper.BufferRoomDataMapper;
import scifidice.levachev.Mapper.PersonMapper;
import scifidice.levachev.Mapper.RoomMapper;
import scifidice.levachev.Model.Booking;
import scifidice.levachev.Model.BufferRoomData;
import scifidice.levachev.Model.Person;
import scifidice.levachev.Model.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.util.Optional;


@Component
@EnableScheduling
public class AutoUpdatableDataBaseHandler{
    private static List<Booking> todayEndBookingList = new ArrayList<>();
    private static List<Booking> todayBeginBookingList = new ArrayList<>();
    
    private final JdbcTemplate jdbcTemplateGamesDB;
    
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    private Notification notification;

    @Autowired
    public AutoUpdatableDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB){
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
    }

    @Scheduled(cron = "@daily")
    public void initTodayEndBookingList(){
     //   JdbcTemplate jdbcTemplate = new Config().jdbcTemplateOrganisationDB();
        todayEndBookingList = jdbcTemplateOrganisationDB.
                query("SELECT * FROM Booking WHERE endDate=?",
                        new Object[]{Date.valueOf(LocalDate.now())},
                        new BookingMapper());
    }

    @Scheduled(cron = "@daily")
    public void initTodayBeginBookingList(){
        todayBeginBookingList = jdbcTemplateOrganisationDB.
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


    public Optional<String> checkPeople(int roomNumber, int actualPeopleNumber) throws WrongRoomNumberException {
        Room room = jdbcTemplateOrganisationDB.query("SELECT * FROM Room WHERE number=?",
                        new Object[]{roomNumber}, new RoomMapper()).stream().findAny().
                orElse(null);
        if(room == null){

            throw new WrongRoomNumberException("1");
        }
        if(room.getCurrentPeopleNumber() < actualPeopleNumber) {
            String phoneNumber = getPhoneNumberByRoom(roomNumber);
            if(phoneNumber == null){
                throw new WrongRoomNumberException("2");
            }
            try {
                String infoBotChatId = getInfoBotChatIDByPhoneNumber(phoneNumber);
                return Optional.of(infoBotChatId);
            }
            catch (NullPointerException e) {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }
    private String getPhoneNumberByRoom(int roomNumber){
        for(Booking booking : todayBeginBookingList){
            int currentHour = LocalDateTime.now(NSK_ZONE_ID).getHour();
            if(booking.getRoomNumber() == roomNumber &&
            booking.getBeginTime()<=currentHour &&
                    booking.getEndTime()>=currentHour){
                return booking.getPhoneNumber();
            }
        }
        return null;
    }


    @Scheduled(cron = "@hourly")
    //@Scheduled(cron = "0 43 * * * *")
    public void deleteOverdueBooking(){
        System.out.println("deleteOverdueBooking");
        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        for(int i=0;i<todayEndBookingList.size();i++){
            Booking booking = todayEndBookingList.get(i);
            if(booking.getEndTime() <= currentTime){
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

    @Scheduled(cron = "0 45 * * * *")
    private void checkTimeOut(){
        System.out.println("checkTimeOut");
        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        for (Booking booking : todayEndBookingList) {
            if (booking.getEndTime() == (currentTime + 1) % HOURS_PER_DAY) {
                try {
                    String infoBotChatId = getInfoBotChatIDByPhoneNumber(booking.getPhoneNumber());
                    notification.sendMessageToInfoBot(infoBotChatId, NOTIFY_TIME_MESSAGE);
                }
                catch (NullPointerException e) {
                    System.out.println("INFO CHAT ID IS NOT FOUND, phone number is not valid " + e.getMessage());
                }

            }
        }
    }

    @Scheduled(cron = "0 55 * * * *")
    private void warningTimeOut(){
        System.out.println("checkTimeOut");
        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        for (Booking booking : todayEndBookingList) {
            if (booking.getEndTime() == (currentTime + 1) % HOURS_PER_DAY) {
                try {
                    String infoBotChatId = getInfoBotChatIDByPhoneNumber(booking.getPhoneNumber());
                    notification.sendMessageToInfoBot(infoBotChatId, WARNING_TIME_MESSAGE);
                }
                catch (NullPointerException e) {
                    System.out.println("INFO CHAT ID IS NOT FOUND, phone number is not valid " + e.getMessage());
                }

            }
        }
    }

    private String getInfoBotChatIDByPhoneNumber(String phoneNumber){
        Person person = jdbcTemplateOrganisationDB.query("SELECT * FROM Person WHERE phoneNumber=?",
                        new Object[]{phoneNumber}, new PersonMapper()).stream().findAny().
                orElse(null);
        if (person == null) {
            throw new NullPointerException(phoneNumber);
        }
        return person.getInfoBotChatID();
    }

    @Scheduled(cron = "@daily")
    private void updateRoomTablePerDay(){
        System.out.println("updateRoomTablePerDay");
        List<Room> list = jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper());
        for (Room room : list){
            updateRoomSchedulePerDay(room);
        }
    }

    private void updateRoomSchedulePerDay(Room room){
        Boolean[] tmpArray = room.getSchedule();
        for(int i = 0; i<(DAYS_PER_WEEK -1); i++) {
            for (int j = 0; j < HOURS_PER_DAY; j++) {
                tmpArray[i* HOURS_PER_DAY +j]=tmpArray[(i+1)* HOURS_PER_DAY +j];
            }
        }
        for(int i = 0; i< HOURS_PER_DAY; i++) {
            tmpArray[(DAYS_PER_WEEK -1)* HOURS_PER_DAY +i]=false;
        }
        room.setSchedule(tmpArray);
    }

    @Scheduled(cron = "0 5 * * * *")
    @Scheduled(cron = "@hourly")
    public void updateRoomData(){
        System.out.println("updateRoomData");
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
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentpersonnumber=? WHERE number=?",
                peopleNumber, roomNumber);
        jdbcTemplateOrganisationDB.update("UPDATE bufferRoomData SET isShouldChange=DEFAULT WHERE roomnumber=?",
                roomNumber);
    }
}
