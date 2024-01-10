package me.sreeraj.jeconomy.database;

import java.io.File;
import java.sql.Connection;

public interface DatabaseManager {
    static void createNewDatabase(File file) {}

    Connection connect();

    void addPlayer(String uuid, String name);
    void removePlayer(String uuid, String name);
    void updateName(String uuid, String name);
    void setName(String uuid, String name);
    String getNameFromUUID(String uuid);

    int getBalanceFromUUID(String uuid);
    int getBalanceFromName(String name);
    boolean setBalance(String uuid, int money);
    void setAllBalance(int money);
    boolean changeBalance(String uuid, int money);
    void changeAllBalance(int money);

    String top(String uuid, int page);
    String rank(int rank);
    int playerRank(String uuid);
}

