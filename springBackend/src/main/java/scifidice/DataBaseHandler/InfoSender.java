package scifidice.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import scifidice.controllers.AdminController;
import scifidice.Mapper.RoomMapper;
import scifidice.Entity.Booking;
import scifidice.Entity.HoursPair;
import scifidice.Entity.Room;
import scifidice.Entity.RoomInfo;

import java.time.LocalTime;
import java.util.List;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.DataBaseHandler.AutoUpdatableDataBaseHandler.getTodayBeginBookingList;

@Component
@PropertySource("classpath:dataBase.properties")
public class InfoSender {
    @Value("${roomNumber}")
    private int roomNumber;

    @Autowired
    private AdminController adminController;

    @Autowired
    private JdbcTemplate jdbcTemplateOrganisationDB;

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
        Room room = jdbcTemplateOrganisationDB.
                query("SELECT * FROM room WHERE number=?", new Object[]{roomNumber}, new RoomMapper()).
                stream().findAny().orElse(null);
        if (room == null) {
            throw new WrongRoomNumberException("wrong room number");
        }
        HoursPair hoursPair = getHoursPair(roomNumber, todayBeginBookingList);
        if (room.getCurrentPersonNumber() == 0) {
            adminController.sendRoomInfo(new RoomInfo(roomNumber, room.getPassword(), room.getCurrentPersonNumber(),
                    -1, -1));
        } else {
            adminController.sendRoomInfo(new RoomInfo(roomNumber, room.getPassword(), room.getCurrentPersonNumber(),
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
