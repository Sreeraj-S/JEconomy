package me.sreeraj.jeconomy.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;


public class JEconomyCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        new BalanceCommand().register(dispatcher);
        new SetCommand().register(dispatcher);
        new TopCommand().register(dispatcher);
        new SendCommand().register(dispatcher);
        new ModifyCommand().register(dispatcher);
    }
}
