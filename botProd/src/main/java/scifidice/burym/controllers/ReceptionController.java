package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import scifidice.levachev.DataBaseHandler.ReceptionDataBaseHandler;
import scifidice.levachev.Model.ClientInformation;
import scifidice.levachev.Model.ReceptionCodeAnswer;


@RestController
@RequestMapping("/reception")
public class ReceptionController {

    @Autowired
    private ReceptionDataBaseHandler receptionDataBaseHandler;
    static class ResponseIsValid {
        private String message;
        ResponseIsValid() {}
        ResponseIsValid(String message) {
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
    public ResponseIsValid postId(@RequestParam("bookId") int bookId) {
        ReceptionCodeAnswer receptionCodeAnswer = receptionDataBaseHandler.isBookingNumberValid(bookId);
        System.out.println("RECEPTION ID: " + bookId);
        return new ResponseIsValid(receptionCodeAnswer.toString());
    }

    @GetMapping(value = "/postPayData")
    @ResponseBody
    public ClientInformation postPayData(@RequestParam("people") int people, @RequestParam("gameId") int gameId) {
        ClientInformation clientInformation = receptionDataBaseHandler.payBooking(gameId, people);
        return clientInformation;
    }
}
