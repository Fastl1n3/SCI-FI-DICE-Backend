package scifidice.burym.controllers;

import org.springframework.dao.DataAccessException;
import scifidice.burym.infoBot.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scifidice.levachev.DataBaseHandler.InformationBotDataBaseHandler;

@RestController
@RequestMapping("/info")
public class InfoBotController {
    @Autowired
    private InformationBotDataBaseHandler informationBotDataBaseHandler;

    @GetMapping(value = "/postChatId")
    @ResponseBody
    private ResponseEntity<Integer> postChatID(@RequestParam("phone") String phone, @RequestParam("chatId") String chatId) {     //тут обязательно нужны параметры, если не стоит required=false
        System.out.println("NEW USER FROM INFO: " + phone + " user id: " + chatId);
        int a = -1;
        try {
            a = informationBotDataBaseHandler.authorization(phone, chatId);
            System.out.println(a);
            return ResponseEntity.ok(a);
        }
        catch (DataAccessException e) {
            return ResponseEntity.ok(a);
        }
    }

    @GetMapping(value = "/getRules")
    @ResponseBody
    private ResponseEntity<StringResponse> getRules(@RequestParam("gameId") int gameId) {     //тут обязательно нужны параметры, если не стоит required=false
        System.out.println("Game ID: " + gameId);
        String ans = informationBotDataBaseHandler.getRules(gameId);
        return ResponseEntity.ok(new StringResponse(ans));
    }
}
