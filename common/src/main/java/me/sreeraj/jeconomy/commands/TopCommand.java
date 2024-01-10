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
import net.minecraft.network.chat.Component;

public class TopCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register( Commands.literal("top")
                .then(
                        Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(e -> {
                                    int page = IntegerArgumentType.getInteger(e, "page");
                                    return topCommand(e, page);
                                })
                )
                .executes(e -> topCommand(e, 1)));
    }

    public static int topCommand(CommandContext<CommandSourceStack> ctx, int page) throws CommandSyntaxException {
        DatabaseManager dm = JEconomyUtils.getDatabaseManager();
        String output = dm.top(ctx.getSource().getPlayerOrException().getStringUUID(), page);
        ctx.getSource().sendSuccess(() -> Component.literal(output), false);
        return 1;
    }
}
