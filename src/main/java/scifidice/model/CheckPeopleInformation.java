package scifidice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckPeopleInformation {
    private String infoBotChatID;
    private boolean inExitWindow;
    private boolean isViolate;
    private String phoneNumber;
}
