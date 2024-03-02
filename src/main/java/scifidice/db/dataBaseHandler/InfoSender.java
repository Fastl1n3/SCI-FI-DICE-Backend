package scifidice.db.dataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import scifidice.model.RoomInfo;
import scifidice.controllers.AdminController;
import scifidice.db.dao.BookingDao;
import scifidice.db.dao.RoomDao;
import scifidice.db.entities.Booking;
import scifidice.db.entities.Room;

import java.time.LocalDateTime;


import static scifidice.config.SpringConfig.NSK_ZONE_ID;

@Component
@PropertySource("classpath:business.properties")
public class InfoSender {
    @Value("${maxRooms}")
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
                sendToAdminRoomInfo(bookingDao.getBookingByRoomAndDate(i, LocalDateTime.now(NSK_ZONE_ID)));
            } catch (WrongRoomNumberException e) {
                System.out.println("Invalid roomNumber in properties file");
            }
        }
    }

    public void sendToAdminRoomInfo(Booking booking) throws WrongRoomNumberException {
        Room room = roomDao.getRoomByNumber(booking.getRoomNumber());

        if (booking.getCurrentPeopleNumber() == 0) {
            adminController.sendRoomInfo(new RoomInfo(room.getNumber(), room.getPassword(), booking.getCurrentPeopleNumber(),
                    -1, -1));
        } else {
            adminController.sendRoomInfo(new RoomInfo(room.getNumber(), room.getPassword(), booking.getCurrentPeopleNumber(),
                    booking.getBeginTime(), booking.getEndTime()));
        }
    }

}
