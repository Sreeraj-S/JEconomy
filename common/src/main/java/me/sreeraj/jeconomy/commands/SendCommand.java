package me.sreeraj.jeconomy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.sreeraj.jeconomy.JEconomyUtils;
import me.sreeraj.jeconomy.database.DatabaseManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SendCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register( Commands.literal("send")
                .then(
                        Commands.argument("player", EntityArgument.player())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(e -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(e, "player");
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return sendCommand(e, player, e.getSource().getPlayerOrException(), amount);
                                                }))));
    }

    public static int sendCommand(CommandContext<CommandSourceStack> ctx, ServerPlayer player, ServerPlayer player1, int amount) throws CommandSyntaxException {
        DatabaseManager dm = JEconomyUtils.getDatabaseManager();
        long bal = dm.getBalanceFromUUID(player.getStringUUID()) + amount;
        if(player!=player1){
            ctx.getSource().sendFailure(Component.literal("Insufficient Balance To Transfer!"));
            return 0;
        }
        if (amount>bal){return 0;}
        if (bal < Integer.MAX_VALUE && dm.changeBalance(player1.getStringUUID(), -amount)) {
            dm.changeBalance(player.getStringUUID(), amount);
            player.displayClientMessage(Component.literal("You received $" + amount + " from " + player1.getName().getString()), false);
            ctx.getSource().sendSuccess(() -> Component.literal("Sent $" + amount + " to " + player.getName().getString()), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Failed because that would go over the max value"), false);
        }
        return 1;
    }
}
