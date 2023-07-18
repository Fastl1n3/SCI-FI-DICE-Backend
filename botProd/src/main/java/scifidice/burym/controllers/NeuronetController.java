package scifidice.burym.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neuronet")
public class NeuronetController {
    @GetMapping(value = "/setPeople")
    @ResponseBody
    public String getDate(@RequestParam("room") int room, @RequestParam("people") int people) {     //тут обязательно нужны параметры, если не стоит required=false
        System.out.println("КОЛ_ВО ЛЮДЕЙ " + people + " В " + room);
        return "SUCCES";
    }
}
