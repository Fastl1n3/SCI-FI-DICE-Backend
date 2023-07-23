package scifidice.burym.infoBot;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestService {
    public static final String INFO_BOT_TOKEN = "6332455018:AAHmfZ4RLi64X1GKkrszsaUDoSTX14ijFno";

    private final RestTemplate restTemplate;
    public RestService() {
        this.restTemplate = new RestTemplate();
    }

    public String sendMessageToInfoBot(int chatId) {
        String url = "https://api.telegram.org/bot"+ INFO_BOT_TOKEN + "/sendMessage?chat_id="+ chatId + "&text=Привет";
        return this.restTemplate.getForObject(url, String.class);
    }
}
