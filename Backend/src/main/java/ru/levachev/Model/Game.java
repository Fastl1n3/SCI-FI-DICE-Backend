package ru.levachev.Model;

public class Game {
    private int gameID;
    private String gameName;
    private String gameRules;
    private boolean isTaken;

    public Game() {
    }

    public Game(int gameID, String gameName, String gameRules) {
        this.gameID = gameID;
        this.gameName=gameName;
        this.gameRules = gameRules;
        isTaken=false;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameRules() {
        return gameRules;
    }

    public void setGameRules(String gameRules) {
        this.gameRules = gameRules;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
