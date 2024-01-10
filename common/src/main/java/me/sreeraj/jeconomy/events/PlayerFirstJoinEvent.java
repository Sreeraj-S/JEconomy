package me.sreeraj.jeconomy.events;

import me.sreeraj.jeconomy.JEconomyUtils;
import me.sreeraj.jeconomy.database.DatabaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Set;

import static me.sreeraj.jeconomy.JEconomy.MOD_ID;
import static me.sreeraj.jeconomy.JEconomyUtils.logPrint;

public class PlayerFirstJoinEvent {
    public static boolean isJoiningWorldForTheFirstTime(Player player, boolean mustHaveEmptyInventory, boolean mustBeCloseToSpawn) {
        String firstjointag =  "jEconomy." + MOD_ID + ".joined";
        String playerName = player.getName().getString();

        Set<String> tags = player.getTags();
        if (tags.contains(firstjointag)) {
            return false;
        }
        logPrint("[" + MOD_ID + "] Added Tag to " + playerName);
        player.addTag(firstjointag);

        if (mustHaveEmptyInventory) {
            Inventory inv = player.getInventory();
            boolean isempty = true;
            for (int i = 0; i < 36; i++) {
                if (!inv.getItem(i).isEmpty()) {
                    isempty = false;
                    break;
                }
            }

            if (!isempty) {
                logPrint("[" + MOD_ID + "] Inventory of " + playerName + " is not empty, first join is false.");
                return false;
            }
        }

        if (mustBeCloseToSpawn) {
            Level level = player.getCommandSenderWorld();
            ServerLevel serverLevel = (ServerLevel) level;
            ServerPlayer serverPlayer = (ServerPlayer) player;

            BlockPos spawnPos = serverPlayer.getRespawnPosition();
            if (spawnPos == null) {
                spawnPos = serverLevel.getSharedSpawnPos();
            }
            logPrint("[" + MOD_ID + "] Checking for first join of " + playerName + " with spawn position: " + spawnPos.toShortString());

            BlockPos playerPos = player.blockPosition();
            BlockPos checkPos = new BlockPos(playerPos.getX(), spawnPos.getY(), playerPos.getZ());

            int spawnRadius = serverLevel.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).get();
            logPrint("[" + MOD_ID + "] Checking for first join of " + playerName + " with spawn radius: " + spawnRadius);

            return checkPos.closerThan(spawnPos, spawnRadius * 2);
        }

        return true;
    }
    public static void onSpawn(Level world, Entity entity) {
        if (world.isClientSide) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player)entity;
        if (isJoiningWorldForTheFirstTime(player,true,true)) {
            DatabaseManager dm = JEconomyUtils.getDatabaseManager();
            String uuid = player.getStringUUID();
            String name = player.getName().getString();
            dm.addPlayer(uuid, name);
            dm.setName(uuid, name);
            logPrint("Player "+player.getName()+" Joined for first time and added money");
        }
    }
}
