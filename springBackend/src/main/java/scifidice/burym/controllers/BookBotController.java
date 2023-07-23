package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import scifidice.burym.bookingBot.*;
import scifidice.levachev.DataBaseHandler.BookingBotDataBaseHandler;
import scifidice.levachev.Model.RoomScheduleForDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.levachev.DataBaseHandler.BookingBotDataBaseHandler.dayNumberRelativeToToday;


@RestController
@RequestMapping("/bot")
public class BookBotController {
    @Autowired
    private BookingBotDataBaseHandler bookingBotDataBaseHandler;

    @Autowired
    private Handler handler;

    @Autowired
    private AdminController adminController;

    @GetMapping(value = "/getDate")
    @ResponseBody
    private DateResponse getDate(@RequestParam("dateStr") String dateStr) {     //тут обязательно нужны параметры, если не стоит required=false
        try {
            System.out.println("DATE REQUEST: " + dateStr);
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) +" DATE REQUEST: " + dateStr + ".");
            LocalDate date = CheckValid.getDateObject(dateStr); // проверка даты на валидность
            ArrayList<RoomScheduleForDay> rooms =  bookingBotDataBaseHandler.getScheduleForDate((int) dayNumberRelativeToToday(date));
            StringBuilder dateAns = handler.dateHandler(rooms);
            return new DateResponse(0, dateAns.toString());
        }
        catch(DateTimeParseException e) {
            System.out.println("DATE REQUEST: " + dateStr + " is not valid date.");
            return new DateResponse(-1, "");
        }
        catch (Exception e) {
            adminController.sendMessageToAdmin("DATABASE shutdown!!!!");
            return new DateResponse(-1, "");
        }
    }

    @PostMapping( "/postReservation")
    @ResponseBody
    private ReservationResponse postReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) +" RESERVATION REQUEST: user: " + reservationRequest.getUserId() + ", d: " + reservationRequest.getDateStr() + ", r: " + reservationRequest.getRoom() + ", h: " + reservationRequest.getHours());
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) +" RESERVATION REQUEST: user: " + reservationRequest.getUserId() + ", d: "
                    + reservationRequest.getDateStr() + ", r: " + reservationRequest.getRoom() + ", h: " + reservationRequest.getHours() + ".");
            int[] hours = CheckValid.checkHours(reservationRequest.getHours()); // проверка валидности часов
            int id = bookingBotDataBaseHandler.book(reservationRequest.getUserId(), CheckValid.getDateObject(reservationRequest.getDateStr()),
                                            CheckValid.getDateObject(reservationRequest.getDateStr()), hours[0], hours[1], reservationRequest.getRoom());
            if (id == -1) {
                throw new RuntimeException();
            }
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RESERVATION RESPONSE: book id: " + id + ", user: " + reservationRequest.getUserId());
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " RESERVATION RESPONSE: book id: " + id + ", user: "
                    + reservationRequest.getUserId() + ".");
            return new ReservationResponse(0, Integer.toString(id));
        } catch (Exception e) {
            return new ReservationResponse(-1, "");
        }
    }

    @GetMapping( "/postPhone")
    @ResponseBody
    private int postPhone(@RequestParam("phone") String phone, @RequestParam("chatId") String bookingChatId) {
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " NEW USER FROM BOOKING: phone: " + phone + ", chat id: " + bookingChatId +".");
        try {
            bookingBotDataBaseHandler.authorization(phone.substring(1), bookingChatId);
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " NEW USER FROM BOOKING: phone: "
                    + phone + ", chat id: " + bookingChatId + " - SUCCESS.");
            return 0;
        }
        catch (DataAccessException e) {
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " NEW USER FROM BOOKING: phone: "
                    + phone + ", chat id: " + bookingChatId + " - FAILED.");
            return -1;
        }
    }

    @GetMapping( "/changePhone")
    @ResponseBody
    private int changePhone(@RequestParam("phone") String phone, @RequestParam("chatId") String bookingChatId) {
        try {
            int ans = bookingBotDataBaseHandler.updatePhoneNumberByBookingBotChatID(phone.substring(1), bookingChatId);
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + "CHANGE PHONE(booking): chat id "+ bookingChatId + ", codeAns: " + ans);
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + "CHANGE PHONE(booking): chat id "+ bookingChatId + ", codeAns: " + ans);
            return ans;
        }
        catch (DataAccessException e) {
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + "CHANGE PHONE(booking): chat id "+ bookingChatId +", codeAns: -1");
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + "CHANGE PHONE(booking): chat id "+ bookingChatId +", codeAns: -1");
            return -1;
        }
    }

}
