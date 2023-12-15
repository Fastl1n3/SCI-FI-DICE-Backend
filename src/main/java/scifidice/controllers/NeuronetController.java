package scifidice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scifidice.infoBot.Notification;
import scifidice.model.AdminMessage;
import scifidice.model.AdminMessageType;
import scifidice.db.dataBaseHandler.AutoUpdatableDataBaseHandler;
import scifidice.db.dataBaseHandler.WrongRoomNumberException;
import scifidice.Entity.CheckPeopleInformation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static scifidice.config.SpringConfig.*;

@RestController
@RequestMapping("/neuronet")
public class NeuronetController {

    @Autowired
    private AutoUpdatableDataBaseHandler autoUpdatableDataBaseHandler;

    @Autowired
    private Notification notification;

    @Autowired
    private AdminController adminController;

    private LocalTime[] startTime = new LocalTime[5];

    @PostMapping(value = "/setPeople")
    private String getDate(@RequestParam("time") String time, @RequestParam("room") int room, @RequestParam("people") int people,  @RequestParam("image") MultipartFile img) {
        try {
            adminController.sendImageToAdmin(img.getBytes(), room);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy, HH:mm:ss");
            LocalDateTime localTime = LocalDateTime.parse(time, formatter);
            System.out.println("КОЛ_ВО ЛЮДЕЙ " + people + " В " + room + " ВРЕМЯ: " + time);
            Optional<CheckPeopleInformation> optPeopleInfo = autoUpdatableDataBaseHandler.checkPeople(room, people, localTime);
            if (optPeopleInfo.isPresent()) {
                CheckPeopleInformation peopleInfo = optPeopleInfo.get();
                if (peopleInfo.isViolate()) {
                    if (peopleInfo.isInExitWindow()) {
                        System.out.println("ТРЕВОГА В ОКНЕ");
                        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM, LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " ЛЮДИ В КОМНАТЕ #" + room +
                                " ПОСЛЕ ОКОНЧАНИЯ ВРЕМЕНИ!!! кол-во человек: " + people + ", номер телефона: " + peopleInfo.getPhoneNumber() + "."));
                        notification.sendMessageToInfoBot(peopleInfo.getInfoBotChatID(), WARNING_TIME_MESSAGE);
                    }
                    else {
                        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " МНОГО ЧЕЛ");
                        if (startTime[room - 1] == null) {
                            startTime[room - 1] = LocalTime.now(NSK_ZONE_ID);
                        }
                        if (MINUTES.between(startTime[room - 1], LocalTime.now(NSK_ZONE_ID)) > 5) {
                            startTime[room - 1] = null;
                            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " MANY PEOPLE WARNING in room #" + room +
                                    "count: " + people + ".");
                            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " В КОМНАТЕ #"+ room +
                                    "БОЛЬШЕ ЛЮДЕЙ ЧЕМ БЫЛО ЗАЯВЛЕНО, спустя 5 минут гости не доплатили за новоприбывших " +
                                    "кол-во человек: " + people + ", номер телефона: " + peopleInfo.getPhoneNumber() + "."));
                        }
                        notification.sendMessageToInfoBot(peopleInfo.getInfoBotChatID(), NOTIFY_MANY_PEOPLE);
                    }

                }
            }
            return "SUCCESS";
        } catch (WrongRoomNumberException e) {
            System.out.println("REQUEST NUM OF PEOPLE: wrong number of room: " + room  + ".");
            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM,LocalDateTime.now(NSK_ZONE_ID).format(dateTimeFormatter) + " Неверная комната с нейросети, комната: " + room  + "."));
            return "FAILED";
        }
        catch (DateTimeParseException e) {
            System.out.println("PARSEEEE " + e.getMessage());
            return "FAILED";
        } catch (IOException e) {
            System.out.println("Can't read bytes from image: " + e.getMessage());
            return "FAILED";
        }
    }
}
