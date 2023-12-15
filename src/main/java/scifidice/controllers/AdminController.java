package scifidice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import scifidice.model.AdminMessage;
import scifidice.model.RoomInfo;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@EnableScheduling
@Controller
public class AdminController {

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping(path="/messages", method = POST)
    public void sendMessageToAdmin(AdminMessage message) {
        this.template.convertAndSend("/admin/messages", message);
    }

    @RequestMapping(path="/images", method = POST)
    public void sendImageToAdmin(byte[] media, int room) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.add("ROOM", String.valueOf(room));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        System.out.println("ПИКЧА ОТПРАВИЛАСЬ");
        this.template.convertAndSend("/admin/images", responseEntity);
    }

    @RequestMapping(path="/roomInfo", method = POST)
    public void sendRoomInfo(RoomInfo roomInfo) {
        System.out.println("Info: room=" + roomInfo.getNumber()+ " people=" + roomInfo.getCurrentPeopleNumber() +
                " password=" + roomInfo.getPassword() + " s=" + roomInfo.getFirstHour() + " e=" + roomInfo.getSecondHour());
        this.template.convertAndSend("/admin/roomInfo", roomInfo);
    }
}


