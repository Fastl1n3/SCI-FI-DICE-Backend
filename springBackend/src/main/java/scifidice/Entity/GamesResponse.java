package scifidice.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import scifidice.db.entities.Game;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GamesResponse {
    private List<Game> games;

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public String toString() {
        return "GameResponse{" +
                "games=" + games +
                '}';
    }
}
