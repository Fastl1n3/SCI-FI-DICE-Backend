package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import scifidice.db.dao.BookingAndGamesDao;
import scifidice.db.dao.BookingDao;
import scifidice.db.dao.GameDao;
import scifidice.db.dao.PersonDao;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Game;
import scifidice.db.entities.Person;
import scifidice.infoBot.Notification;
import scifidice.Entity.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static scifidice.config.SpringConfig.*;


@Component
@EnableScheduling
public class AutoUpdatableDataBaseHandler{
    private static List<Booking> todayBeginBookingList = new ArrayList<>();

    private final JdbcTemplate jdbcTemplateGamesDB;

    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    private InfoSender infoSender;

    @Autowired
    private Notification notification;

    private final BookingDao bookingDao;

    private final GameDao gameDao;

    private final BookingAndGamesDao bookingAndGamesDao;

    private final PersonDao personDao;

    @Autowired
    public AutoUpdatableDataBaseHandler(JdbcTemplate jdbcTemplateOrganisationDB, JdbcTemplate jdbcTemplateGamesDB, BookingDao bookingDao, GameDao gameDao, BookingAndGamesDao bookingAndGamesDao, PersonDao personDao){
        this.jdbcTemplateOrganisationDB = jdbcTemplateOrganisationDB;
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
        this.bookingDao = bookingDao;
        this.gameDao = gameDao;
        this.bookingAndGamesDao = bookingAndGamesDao;
        this.personDao = personDao;
    }

    @Scheduled(cron = "0 1 0 * * *")
    public void initTodayBeginBookingList(){
        todayBeginBookingList = bookingDao.getAllByDate(Date.valueOf(LocalDate.now(NSK_ZONE_ID)));
    }

    @Scheduled(cron = "@daily")
    public void deleteOldBooking() {
        List<Booking> allBooking = bookingDao.getAll();

        int size = allBooking.size();

        for(int i = size - 1; i >= 0; i--) {
            Booking booking = allBooking.get(i);
            if(booking.getEndDate().isBefore(LocalDate.now(NSK_ZONE_ID))){
                deleteBooking(booking);
            }
        }
    }

    static void addToTodayBeginBookingList(Booking booking){
        todayBeginBookingList.add(booking);
    }

    static List<Booking> getTodayBeginBookingList() {
        return todayBeginBookingList;
    }

    public Optional<CheckPeopleInformation> checkPeople(int roomNumber, int actualPeopleNumber, LocalDateTime takePictureTime) throws WrongRoomNumberException {
        Booking actualBooking = bookingDao.getByRoomNumber(roomNumber);
        if(actualBooking == null){
            throw new WrongRoomNumberException("room is null");
        }

        for (Booking booking : todayBeginBookingList) {
            LocalDate endDate = booking.getEndDate();
            int endTime = booking.getEndTime();
            if(booking.getEndTime() == 24){
                endTime = 0;
                endDate = endDate.plusDays(1);
            }

            LocalDateTime bookingEndDateTime = LocalDateTime.of(endDate, LocalTime.of(endTime, 0, 0));
            LocalDateTime bookingBeginDateTime = LocalDateTime.of(booking.getBeginDate(), LocalTime.of(booking.getBeginTime()%HOURS_PER_DAY, 0, 0));

            long diff = MINUTES.between(takePictureTime, bookingEndDateTime);

            if(!takePictureTime.isBefore(bookingBeginDateTime) &&
                    takePictureTime.isBefore(bookingEndDateTime) && booking.getRoomNumber() == roomNumber){

                CheckPeopleInformation checkPeopleInformation = new CheckPeopleInformation();

                String phoneNumber = getPhoneNumberByRoom(roomNumber);
                if (phoneNumber == null) {
                    throw new WrongRoomNumberException("phone number is null");
                }
                try {
                    String infoBotChatId = getInfoBotChatIDByPhoneNumber(phoneNumber);
                    checkPeopleInformation.setInfoBotChatID(infoBotChatId);
                    checkPeopleInformation.setPhoneNumber(phoneNumber);
                } catch (NullPointerException e) {
                    return Optional.empty();
                }

                checkPeopleInformation.setViolate(actualPeopleNumber > actualBooking.getCurrentPeopleNumber() && booking.isPaid());
                checkPeopleInformation.setInExitWindow(diff < 5);

                return Optional.of(checkPeopleInformation);
            }
        }
        return Optional.empty();
    }

    private String getPhoneNumberByRoom(int roomNumber){
        for(Booking booking : todayBeginBookingList){
            int currentHour = LocalDateTime.now(NSK_ZONE_ID).getHour();
            if(booking.getRoomNumber() == roomNumber &&
                    booking.getBeginTime() <= currentHour &&
                    booking.getEndTime() > currentHour) {
                return booking.getPhoneNumber();
            }
        }
        return null;
    }

