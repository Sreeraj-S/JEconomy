package me.sreeraj.jeconomy.forge;

import dev.architectury.platform.forge.EventBuses;
import me.sreeraj.jeconomy.JEconomy;
import me.sreeraj.jeconomy.forge.events.CommandRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(JEconomy.MOD_ID)
public class JEconomyForge {
    public JEconomyForge() {
		// Submit our event bus to let architectury register our content on the right time
        IEventBus eventBus =FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::loadCommandRegister);
        EventBuses.registerModEventBus(JEconomy.MOD_ID, eventBus);

        JEconomy.init();
    }
    private void loadCommandRegister(final FMLLoadCompleteEvent event) {
        MinecraftForge.EVENT_BUS.register(new CommandRegisterEvent());
    }
}