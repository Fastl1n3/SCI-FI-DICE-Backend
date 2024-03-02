package scifidice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AdminMessage {
    
    AdminMessageType messageType;
    
    String message;

    public AdminMessage(AdminMessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }
}
