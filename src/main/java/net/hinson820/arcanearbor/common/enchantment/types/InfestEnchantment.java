package net.hinson820.arcanearbor.common.enchantment.types;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfestEnchantment extends ArcaneEnchantment {
    public static final String ID = "infest";

    public InfestEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".infest"),
                stack -> stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public Map<Integer, Double> getLevelProbabilities() {
        return Map.of(
                1, 0.50,
                2, 0.35,
                3, 0.15
        );
    }

    @Override
    public double getOverallSuccessChance(ItemStack stack, Level world, LivingEntity enchantingPlayer) {
        return 0.70;
    }

    @Override
    public double getBreakChanceOnFailure(ItemStack stack, Level world, LivingEntity enchantingPlayer) {
        return 1.0;
    }


    @Override
    public boolean checkEnchantmentPrerequisites(Level world, LivingEntity enchantingPlayer) {
        // Example: Player has used a certain number of tipped arrows or is near a spawner
        return true; // Placeholder
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        String targets;
        if (level == 3) {
            targets = Component.translatable("enchantment." + ArcaneArbor.MODID + ".infest.tooltip.targets.all").getString();
        } else {
            targets = String.valueOf(getMaxSpreadTargets(level));
        }

        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".infest.tooltip.description",
                        targets, getSpreadRadius(level))
                .withStyle(ChatFormatting.DARK_GREEN));
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.INFEST_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        return switch (level) {
            case 1 -> Configs.COMMON.INFEST_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.INFEST_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.INFEST_L3_ACTIVATION_MANA.get();
            default -> 9999;
        };
    }

    public int getSpreadRadius(int level) {
        return switch (level) {
            case 1 -> 5;
            case 2 -> 8;
            case 3 -> 10;
            default -> 0;
        };
    }

    public int getMaxSpreadTargets(int level) {
        // For level 3, "all" is handled by using a very high number or by specific logic in handler
        return switch (level) {
            case 1 -> 3;
            case 2 -> 8;
            case 3 -> Integer.MAX_VALUE; // Represents "all" within radius
            default -> 0;
        };
    }
}
