package me.sreeraj.jeconomy;

import me.sreeraj.jeconomy.database.DatabaseManager;
import me.sreeraj.jeconomy.database.sqlite.SqliteDatabaseManager;



public class JEconomyUtils {

    public static boolean DEBUGMODE = false;
    public static void logPrint(String message){
        if(DEBUGMODE){
            JEconomy.LOGGER.info(message);
        }
    }
    public static void registerTable(String query){JEconomy.TableReg.add(query);}

    public static DatabaseManager getDatabaseManager() {
        return new SqliteDatabaseManager();
    }
}
