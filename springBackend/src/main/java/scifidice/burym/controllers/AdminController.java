package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@EnableScheduling
@Controller
public class AdminController {

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping(path="/messages", method = POST)
    public void sendMessageToAdmin(String message) {
        this.template.convertAndSend("/admin/messages", message);
    }
}
