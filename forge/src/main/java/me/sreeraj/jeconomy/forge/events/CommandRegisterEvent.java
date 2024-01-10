package me.sreeraj.jeconomy.forge.events;

import me.sreeraj.jeconomy.commands.JEconomyCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandRegisterEvent {

        @SubscribeEvent
        public void registerCommands(RegisterCommandsEvent e) {
            JEconomyCommands.register(e.getDispatcher());
        }
    }

