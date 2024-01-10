package me.sreeraj.jeconomy.commands;


import com.mojang.brigadier.CommandDispatcher;
import me.sreeraj.jeconomy.JEconomyUtils;
import me.sreeraj.jeconomy.database.DatabaseManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;



public class BalanceCommand {


    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bal")
                        .executes((command)->{balanceCommand(command.getSource());return 1;})
        );
    }

    private int balanceCommand(CommandSourceStack source) {
        if (source.getPlayer() != null) {
            Player player = source.getPlayer();
            DatabaseManager db = JEconomyUtils.getDatabaseManager();
            String msg = "";;
            int bal = db.getBalanceFromName(player.getName().getString());
//            ctx.getSource().sendSuccess(() -> Component.literal((bal > -1) ? (player + " has $" + bal) : ("No account was found for player with the name \"" + player + "\"")), false);
            if (bal > -1) {
                msg= player.getName().getString() + " has $" + bal;
            } else {
                msg ="No account was found for player with the name \"" + player.getName().getString() + "\"";
            }
            MutableComponent message = Component.literal(msg);
            source.sendSuccess(()->{return message;},false);
        }
        return 1;
    }
}
