package scifidice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scifidice.model.AdminMessage;
import scifidice.model.AdminMessageType;
import scifidice.model.StringResponse;
import scifidice.DataBaseHandler.ReceptionDataBaseHandler;
import scifidice.Entity.ClientInformation;
import scifidice.Entity.ReceptionCodeAnswer;

import java.time.LocalDateTime;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;
import static scifidice.config.SpringConfig.dateTimeFormatter;


@RestController
@RequestMapping("backend/reception")
public class ReceptionController {

    @Autowired
    private ReceptionDataBaseHandler receptionDataBaseHandler;

    @Autowired
    private AdminController adminController;

    @GetMapping(value = "/postId")
    private StringResponse postId(@RequestParam("bookId") int bookId) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.isBookingNumberValid(bookId);
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: booking id: " + + bookId + ".");
        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,
                LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " РЕСЕПШЕН: ввели id брони: " + + bookId + "..."));
        return new StringResponse(receptionCodeAnswer.toString());
    }

    @GetMapping(value = "/postPayData")
    private ClientInformation postPayData(@RequestParam("people") int people, @RequestParam("gameId") int gameId) {
        ClientInformation clientInformation = receptionDataBaseHandler.payBooking(gameId, people);
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: people: " + people + ", gameId: " + gameId
                + ", " + clientInformation.getCodeAnswer() + " sT: " + clientInformation.getBeginTime() + ", eT: " + clientInformation.getEndTime());
        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,
                LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + "...пришло " + people + " человек в комнату #" +
                        clientInformation.getRoomNumber()+ ", взяли игру: " + gameId + ", начало: " + clientInformation.getBeginTime()
                        + "ч, конец: " + clientInformation.getEndTime() + "ч, наш ответ: " + clientInformation.getCodeAnswer() + "."));
        return clientInformation;
    }

    @GetMapping(value = "/addPeople")
    private StringResponse addPeople(@RequestParam("people") int people) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.addPeople(people);
        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,
                LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + "...добавление новых людей: +" + people + " чел, " + receptionCodeAnswer));
        return new StringResponse(receptionCodeAnswer.toString());
    }
}
