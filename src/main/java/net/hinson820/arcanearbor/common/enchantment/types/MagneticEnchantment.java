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

public class MagneticEnchantment extends ArcaneEnchantment {
    public static final String ID = "magnetic";

    public MagneticEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".magnetic"),
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
        // Example: Player has been near an active lodestone or has crafted compasses
        return true; // Placeholder
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        if (level == 1) {
            tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".magnetic.tooltip.description_l1")
                    .withStyle(ChatFormatting.BLUE));
        } else {
            tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".magnetic.tooltip.description_lx",
                            getPullRadius(level))
                    .withStyle(ChatFormatting.BLUE));
        }
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.MAGNETIC_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        return switch (level) {
            case 1 -> Configs.COMMON.MAGNETIC_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.MAGNETIC_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.MAGNETIC_L3_ACTIVATION_MANA.get();
            default -> 9999;
        };
    }

    public int getPullRadius(int level) { // Radius around the *initial target* to select other entities
        return switch (level) {
            case 1 -> 0;  // Only affects the hit target
            case 2 -> 3;
            case 3 -> 6;
            default -> 0;
        };
    }

    public double getPullStrength() {
        return 1.5; // e.g., 0.5 to 1.5
    }

    public double getPullDestinationOffset() {
        return 4.0; // Blocks in front of the player
    }
}
