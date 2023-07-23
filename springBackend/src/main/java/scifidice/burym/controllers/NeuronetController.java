package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scifidice.burym.infoBot.Notification;
import scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler;
import scifidice.levachev.DataBaseHandler.WrongRoomNumberException;
import scifidice.levachev.Model.CheckPeopleInformation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static scifidice.burym.config.SpringConfig.NOTIFY_MANY_PEOPLE;
import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;

@RestController
@RequestMapping("/neuronet")
public class NeuronetController {

    @Autowired
    AutoUpdatableDataBaseHandler autoUpdatableDataBaseHandler;

    @Autowired
    Notification notification;

    @Autowired
    private AdminController adminController;

    private LocalTime startTime = null;

    @GetMapping(value = "/setPeople")
    @ResponseBody
    public String getDate(@RequestParam("room") int room, @RequestParam("people") int people, @RequestParam("time") String time) {
        try {
            LocalTime localTime = LocalTime.parse(time);
            System.out.println("КОЛ_ВО ЛЮДЕЙ " + people + " В " + room + " ВРЕМЯ: " + time);
            Optional<CheckPeopleInformation> optPeopleInfo = autoUpdatableDataBaseHandler.checkPeople(room, people, localTime);
            if (optPeopleInfo.isPresent()) {
               CheckPeopleInformation peopleInfo = optPeopleInfo.get();
               if (peopleInfo.isViolate()) {
                   if (peopleInfo.isInExitWindow()) {
                       //Тревога
                       //TODO слать админу
                       System.out.println("ТРЕВОГА В ОКНЕ");
                       adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " PEOPLE AFTER TIMEOUT!!! in room #" + room +
                               "count: " + people + ".");
                   }
                   else {
                       //Много чел
                       System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " МНОГО ЧЕЛ");
                       if (startTime == null) {
                           startTime = LocalTime.now(NSK_ZONE_ID);
                       }
                       if (MINUTES.between(startTime, LocalTime.now(NSK_ZONE_ID)) > 5) {
                           startTime = null;
                           System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " MANY PEOPLE WARNING in room #" + room +
                                   "count: " + people + ".");
                           adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " MANY PEOPLE WARNING in room #" + room +
                                   "count: " + people + ".");
                       }
                       notification.sendMessageToInfoBot(peopleInfo.getInfoBotChatID(), NOTIFY_MANY_PEOPLE);
                   }
               }
            }
            return "SUCCESS";
        } catch (WrongRoomNumberException e) {
            System.out.println("REQUEST NUM OF PEOPLE: wrong number of room: " + room  + ".");
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) + " REQUEST NUM OF PEOPLE: wrong number of room: " + room  + ".");
            return "FAILED";
        }
    }
}
