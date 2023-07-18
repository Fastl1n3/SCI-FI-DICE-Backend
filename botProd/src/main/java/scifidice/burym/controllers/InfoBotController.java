package scifidice.burym.controllers;

import scifidice.burym.infoBot.RestService;
import scifidice.burym.infoBot.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info")
public class InfoBotController {

    @Autowired
    private RestService restService;

    @GetMapping(value = "/postChatId")
    @ResponseBody
    public ResponseEntity<String> postChatID(@RequestParam("phone") String phone, @RequestParam("chatId") int chatId) {     //тут обязательно нужны параметры, если не стоит required=false
        System.out.println(phone + " user id: " + chatId);
        restService.sendMessageToInfoBot(532823299);
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping(value = "/getRules")
    @ResponseBody
    public ResponseEntity<StringResponse> getRules(@RequestParam("gameId") int gameId) {     //тут обязательно нужны параметры, если не стоит required=false
        System.out.println("Game ID: " + gameId);
        //TODO даем gameId нам возвращается в строку ее правила
        String ans = switch (gameId) {
            case 1 -> "Жопа";
            case 2 -> "Игра для обмазывания в масле";
            case 3 -> "Для двух игроков на весь день";
            default -> "Жесть какая-то";
        };
        return ResponseEntity.ok(new StringResponse(ans));
    }
}
