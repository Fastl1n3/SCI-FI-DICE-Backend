package scifidice.burym.controllers;

import org.springframework.dao.DataAccessException;
import scifidice.burym.infoBot.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scifidice.levachev.DataBaseHandler.InformationBotDataBaseHandler;

import java.time.LocalDateTime;

import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;

@RestController
@RequestMapping("/info")
public class InfoBotController {
    @Autowired
    private InformationBotDataBaseHandler informationBotDataBaseHandler;

    @Autowired
    private AdminController adminController;

    @GetMapping(value = "/postChatId")
    @ResponseBody
    private ResponseEntity<Integer> postChatID(@RequestParam("phone") String phone, @RequestParam("chatId") String chatId) {
        int a = -1;
        try {
            a = informationBotDataBaseHandler.authorization(phone, chatId);
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) +" NEW USER FROM INFO: phone: "
                    + phone + ", chat id: " + chatId + ", codeAns: " + a + ".");
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) +" NEW USER FROM INFO: phone: "
                    + phone + ", chat id: " + chatId + ", codeAns: " + a + ".");
            return ResponseEntity.ok(a);
        }
        catch (DataAccessException e) {
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " NEW USER FROM INFO: phone: "
                    + phone + ", chat id: " + chatId + ", codeAns: " + a + ".");
            adminController.sendMessageToAdmin(LocalDateTime.now(NSK_ZONE_ID) +" NEW USER FROM INFO: phone: "
                    + phone + ", chat id: " + chatId + ", codeAns: " + a + ".");
            return ResponseEntity.ok(a);
        }
    }

    @GetMapping(value = "/getRules")
    @ResponseBody
    private ResponseEntity<StringResponse> getRules(@RequestParam("gameId") int gameId) {
        System.out.println("Game ID: " + gameId);
        String ans = informationBotDataBaseHandler.getRules(gameId);
        return ResponseEntity.ok(new StringResponse(ans));
    }
}
