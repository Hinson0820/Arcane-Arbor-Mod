package net.hinson820.arcanearbor.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.hinson820.arcanearbor.common.mana.PlayerMana;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ManaCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("arcanemana")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setcurrent")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ManaCommands::executeSetCurrentMana)
                                )
                        )
                )
                .then(Commands.literal("setmax")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(PlayerMana.MIN_MANA_CAP))
                                        .executes(ManaCommands::executeSetMaxMana)
                                )
                        )
                )
                .then(Commands.literal("fill")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ManaCommands::executeFillMana)
                        )
                )
                .then(Commands.literal("fill")
                        .executes(ManaCommands::executeFillSelfMana)
                )
        );
    }

    private static int executeSetCurrentMana(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        ManaManager.setMana(targetPlayer, amount);

        context.getSource().sendSuccess(() -> Component.translatable(
                "commands." + ArcaneArbor.MODID + ".mana.setcurrent.success",
                targetPlayer.getDisplayName(),
                amount
        ), true);
        return 1;
    }

    private static int executeSetMaxMana(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        ManaManager.setMaxMana(targetPlayer, amount);

        context.getSource().sendSuccess(() -> Component.translatable(
                "commands." + ArcaneArbor.MODID + ".mana.setmax.success",
                targetPlayer.getDisplayName(),
                amount
        ), true);
        return 1;
    }

    private static int executeFillMana(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        PlayerMana manaData = ManaManager.getManaData(targetPlayer);

        ManaManager.setMana(targetPlayer, manaData.getMaxMana());

        context.getSource().sendSuccess(() -> Component.translatable(
                "commands." + ArcaneArbor.MODID + ".mana.fill.success",
                targetPlayer.getDisplayName(),
                manaData.getMaxMana()
        ), true);
        return 1;
    }

    private static int executeFillSelfMana(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (context.getSource().getEntity() instanceof ServerPlayer selfPlayer) {
            PlayerMana manaData = ManaManager.getManaData(selfPlayer);
            ManaManager.setMana(selfPlayer, manaData.getMaxMana());
            context.getSource().sendSuccess(() -> Component.translatable(
                    "commands." + ArcaneArbor.MODID + ".mana.fill.self.success",
                    manaData.getMaxMana()
            ), true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("commands." + ArcaneArbor.MODID + ".error.not_a_player"));
            return 0;
        }
    }
}
