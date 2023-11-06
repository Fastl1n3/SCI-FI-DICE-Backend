package scifidice.levachev.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientInformation {
    private int roomNumber;
    private int beginTime;
    private int endTime;
    private String password;
    private ReceptionCodeAnswer codeAnswer;
}
