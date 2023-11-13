package scifidice.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Game {
    private int gameId;
    private String name;
    private String rules;
    private boolean isTaken;

    public Game(String name, String rules) {
        this.name = name;
        this.rules = rules;
    }
}
