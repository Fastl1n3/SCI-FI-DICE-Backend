package scifidice.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Game {
    private int id;
    private String name;
    private String rules;
    private boolean isTaken;

    public Game(int id, String name, String rules) {
        this.id = id;
        this.name = name;
        this.rules = rules;
        isTaken=false;
    }
}
