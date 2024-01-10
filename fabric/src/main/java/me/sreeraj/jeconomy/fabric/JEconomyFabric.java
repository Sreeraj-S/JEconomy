package me.sreeraj.jeconomy.fabric;

import me.sreeraj.jeconomy.JEconomy;
import me.sreeraj.jeconomy.commands.JEconomyCommands;
import me.sreeraj.jeconomy.events.PlayerFirstJoinEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class JEconomyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        JEconomy.init();
        loadEvents();
    }

    private void loadEvents(){
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, environment)-> JEconomyCommands.register(dispatcher));
        ServerLifecycleEvents.SERVER_STARTING.register(JEconomy::onInitServer);
        ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerLevel world) -> {
            PlayerFirstJoinEvent.onSpawn(world, entity);
        });
    }
}