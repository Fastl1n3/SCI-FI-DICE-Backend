package scifidice.burym.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scifidice.burym.infoBot.StringResponse;
import scifidice.levachev.DataBaseHandler.InformationBotDataBaseHandler;
import scifidice.levachev.Model.Game;

import java.time.LocalDateTime;
import java.util.List;

import static scifidice.burym.config.SpringConfig.NSK_ZONE_ID;

@RestController
@RequestMapping("/info")
public class InfoBotController {
    @Autowired
    private InformationBotDataBaseHandler informationBotDataBaseHandler;

    @GetMapping(value = "/postChatId")
    @ResponseBody
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
    @ResponseBody
    private StringResponse getRules(@RequestParam("gameId") int gameId) {
        String ans = informationBotDataBaseHandler.getRules(gameId);
        return new StringResponse(ans);
    }

    @GetMapping(value = "/getRulesList")
    @ResponseBody
    private List<Game> getRulesList() {
        List<Game> ans = informationBotDataBaseHandler.getGames();

        return ans;
    }
}
