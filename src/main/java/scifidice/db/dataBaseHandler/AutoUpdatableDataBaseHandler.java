package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
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
import scifidice.model.CheckPeopleInformation;
import scifidice.service.BookingTime;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static scifidice.config.SpringConfig.*;


@Component
@EnableScheduling
public class AutoUpdatableDataBaseHandler{

    private final InfoSender infoSender;

    private final Notification notification;

    private final BookingDao bookingDao;

    private final GameDao gameDao;

    private final BookingAndGamesDao bookingAndGamesDao;

    private final PersonDao personDao;

    private final BookingTime bookingTime;

    @Autowired
    public AutoUpdatableDataBaseHandler(BookingDao bookingDao, GameDao gameDao, BookingAndGamesDao bookingAndGamesDao, PersonDao personDao, InfoSender infoSender, Notification notification, BookingTime bookingTime){
        this.bookingDao = bookingDao;
        this.gameDao = gameDao;
        this.bookingAndGamesDao = bookingAndGamesDao;
        this.personDao = personDao;
        this.infoSender = infoSender;
        this.notification = notification;
        this.bookingTime = bookingTime;
    }


    public Optional<CheckPeopleInformation> checkPeople(int roomNumber, int actualPeopleNumber, LocalDateTime takePictureTime) throws WrongRoomNumberException {
        Booking actualBooking = bookingDao.getBookingByRoomAndDate(roomNumber, takePictureTime);
        if(actualBooking == null){
            throw new WrongRoomNumberException("room is not Valid");
        }

        LocalDate endDate = actualBooking.getEndDate();
        int endTime = actualBooking.getEndTime();
        if(actualBooking.getEndTime() == 24){
            endTime = 0;
            endDate = endDate.plusDays(1);
        }

        LocalDateTime bookingEndDateTime = LocalDateTime.of(endDate, LocalTime.of(endTime, 0, 0));
        long diff = MINUTES.between(takePictureTime, bookingEndDateTime);

        CheckPeopleInformation checkPeopleInformation = new CheckPeopleInformation();

        String phoneNumber = actualBooking.getPhoneNumber();
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

        checkPeopleInformation.setViolate(actualPeopleNumber > actualBooking.getCurrentPeopleNumber() && actualBooking.isPaid());
        checkPeopleInformation.setInExitWindow(diff < 5);

        return Optional.of(checkPeopleInformation);
    }

    @Scheduled(cron = "0 3 * * * *")
    public void deleteOverdueBooking(){
        System.out.println("deleteOverdueBooking");

        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        if (currentTime == 0) {
            List<Booking> todayBooking = bookingDao.getAllByDate(Date.valueOf(LocalDate.now(NSK_ZONE_ID).minusDays(1)));
            for(Booking booking: todayBooking){
                deleteBooking(booking);
            }
            bookingTime.deletePastReserving();
        }

        List<Booking> todayBooking = bookingDao.getAllByDate(Date.valueOf(LocalDate.now(NSK_ZONE_ID)));
        for(Booking booking: todayBooking){
            if(booking.getEndTime() <= currentTime){
                deleteBooking(booking);
            }
        }
    }

    private void deleteBooking(Booking booking){
        bookingDao.deleteByBooking(booking);
        List<Game> gamesToChange = bookingAndGamesDao.getGamesByBooking(booking);
        for(Game game: gamesToChange) {
            gameDao.updateIsTakenByGameId(game.getGameId(), false);
        }
        bookingAndGamesDao.deleteGamesByBookingId(booking.getBookingNumber());
    }

    @Scheduled(cron = "0 45 * * * *")
    private void checkTimeOut(){
        System.out.println("checkTimeOut");
        int currentTime = LocalDateTime.now(NSK_ZONE_ID).getHour();
        List<Booking> todayBooking = bookingDao.getAllByDate(Date.valueOf(LocalDate.now(NSK_ZONE_ID)));
        for (Booking booking : todayBooking) {
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
        List<Booking> todayBookingList = bookingDao.getAllByDate(Date.valueOf(LocalDate.now(NSK_ZONE_ID)));
        for (Booking booking : todayBookingList) {
            if (booking.getEndTime() == (currentTime + 1) && booking.isPaid()) {
                try {
                    bookingDao.updateCurrentPeopleByBookingNumber(0, booking.getBookingNumber());
                    infoSender.sendToAdminRoomInfo(booking);
                }
                catch (NullPointerException | WrongRoomNumberException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
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


//    @Scheduled(cron = "0 5 * * * *")
//    @Scheduled(cron = "@hourly")
//    public void updateRoomData() {
//            try {
//                infoSender.sendToAdminRoomInfo(bufferRoomData.getRoomNumber(), getTodayBeginBookingList());
//            } catch (WrongRoomNumberException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//    }

}
