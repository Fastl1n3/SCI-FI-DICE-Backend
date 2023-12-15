package scifidice.infoBot;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Notification {
    private final String INFO_BOT_TOKEN = "6332455018:AAHmfZ4RLi64X1GKkrszsaUDoSTX14ijFno";
    private final RestTemplate restTemplate;

    public Notification() {
        this.restTemplate = new RestTemplate();
    }

    public void sendMessageToInfoBot(String chatId, String message) {
        String url = "https://api.telegram.org/bot"+ INFO_BOT_TOKEN + "/sendMessage?chat_id="+ chatId + "&text=" + message;
        this.restTemplate.getForObject(url, String.class);
    }
}
