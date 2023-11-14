package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import scifidice.Entity.HoursPair;
import scifidice.Entity.RoomInfo;
import scifidice.controllers.AdminController;
import scifidice.db.dao.BookingDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Room;
import scifidice.db.mapper.BookingMapper;

import java.time.LocalTime;
import java.util.List;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.db.dataBaseHandler.AutoUpdatableDataBaseHandler.getTodayBeginBookingList;

@Component
@PropertySource("classpath:dataBase.properties")
public class InfoSender {
    @Value("${roomNumber}")
    private int roomNumber;

    private final AdminController adminController;
    private final RoomDao roomDao;
    private final BookingDao bookingDao;

    @Autowired
    public InfoSender(AdminController adminController, RoomDao roomDao, BookingDao bookingDao) {
        this.adminController = adminController;
        this.roomDao = roomDao;
        this.bookingDao = bookingDao;
    }

    @EventListener
    public void handleSessionConnected(SessionSubscribeEvent event) {
        System.out.println("Admin has connected.");
        for (int i = 1; i <= roomNumber; i++) {
            try {
                sendToAdminRoomInfo(i, getTodayBeginBookingList());
            } catch (WrongRoomNumberException e) {
                System.out.println("Invalid roomNumber in properties file");
            }
        }
    }

    public void sendToAdminRoomInfo(int roomNumber, List<Booking> todayBeginBookingList) throws WrongRoomNumberException {
        Room room = roomDao.getRoomByNumber(roomNumber);
        if (room == null) {
            throw new WrongRoomNumberException("wrong room number");
        }
        Booking booking = bookingDao.getByRoomNumber(roomNumber);
        if (booking == null) {
            throw new WrongRoomNumberException("wrong room number");
        }
        HoursPair hoursPair = getHoursPair(roomNumber, todayBeginBookingList);
        if (booking.getCurrentPeopleNumber() == 0) {
            adminController.sendRoomInfo(new RoomInfo(roomNumber, room.getPassword(), booking.getCurrentPeopleNumber(),
                    -1, -1));
        } else {
            adminController.sendRoomInfo(new RoomInfo(roomNumber, room.getPassword(), booking.getCurrentPeopleNumber(),
                    hoursPair.getFirstHour(), hoursPair.getSecondHour()));
        }
    }

    private HoursPair getHoursPair(int roomNumber, List<Booking> todayBeginBookingList) {
        for (Booking booking : todayBeginBookingList) {
            LocalTime nowTime = LocalTime.now(NSK_ZONE_ID);
            if (booking.getBeginTime() <= nowTime.getHour() &&
                    booking.getEndTime() > nowTime.getHour() &&
                    booking.getRoomNumber() == roomNumber) {
                return new HoursPair(booking.getBeginTime(), booking.getEndTime());
            }
        }
        return new HoursPair(-1, -1);
    }
}
