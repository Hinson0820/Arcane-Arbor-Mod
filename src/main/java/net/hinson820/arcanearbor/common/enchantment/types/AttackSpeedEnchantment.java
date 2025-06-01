package net.hinson820.arcanearbor.common.enchantment.types;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttackSpeedEnchantment extends ArcaneEnchantment {
    public static final String ID = "rageblade";

    public AttackSpeedEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".rageblade"),
                stack -> stack.getItem() instanceof SwordItem); // Only for swords
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public Map<Integer, Double> getLevelProbabilities() {
        return Map.of(
                1, 0.40,
                2, 0.30,
                3, 0.20,
                4, 0.10
        );
    }

    @Override
    public double getOverallSuccessChance(ItemStack stack, Level world, LivingEntity enchantingPlayer)  {
        return 0.70;
    }

    @Override
    public double getBreakChanceOnFailure(ItemStack stack, Level world, LivingEntity enchantingPlayer) {
        return 1.0; // Always breaks on failure
    }

    @Override
    public boolean checkEnchantmentPrerequisites(Level world, LivingEntity enchantingPlayer) {
        return true; // Placeholder
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        float reductionPerStackPercent = getCooldownReductionPerStack(level) * 100f;
        int maxStacks = getMaxStacks(level);
        float totalMaxReductionPercent = reductionPerStackPercent * maxStacks;

        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".rageblade.tooltip.description1",
                        String.format("%.0f%%", reductionPerStackPercent), maxStacks)
                .withStyle(ChatFormatting.YELLOW));
        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".rageblade.tooltip.description2",
                        String.format("%.0f%%", totalMaxReductionPercent))
                .withStyle(ChatFormatting.YELLOW));
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.RAGEBLADE_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        // Mana cost per hit to gain/refresh a stack
        return switch (level) {
            case 1 -> Configs.COMMON.RAGEBLADE_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.RAGEBLADE_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.RAGEBLADE_L3_ACTIVATION_MANA.get();
            case 4 -> Configs.COMMON.RAGEBLADE_L4_ACTIVATION_MANA.get();
            default -> 9999;
        };
    }

    public float getCooldownReductionPerStack(int level) {
        return switch (level) {
            case 1 -> 0.10f; // 10%
            case 2 -> 0.15f; // 15%
            case 3 -> 0.15f; // 15%
            case 4 -> 0.18f; // 18%
            default -> 0.0f;
        };
    }

    public int getMaxStacks(int level) {
        return switch (level) {
            case 1 -> 3;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            default -> 0;
        };
    }

    public int getStackDecayTicks(int level) {
        // How long stacks last without attacking (e.g., 3 seconds)
        return Configs.COMMON.RAGEBLADE_STACK_DECAY_TICKS.get(); // Example: 60 ticks
    }
}
