package net.hinson820.arcanearbor.common.enchantment.types;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecuteEnchantment extends ArcaneEnchantment {
    public static final String ID = "execute";

    public ExecuteEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".execute"),
                stack -> stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public Map<Integer, Double> getLevelProbabilities() {
        return Map.of(
                1, 0.50, // 50% chance for level 1
                2, 0.35, // 35% chance for level 2
                3, 0.15  // 15% chance for level 3
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
        // Example: Player must have dealt a certain amount of damage recently, or be in a specific dimension
        return true; // Placeholder - implement your logic
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        float thresholdHealthHearts = getExecuteThresholdAbsoluteHealth(level) / 2f; // Convert HP to hearts
        float thresholdPercent = getExecuteThresholdPercentMaxHealth(level) * 100f;

        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".execute.tooltip.description",
                        String.format("%.1f", thresholdHealthHearts),
                        String.format("%.0f%%", thresholdPercent))
                .withStyle(ChatFormatting.RED));
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.EXECUTE_APPLICATION_MANA.get(); // Mana to apply the enchantment
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        // Mana cost per execution attempt
        return switch (level) {
            case 1 -> Configs.COMMON.EXECUTE_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.EXECUTE_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.EXECUTE_L3_ACTIVATION_MANA.get();
            default -> 9999; // Should not happen
        };
    }

    // --- Effect specific methods ---
    public float getExecuteThresholdAbsoluteHealth(int level) { // in HP (half-hearts)
        return switch (level) {
            case 1 -> 4.0f;  // 2 hearts
            case 2 -> 8.0f;  // 4 hearts
            case 3 -> 10.0f; // 5 hearts
            default -> 0.0f;
        };
    }

    public float getExecuteThresholdPercentMaxHealth(int level) { // e.g., 0.05 for 5%
        return switch (level) {
            case 1 -> 0.05f; // 5%
            case 2 -> 0.10f; // 10%
            case 3 -> 0.20f; // 20%
            default -> 0.0f;
        };
    }
}
