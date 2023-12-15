package scifidice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReservationResponse {

    private int codeAnswer;

    private String bookId;

    public ReservationResponse(int codeAnswer, String bookId) {
        this.codeAnswer = codeAnswer;
        this.bookId = bookId;

    }
}
