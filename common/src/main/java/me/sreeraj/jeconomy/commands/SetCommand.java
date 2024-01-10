package me.sreeraj.jeconomy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.sreeraj.jeconomy.JEconomyUtils;
import me.sreeraj.jeconomy.config.JEconomyConfig;
import me.sreeraj.jeconomy.database.DatabaseManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class SetCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register( Commands.literal("set")
                .requires((permission) -> permission.hasPermission(JEconomyConfig.opCommandsPermissionLevel))
                .then(
                        Commands.argument("players", EntityArgument.players())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return setCommand(e, EntityArgument.getPlayers(e, "players").stream().toList(), amount);
                                                }))
                )
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer(0))
                                .then(
                                        Commands.argument("shouldModifyAll", BoolArgumentType.bool())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    boolean shouldModifyAll = BoolArgumentType.getBool(e, "shouldModifyAll");
                                                    return setCommand(e, amount, shouldModifyAll);
                                                })
                                )
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return setCommand(e, amount, false);
                                })
                ));
    }

    public static int setCommand(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, int amount) {
        DatabaseManager dm = JEconomyUtils.getDatabaseManager();
        players.forEach(player -> dm.setBalance(player.getStringUUID(), amount));
        ctx.getSource().sendSuccess(() -> Component.literal("Updated balance of " + players.size() + " players to " + amount), true);
        return players.size();
    }

    public static int setCommand(CommandContext<CommandSourceStack> ctx, int amount, boolean shouldModifyAll) throws CommandSyntaxException {
        if (shouldModifyAll) {
            JEconomyUtils.getDatabaseManager().setAllBalance(amount);
            ctx.getSource().sendSuccess(() -> Component.literal("All accounts balance to " + amount), true);
        } else {
            JEconomyUtils.getDatabaseManager().setBalance(ctx.getSource().getPlayerOrException().getStringUUID(), amount);
            ctx.getSource().sendSuccess(() -> Component.literal("Updated your balance to " + amount), true);
        }
        return 1;
    }
}
