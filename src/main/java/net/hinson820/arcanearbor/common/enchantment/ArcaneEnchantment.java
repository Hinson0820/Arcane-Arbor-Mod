package net.hinson820.arcanearbor.common.enchantment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class ArcaneEnchantment {
    private final String id;
    private final Component baseDisplayName;
    private final Predicate<ItemStack> applicableTo;

    public ArcaneEnchantment(String id, Component baseDisplayName, Predicate<ItemStack> applicableTo) {
        this.id = id;
        this.baseDisplayName = baseDisplayName;
        this.applicableTo = applicableTo;
    }

    public String getId() {
        return id;
    }

    public Component getBaseDisplayName() {
        return baseDisplayName;
    }

    public Component getDisplayName(int level) {
        if (level <= 0) return baseDisplayName.copy().withStyle(ChatFormatting.RED);
        if (getMaxLevel() == 1 && level == 1) return baseDisplayName.copy();

        return Component.translatable("enchantment.level." + level, baseDisplayName);
    }

    public boolean canApplyTo(ItemStack stack) {
        return applicableTo.test(stack);
    }

    public abstract boolean checkEnchantmentPrerequisites(Level world, LivingEntity enchantingPlayer /*, BlockPos ritualCenterPos */);

    public void onEnchanted(ItemStack stack, int level) {

    }

    public abstract List<Component> getTooltip(ItemStack stack, int level);

    public abstract int getManaCost();

    public abstract int getMaxLevel();

    public abstract double getOverallSuccessChance(ItemStack stack, Level world, LivingEntity enchantingPlayer);

    public abstract double getBreakChanceOnFailure(ItemStack stack, Level world, LivingEntity enchantingPlayer);

    public abstract Map<Integer, Double> getLevelProbabilities();

    public abstract int getActivationManaCost(int level, ItemStack stack, LivingEntity user);

    public int rollLevel() {
        Map<Integer, Double> probabilities = getLevelProbabilities();
        if (probabilities == null || probabilities.isEmpty()) {
            return getMaxLevel() >= 1 ? 1 : 0;
        }

        double random = Math.random();
        double cumulativeProbability = 0.0;

        List<Map.Entry<Integer, Double>> sortedProbabilities = new ArrayList<>(probabilities.entrySet());
        sortedProbabilities.sort(Map.Entry.comparingByKey());

        for (Map.Entry<Integer, Double> entry : sortedProbabilities) {
            cumulativeProbability += entry.getValue();
            if (random <= cumulativeProbability) {
                int rolledLevel = entry.getKey();
                return Math.min(rolledLevel, getMaxLevel());
            }
        }

        return !sortedProbabilities.isEmpty() ? Math.min(sortedProbabilities.get(sortedProbabilities.size() - 1).getKey(), getMaxLevel()) : (getMaxLevel() > 0 ? 1 : 0);
    }
}