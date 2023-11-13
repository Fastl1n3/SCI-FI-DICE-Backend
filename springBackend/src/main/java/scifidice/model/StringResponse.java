package scifidice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StringResponse {

    private String message;

    public StringResponse(String message) {
        this.message = message;
    }
}
