package me.sreeraj.jeconomy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;


public class JEconomyConfig {

    Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();


    public static final File configFolder = new File(System.getProperty("user.dir") + "/config/JEconomy");
    public static final File sqliteFolder = new File(System.getProperty("user.dir") + "/config/JEconomy/sqlite");

    public static final File configFile = new File(configFolder, "config.json");

    public static String sqliteFileConfig = "JEconomy.sqlite";
    public static int startingMoney = 500;
    public static int COMMAND_BALANCE_PERMISSION_LEVEL = 2;

    public static int opCommandsPermissionLevel=4;
    public JEconomyConfig(){init();
    }
    public void init() {
        System.out.println("JEconomy config -> " + configFolder.getAbsolutePath());
        if (!configFolder.exists()) {
            configFolder.mkdirs();
            createConfig();
        } else if (!configFile.exists()) {
            createConfig();
        }
        if (!sqliteFolder.exists()) {
            sqliteFolder.mkdirs();
        }
        try {
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            JsonObject obj = GSON.fromJson(new FileReader(configFile), JsonObject.class);
            JsonObject databaseConfig = obj.get("Config").getAsJsonObject();
            HashMap<String, String> databaseMap = GSON.fromJson(databaseConfig, type);
            sqliteFileConfig = databaseMap.getOrDefault("SQLiteDatabaseName", "JEconomy.sqlite");
            startingMoney = Integer.parseInt(databaseMap.getOrDefault("StartingMoney", String.valueOf(500)));
        } catch (Exception e) {
            throw new RuntimeException("2->"+e);
        }
    }

    private void createConfig() {
        try {
            configFile.createNewFile();
            JsonWriter writer = GSON.newJsonWriter(new FileWriter(configFile));
            writer.beginObject()
                    .name("Config")
                    .beginObject()
                    .name("SQLiteDatabaseName")
                    .value("JEconomy.sqlite")
                    .name("StartingMoney")
                    .value(500)
                    .endObject()
                    .endObject()
                    .flush();
        } catch (IOException e) {
            throw new RuntimeException("1->"+e);
        }
    }

    public boolean isSQLiteDatabaseExist(){
        return new File(sqliteFolder,sqliteFileConfig).exists();
    }

    public File getFileLocation(){
       return new File(sqliteFolder,sqliteFileConfig);
    }

}
