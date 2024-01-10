package me.sreeraj.jeconomy;

import me.sreeraj.jeconomy.config.JEconomyConfig;
import me.sreeraj.jeconomy.database.sqlite.SqliteDatabaseManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static me.sreeraj.jeconomy.JEconomyUtils.DEBUGMODE;

public class JEconomy
{
	public static final String MOD_ID = "JEconomy";

	private static final JEconomyConfig config = new JEconomyConfig();
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ArrayList<String> TableReg = new ArrayList<>();
	public static void onInitServer(MinecraftServer minecraftServer){
		JEconomyUtils.registerTable("CREATE TABLE IF NOT EXISTS JEconomy(UUID text PRIMARY KEY, Name text NOT NULL, Money integer DEFAULT 0);");
		SqliteDatabaseManager.createNewDatabase(config.getFileLocation());
	}
	public static void init() {
		LOGGER.info(MOD_ID+" Starting...");
		DEBUGMODE = true;
		new JEconomyConfig();
	}
}
