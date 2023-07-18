package scifidice.burym.controllers;

import org.springframework.dao.DataAccessException;
import scifidice.burym.bookingBot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scifidice.levachev.DataBaseHandler.BookingBotDataBaseHandler;
import scifidice.levachev.Model.RoomScheduleForDay;

import static scifidice.levachev.DataBaseHandler.BookingBotDataBaseHandler.dayNumberRelativeToToday;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


@RestController
@RequestMapping("/bot")
public class BookBotController {
    @Autowired
    private BookingBotDataBaseHandler bookingBotDataBaseHandler;

    @Autowired
    private Handler handler;

    @GetMapping(value = "/getDate")
    @ResponseBody
    public DateResponse getDate(@RequestParam("dateStr") String dateStr) {     //тут обязательно нужны параметры, если не стоит required=false
        try {
            System.out.println(dateStr);
            LocalDate date = CheckValid.getDateObject(dateStr); // проверка даты на валидность
            ArrayList<RoomScheduleForDay> rooms =  bookingBotDataBaseHandler.getScheduleForDate((int) dayNumberRelativeToToday(date));
            StringBuilder dateAns = handler.dateHandler(rooms); //TODO распарсить
            System.out.println(dateAns);
            return new DateResponse(0, dateAns.toString());
        }
        catch(DateTimeParseException e) {
            System.out.println(dateStr + " is not valid date.");
            return new DateResponse(-1, "");
        }
    }

    @PostMapping( "/postReservation")
    @ResponseBody
    public ReservationResponse postReservation(@RequestBody ReservationRequest reservationRequest) {
        try {
            System.out.println(reservationRequest.getUserId() + " " + reservationRequest.getDateStr() + " " + reservationRequest.getRoom() + " " + reservationRequest.getHours());
            int[] hours = CheckValid.checkHours(reservationRequest.getHours()); // проверка валидности часов
            int id = bookingBotDataBaseHandler.book(reservationRequest.getUserId(), CheckValid.getDateObject(reservationRequest.getDateStr()),
                                            CheckValid.getDateObject(reservationRequest.getDateStr()), hours[0], hours[1], reservationRequest.getRoom());
            if (id == -1) {
                throw new RuntimeException();
            }
            System.out.println("book id: " + id);
            //TODO запись в БД и просить ID брони
            return new ReservationResponse(0, Integer.toString(id));
        } catch (Exception e) {
            return new ReservationResponse(-1, "");
        }
    }

    @GetMapping( "/postPhone")
    @ResponseBody
    public String postPhone(@RequestParam("phone") String phone, @RequestParam("chatId") String bookingChatId) {
        System.out.println("Phone: " + phone + ", chatId: " + bookingChatId);
        try {
            bookingBotDataBaseHandler.authorization(phone, bookingChatId);
            return "0";
        }
        catch (DataAccessException e) {
            return "-1";
        }
    }
}
