package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scifidice.burym.infoBot.Notification;
import scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler;
import scifidice.levachev.DataBaseHandler.WrongRoomNumberException;

import java.util.Optional;

import static scifidice.burym.config.SpringConfig.NOTIFY_MANY_PEOPLE;

@RestController
@RequestMapping("/neuronet")
public class NeuronetController {

    @Autowired
    AutoUpdatableDataBaseHandler autoUpdatableDataBaseHandler;

    @Autowired
    Notification notification;

    @GetMapping(value = "/setPeople")
    @ResponseBody
    public String getDate(@RequestParam("room") int room, @RequestParam("people") int people) {     //тут обязательно нужны параметры, если не стоит required=false
        try {
            Optional<String> optString = autoUpdatableDataBaseHandler.checkPeople(room, people);
            optString.ifPresent(s -> notification.sendMessageToInfoBot(s, NOTIFY_MANY_PEOPLE));
            System.out.println("КОЛ_ВО ЛЮДЕЙ " + people + " В " + room);
            return "SUCCESS";
        } catch (WrongRoomNumberException e) {
            System.out.println("REQUEST NUM OF PEOPLE: wrong number of room: " + room  +" " + e.getMessage());
            return "FAILED";
        }
    }
}
