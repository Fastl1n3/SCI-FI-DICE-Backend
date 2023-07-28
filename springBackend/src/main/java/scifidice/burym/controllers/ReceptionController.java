package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scifidice.burym.model.AdminMessage;
import scifidice.burym.model.AdminMessageType;
import scifidice.levachev.DataBaseHandler.ReceptionDataBaseHandler;
import scifidice.levachev.Model.ClientInformation;
import scifidice.levachev.Model.ReceptionCodeAnswer;

import java.time.LocalDateTime;

import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;


@RestController
@RequestMapping("/reception")
public class ReceptionController {

    @Autowired
    private ReceptionDataBaseHandler receptionDataBaseHandler;

    @Autowired
    private AdminController adminController;

    static class ResponseReception {
        private String message;
        ResponseReception() {}
        ResponseReception(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }

    @GetMapping(value = "/postId")
    @ResponseBody
    public scifidice.burym.controllers.ReceptionController.ResponseReception postId(@RequestParam("bookId") int bookId) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.isBookingNumberValid(bookId);
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: booking id: " + + bookId + ".");
        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,
                LocalDateTime.now(NSK_ZONE_ID) + " РЕСЕПШЕН: ввели id брони: " + + bookId + "."));
        return new scifidice.burym.controllers.ReceptionController.ResponseReception(receptionCodeAnswer.toString());
    }

    @GetMapping(value = "/postPayData")
    @ResponseBody
    public ClientInformation postPayData(@RequestParam("people") int people, @RequestParam("gameId") int gameId) {
        ClientInformation clientInformation = receptionDataBaseHandler.payBooking(gameId, people);
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: people: " + people + ", gameId: " + gameId
                + ", " + clientInformation.getCodeAnswer() + " sT: " + clientInformation.getBeginTime() + ", eT: " + clientInformation.getEndTime());
        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,
                LocalDateTime.now(NSK_ZONE_ID) + " РЕСЕПШЕН: пришло " + people + " человек в комнату #" +
                        clientInformation.getRoomNumber()+ ", взяли игру: " + gameId + ", начало: " + clientInformation.getBeginTime()
                        + "ч, конец: " + clientInformation.getEndTime() + "ч, наш ответ: " + clientInformation.getCodeAnswer() + "."));
        return clientInformation;
    }

    @GetMapping(value = "/addPeople")
    @ResponseBody
    public scifidice.burym.controllers.ReceptionController.ResponseReception addPeople(@RequestParam("people") int people) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.addPeople(people);
        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.LOG,
                LocalDateTime.now(NSK_ZONE_ID) + " РЕСЕПШЕН: добавление новых людей: +" + people + " чел, " + receptionCodeAnswer));
        return new scifidice.burym.controllers.ReceptionController.ResponseReception(receptionCodeAnswer.toString());
    }
}
