package ru.levachev.DataBaseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class InitializationDBHandler {
    private final JdbcTemplate jdbcTemplateGamesDB;
    private final JdbcTemplate jdbcTemplateOrganisationDB;

    @Autowired
    public InitializationDBHandler(JdbcTemplate jdbcTemplateGamesDB, JdbcTemplate jdbcTemplateOrganisationDB) {
        this.jdbcTemplateGamesDB = jdbcTemplateGamesDB;
        this.jdbcTemplateOrganisationDB=jdbcTemplateOrganisationDB;
    }

    public void defaultInitialization(){
        initGamesTable();
        initRoomTable();
    }

    private void initGamesTable(){

    }

    private void initRoomTable(){

    }
}
