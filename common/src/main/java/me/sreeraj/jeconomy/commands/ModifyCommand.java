package me.sreeraj.jeconomy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.sreeraj.jeconomy.JEconomyUtils;
import me.sreeraj.jeconomy.config.JEconomyConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ModifyCommand {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("modify")
                .requires((permission) -> permission.hasPermission(JEconomyConfig.opCommandsPermissionLevel))
                .then(Commands.literal("add").then(
                        Commands.argument("players", EntityArgument.players())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return modifyAddCommand(e, EntityArgument.getPlayers(e, "players").stream().toList(), amount);
                                                })))
                )
                .then(Commands.literal("add").then(
                        Commands.argument("amount", IntegerArgumentType.integer())
                                .then(
                                        Commands.argument("shouldModifyAll", BoolArgumentType.bool())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    boolean shouldModifyAll = BoolArgumentType.getBool(e, "shouldModifyAll");
                                                    return modifyAddCommand(e, amount, shouldModifyAll);
                                                })
                                )
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return modifyAddCommand(e, amount, false);
                                }))
                )
                .then(Commands.literal("remove").then(
                        Commands.argument("players", EntityArgument.players())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return modifyRemoveCommand(e, EntityArgument.getPlayers(e, "players").stream().toList(), amount);
                                                })))
                )
                .then(Commands.literal("remove").then(
                        Commands.argument("amount", IntegerArgumentType.integer())
                                .then(
                                        Commands.argument("shouldModifyAll", BoolArgumentType.bool())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    boolean shouldModifyAll = BoolArgumentType.getBool(e, "shouldModifyAll");
                                                    return modifyRemoveCommand(e, amount, shouldModifyAll);
                                                })
                                )
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return modifyRemoveCommand(e, amount, false);
                                }))
                )
        );
    }
    public static int modifyAddCommand(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, int amount) {
        players.forEach(player -> ctx.getSource().sendSuccess(() -> Component.literal((JEconomyUtils.getDatabaseManager().changeBalance(player.getStringUUID(), amount)) ? ("Modified " + players.size() + " players money by $" + amount) : ("That would go out of the valid money range for " + player.getName().getString())), true));
        return players.size();
    }

    public static int modifyAddCommand(CommandContext<CommandSourceStack> ctx, int amount, boolean shouldModifyAll) throws CommandSyntaxException {
        if (shouldModifyAll) {
            JEconomyUtils.getDatabaseManager().changeAllBalance(amount);
            ctx.getSource().sendSuccess(() -> Component.literal(("Modified everyones account by $" + amount)), true);
        } else {
            String output = (JEconomyUtils.getDatabaseManager().changeBalance(ctx.getSource().getPlayerOrException().getStringUUID(), amount)) ? ("Modified your money by $" + amount) : ("That would go out of your valid money range");
            ctx.getSource().sendSuccess(() -> Component.literal(output), true);
        }
        return 1;
    }

    public static int modifyRemoveCommand(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, int amount) {
        players.forEach(player -> ctx.getSource().sendSuccess(() -> Component.literal((JEconomyUtils.getDatabaseManager().changeBalance(player.getStringUUID(), -amount)) ? ("Modified " + players.size() + " players money by $" + amount) : ("That would go out of the valid money range for " + player.getName().getString())), true));
        return players.size();
    }

    public static int modifyRemoveCommand(CommandContext<CommandSourceStack> ctx, int amount, boolean shouldModifyAll) throws CommandSyntaxException {
        if (shouldModifyAll) {
            JEconomyUtils.getDatabaseManager().changeAllBalance(-amount);
            ctx.getSource().sendSuccess(() -> Component.literal(("Modified everyones account by $" + amount)), true);
        } else {
            String output = (JEconomyUtils.getDatabaseManager().changeBalance(ctx.getSource().getPlayerOrException().getStringUUID(), amount)) ? ("Modified your money by $" + amount) : ("That would go out of your valid money range");
            ctx.getSource().sendSuccess(() -> Component.literal(output), true);
        }
        return 1;
    }


}