    @Scheduled(cron = "0 3 * * * *")
    public void deleteOverdueBooking(){
        System.out.println("deleteOverdueBooking");

        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        int size = todayBeginBookingList.size();

        for(int i = size - 1; i >= 0; i--){
            Booking booking = todayBeginBookingList.get(i);
            if(booking.getEndTime() <= currentTime){
                deleteBooking(booking);
            }
        }
    }

    private void deleteBooking(Booking booking){
        bookingDao.deleteByBooking(booking);
        todayBeginBookingList.remove(booking);
        List<Game> gamesToChange = bookingAndGamesDao.getGamesByBooking(booking);
        for(Game game: gamesToChange) {
            gameDao.updateIsTakenByGame(game);
        }
        bookingAndGamesDao.deleteGamesByBookingId(booking.getBookingNumber());
    }

    @Scheduled(cron = "0 45 * * * *")
    private void checkTimeOut(){
        System.out.println("checkTimeOut");
        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        for (Booking booking : todayBeginBookingList) {
            if (booking.getEndTime() == (currentTime + 1) && booking.isPaid()) {
                try {
                    String infoBotChatId = getInfoBotChatIDByPhoneNumber(booking.getPhoneNumber());
                    notification.sendMessageToInfoBot(infoBotChatId, NOTIFY_TIME_MESSAGE);
                }
                catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @Scheduled(cron = "0 55 * * * *")
    private void warningTimeOut(){
        System.out.println("warningTimeOut");
        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        for (Booking booking : todayBeginBookingList) {
            if (booking.getEndTime() == (currentTime + 1) && booking.isPaid()) {
                try {
                    setCurrentPeopleNumberByRoomNumber(booking.getRoomNumber());
                    infoSender.sendToAdminRoomInfo(booking.getRoomNumber(), getTodayBeginBookingList());
                }
                catch (NullPointerException | WrongRoomNumberException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void setCurrentPeopleNumberByRoomNumber(int roomNumber){
        bookingDao.setZeroPeopleByRoomNumber(roomNumber);
    }

    private String getInfoBotChatIDByPhoneNumber(String phoneNumber){
        Person person = personDao.getPersonByPhoneNumber(phoneNumber);
        if (person == null) {
            throw new NullPointerException("PERSON WITH THIS PHONENUMBER NOT EXIST, phonenumber : " + phoneNumber);
        }
        if(person.getInfoBotChatID() == null){
            throw new NullPointerException("PERSON WITH THIS PHONENUMBER HAVE NOT INFO CHAT ID, phonenumber : " + phoneNumber);
        }
        return person.getInfoBotChatID();
    }

    /*@Scheduled(cron = "@daily")                        тоже до лучших
    private void updateRoomTablePerDay(){
        System.out.println("updateRoomTablePerDay");
        List<Room> list = jdbcTemplateOrganisationDB.query("SELECT * FROM Room", new RoomMapper());
        for (Room room : list){
            updateRoomSchedulePerDay(room);
        }
    }*/

    /*private void updateRoomSchedulePerDay(Room room){       до лучших времен
        Boolean[] tmpArray = room.getSchedule();
        for(int i = 0; i < DAYS_PER_WEEK - 1; i++) {
            for (int j = 0; j < HOURS_PER_DAY; j++) {
                tmpArray[i * HOURS_PER_DAY + j] = tmpArray[(i + 1) * HOURS_PER_DAY + j];
            }
        }
        for(int i = 0; i < HOURS_PER_DAY; i++) {
            tmpArray[(DAYS_PER_WEEK - 1) * HOURS_PER_DAY + i]=false;
        }
        room.setSchedule(tmpArray);

        jdbcTemplateOrganisationDB.update("UPDATE Room SET schedule=? WHERE number=?",
                room.getSchedule(), room.getNumber());
    }*/

    /*@Scheduled(cron = "0 5 * * * *")
    @Scheduled(cron = "@hourly")
    public void updateRoomData() {
        System.out.println("updateRoomData");
        List<BufferRoomData> list =
                jdbcTemplateOrganisationDB.
                        query("SELECT * FROM bufferRoomData WHERE isShouldChange=?",
                                new Object[]{true}, new BufferRoomDataMapper());

        for (BufferRoomData bufferRoomData : list) {
            updateDataByRoom(bufferRoomData.getRoomNumber(),
                    bufferRoomData.getPeopleNumber());
            try {
                infoSender.sendToAdminRoomInfo(bufferRoomData.getRoomNumber(), getTodayBeginBookingList());
            } catch (WrongRoomNumberException e) {
                System.out.println(e.getMessage());
            }
        }
    }*/

    /*private void updateDataByRoom(int roomNumber, int peopleNumber){
        jdbcTemplateOrganisationDB.update("UPDATE Room SET currentpersonnumber=? WHERE number=?",
                peopleNumber, roomNumber);
        jdbcTemplateOrganisationDB.update("UPDATE bufferRoomData SET isShouldChange=DEFAULT WHERE roomnumber=?",
                roomNumber);
    }*/
}
