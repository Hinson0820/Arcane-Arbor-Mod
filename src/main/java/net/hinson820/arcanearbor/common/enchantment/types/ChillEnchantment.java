package net.hinson820.arcanearbor.common.enchantment.types;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChillEnchantment extends ArcaneEnchantment {
    public static final String ID = "chill";

    public ChillEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".chill"),
                stack -> stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem);
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
        return true; // Placeholder
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        int durationSeconds = getSlownessDurationTicks(level) / 20;
        // Amplifiers are 0-indexed, so Slowness I is amplifier 0. Add 1 for display.
        String slownessLevelRoman = getRomanNumeral(getSlownessAmplifier(level) + 1);

        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".chill.tooltip.description",
                        Component.translatable(MobEffects.MOVEMENT_SLOWDOWN.getRegisteredName()), // "Slowness"
                        slownessLevelRoman,
                        durationSeconds)
                .withStyle(ChatFormatting.AQUA));
        return tooltipLines;
    }

    // Helper for Roman numerals in tooltip (optional, or use vanilla's way if available)
    private String getRomanNumeral(int number) {
        if (number == 1) return "I";
        if (number == 2) return "II";
        if (number == 3) return "III";
        // Add more if needed, or use a more robust converter
        return String.valueOf(number);
    }


    @Override
    public int getManaCost() {
        return Configs.COMMON.CHILL_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        // Mana cost per Chill application
        return switch (level) {
            case 1 -> Configs.COMMON.CHILL_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.CHILL_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.CHILL_L3_ACTIVATION_MANA.get();
            default -> 9999;
        };
    }

    public int getSlownessDurationTicks(int level) {
        return switch (level) {
            case 1 -> 20; // 1 second
            case 2 -> 20; // 1 second
            case 3 -> 40; // 2 seconds
            default -> 0;
        };
    }

    public int getSlownessAmplifier(int level) { // 0 for Slowness I, 1 for Slowness II
        return switch (level) {
            case 1 -> 0; // Slowness I
            case 2 -> 1; // Slowness II
            case 3 -> 1; // Slowness II
            default -> 0;
        };
    }
}