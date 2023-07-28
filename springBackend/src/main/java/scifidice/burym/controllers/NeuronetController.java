package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scifidice.burym.infoBot.Notification;
import scifidice.burym.model.AdminMessage;
import scifidice.burym.model.AdminMessageType;
import scifidice.levachev.DataBaseHandler.AutoUpdatableDataBaseHandler;
import scifidice.levachev.DataBaseHandler.WrongRoomNumberException;
import scifidice.levachev.Model.CheckPeopleInformation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static scifidice.burym.config.SpringConfig.*;

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

    @PostMapping(value = "/setPeople")
    @ResponseBody
    public String getDate(@RequestParam("time") String time, @RequestParam("room") int room, @RequestParam("people") int people,  @RequestParam("image") MultipartFile img) {

        adminController.sendImageToAdmin(parseImg(img), room);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy, HH:mm:ss");
            LocalDateTime localTime = LocalDateTime.parse(time, formatter);
            System.out.println("КОЛ_ВО ЛЮДЕЙ " + people + " В " + room + " ВРЕМЯ: " + time);
            Optional<CheckPeopleInformation> optPeopleInfo = autoUpdatableDataBaseHandler.checkPeople(room, people, localTime);
            if (optPeopleInfo.isPresent()) {
                CheckPeopleInformation peopleInfo = optPeopleInfo.get();
                if (peopleInfo.isViolate()) {
                    if (peopleInfo.isInExitWindow()) {
                        System.out.println("ТРЕВОГА В ОКНЕ");
                        adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM, LocalDateTime.now(NSK_ZONE_ID) + " ЛЮДИ В КОМНАТЕ №" + room +
                                " ПОСЛЕ ОКОНЧАНИЯ ВРЕМЕНИ!!! кол-во: " + people + "."));
                        notification.sendMessageToInfoBot(peopleInfo.getInfoBotChatID(), WARNING_TIME_MESSAGE);
                    }
                    else {
                        System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " МНОГО ЧЕЛ");
                        if (startTime == null) {
                            startTime = LocalTime.now(NSK_ZONE_ID);
                        }
                        if (MINUTES.between(startTime, LocalTime.now(NSK_ZONE_ID)) > 2) {
                            startTime = null;
                            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " MANY PEOPLE WARNING in room #" + room +
                                    "count: " + people + ".");
                            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM,LocalDateTime.now(NSK_ZONE_ID) + " В КОМНАТЕ #"+ room +
                                    "БОЛЬШЕ ЛЮДЕЙ ЧЕМ БЫЛО ЗАЯВЛЕНО, спустя 5 минут гости не доплатили за новоприбывших " +
                                    "кол-во: " + people + "."));
                        }
                        notification.sendMessageToInfoBot(peopleInfo.getInfoBotChatID(), NOTIFY_MANY_PEOPLE);
                    }

                }
            }
            return "SUCCESS";
        } catch (WrongRoomNumberException e) {
            System.out.println("REQUEST NUM OF PEOPLE: wrong number of room: " + room  + ".");
            adminController.sendMessageToAdmin(new AdminMessage(AdminMessageType.ALARM,LocalDateTime.now(NSK_ZONE_ID) + " Неверная комната с нейросети, комната: " + room  + "."));
            return "FAILED";
        }
        catch (DateTimeParseException e) {
            System.out.println("PARSEEEE " + e.getMessage());
            return "FAILED";
        }
    }

    private byte[] parseImg(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream("img.png"));
            stream.write(bytes);
            stream.close();

            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
