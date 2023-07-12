package ru.levachev.Model;

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

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
