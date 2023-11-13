package scifidice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scifidice.model.StringResponse;
import scifidice.DataBaseHandler.InformationBotDataBaseHandler;
import scifidice.Entity.Game;

import java.time.LocalDateTime;
import java.util.List;

import static scifidice.config.SpringConfig.NSK_ZONE_ID;

@RestController
@RequestMapping("/backend/info")
public class InfoBotController {
    @Autowired
    private InformationBotDataBaseHandler informationBotDataBaseHandler;

    @GetMapping(value = "/postChatId")
    private ResponseEntity<Integer> postChatID(@RequestParam("phone") String phone, @RequestParam("chatId") String chatId) {
        int a = -1;
        try {
            a = informationBotDataBaseHandler.authorization(phone, chatId);
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) +" NEW USER FROM INFO: phone: "
                    + phone + ", chat id: " + chatId + ", codeAns: " + a + ".");
            return ResponseEntity.ok(a);
        }
        catch (DataAccessException e) {
            System.out.println(LocalDateTime.now(NSK_ZONE_ID) + " NEW USER FROM INFO: phone: "
                    + phone + ", chat id: " + chatId + ", codeAns: " + a + ".");
            return ResponseEntity.ok(a);
        }
    }

    @GetMapping(value = "/getRules")
    private StringResponse getRules(@RequestParam("gameId") int gameId) {
        return new StringResponse(informationBotDataBaseHandler.getRules(gameId));
    }

    @GetMapping(value = "/getRulesList")
    private List<Game> getRulesList() {
        return informationBotDataBaseHandler.getGames();
    }
}
