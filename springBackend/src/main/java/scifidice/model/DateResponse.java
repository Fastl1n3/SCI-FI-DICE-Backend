package scifidice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateResponse {

    private int codeAnswer;

    private String dateStr;

    public DateResponse(int codeAnswer, String date) {
        this.codeAnswer = codeAnswer;
        this.dateStr = date;
    }
}
