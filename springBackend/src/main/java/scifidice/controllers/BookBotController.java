package scifidice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import scifidice.model.*;
import scifidice.db.dataBaseHandler.BookingBotDataBaseHandler;
import scifidice.Entity.HoursPair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.config.SpringConfig.dateTimeFormatter;
import static scifidice.db.dataBaseHandler.BookingBotDataBaseHandler.dayNumberRelativeToToday;


@RestController
@RequestMapping("/backend/bot")
public class BookBotController {
    @Autowired
    private BookingBotDataBaseHandler bookingBotDataBaseHandler;

    @Autowired
    private AdminController adminController;

    @GetMapping(value = "/getDate")
    private DateResponse getDate(@RequestParam("dateStr") String dateStr, @RequestParam("room") int room) {     //тут обязательно нужны параметры, если не стоит required=false
        try {
            System.out.println(LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " DATE REQUEST: " + dateStr);
            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG, LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) +" Запрос даты: " + dateStr + "."));
            LocalDate date = HandlerDateTime.getDateObject(dateStr); // проверка даты на валидность
            ArrayList<HoursPair> hourPairs =  bookingBotDataBaseHandler.getScheduleForDateByRoomNumber((int) dayNumberRelativeToToday(date), room);
            StringBuilder dateAns = HandlerDateTime.hoursPairsHandler(hourPairs);
            dateAns.insert(0, "ROOM #" + room + "\n");
            return new DateResponse(0, dateAns.toString());
        }
        catch(DateTimeParseException e) {
            System.out.println("DATE REQUEST: " + dateStr + " is not valid date.");
            return new DateResponse(-1, "");
        }
        catch (Exception e) {
            System.out.println("DATABASE shutdown!!!");
            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " DATABASE shutdown!!!!"));
            return new DateResponse(-1, "");
        }
    }

    @PostMapping( "/postReservation")
    private ReservationResponse postReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            String phone = bookingBotDataBaseHandler.getPhoneNumberByBookingChatID(reservationRequest.getUserId());
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) +" RESERVATION REQUEST: user: " + reservationRequest.getUserId()+", phone: "+ phone + ", d: " + reservationRequest.getDateStr() + ", r: " + reservationRequest.getRoom() + ", h: " + reservationRequest.getHours());

            if (bookingBotDataBaseHandler.isBlackMarkPerson(reservationRequest.getUserId())) {
                throw new RuntimeException("Person "+ reservationRequest.getUserId() +" in black list");
            }

            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + "  Запрос на бронирование: пользователь: " + phone + ", на дату: "
                    + reservationRequest.getDateStr() + ", в комнату: " + reservationRequest.getRoom() + ", часы: " + reservationRequest.getHours() + "ч."));

            int[] hours = HandlerDateTime.getHours(reservationRequest.getHours()); // проверка валидности часов
            int id = bookingBotDataBaseHandler.book(reservationRequest.getUserId(), HandlerDateTime.getDateObject(reservationRequest.getDateStr()),
                    HandlerDateTime.getDateObject(reservationRequest.getDateStr()), hours[0], hours[1], reservationRequest.getRoom());
            if (id == -1) {
                throw new RuntimeException("id = -1");
            }
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RESERVATION RESPONSE: book id: " + id + ", user: " + reservationRequest.getUserId()+", phone: "+ phone);

            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " Пользователь: "+ phone +
                    " успешно забронировал, id брони: " + id + "."));

            return new ReservationResponse(0, Integer.toString(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ReservationResponse(-1, "");
        }
    }

    @GetMapping( "/postPhone")
    private int postPhone(@RequestParam("phone") String phone, @RequestParam("chatId") String bookingChatId) {
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " NEW USER FROM BOOKING: phone: " + phone + ", chat id: " + bookingChatId +".");
        try {
            bookingBotDataBaseHandler.authorization(phone, bookingChatId);
            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " Новый пользователь букинг бота: телефон: "
                    + phone + ", его chat id: " + bookingChatId + " - УСПЕШНО."));
            return 0;
        }
        catch (DataAccessException e) {
            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " Новый пользователь букинг бота: телефон: "
                    + phone + ", его chat id: " + bookingChatId + " - ПРОВАЛЕНО."));
            return -1;
        }
    }

    @GetMapping( "/checkHasPhone")
    private int checkHasPhone(@RequestParam("chatId") String bookingChatId) {
        if (bookingBotDataBaseHandler.getPhoneNumberByBookingChatID(bookingChatId) == null) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
