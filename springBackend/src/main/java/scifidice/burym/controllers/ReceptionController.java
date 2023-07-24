package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public ResponseReception postId(@RequestParam("bookId") int bookId) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.isBookingNumberValid(bookId);
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: booking id: " + + bookId + ".");
        adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: booking id: " + + bookId + ".");
        return new ResponseReception(receptionCodeAnswer.toString());
    }

    @GetMapping(value = "/postPayData")
    @ResponseBody
    public ClientInformation postPayData(@RequestParam("people") int people, @RequestParam("gameId") int gameId) {
        ClientInformation clientInformation = receptionDataBaseHandler.payBooking(gameId, people);
        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: people: " + people + ", gameId: " + gameId
                + ", " + clientInformation.getCodeAnswer() + " sT: " + clientInformation.getBeginTime() + ", eT: " + clientInformation.getEndTime());
        adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION: people: " + people + ", gameId: " + gameId
                + ", " + clientInformation.getCodeAnswer() + " sT: " + clientInformation.getBeginTime() + ", eT: " + clientInformation.getEndTime());
        return clientInformation;
    }

    @GetMapping(value = "/addPeople")
    @ResponseBody
    public ResponseReception addPeople(@RequestParam("people") int people) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.addPeople(people);
        adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " RECEPTION ADD: people: " + people + ", " + receptionCodeAnswer);
        return new ResponseReception(receptionCodeAnswer.toString());
    }
}
