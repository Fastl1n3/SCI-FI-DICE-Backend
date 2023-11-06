package scifidice.levachev.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private int id;
    private String name;
    private String rules;
    private boolean isTaken;

    public Game() {
    }

    public Game(int id, String name, String rules) {
        this.id = id;
        this.name = name;
        this.rules = rules;
        isTaken=false;
    }
}
