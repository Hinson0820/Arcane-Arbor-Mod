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

public class FullCriticalEnchantment extends ArcaneEnchantment {
    public static final String ID = "full_critical";

    public FullCriticalEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".full_critical"),
                stack -> stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem);
    }

    @Override
    public int getMaxLevel() {
        return 1; // Only one level
    }

    @Override
    public Map<Integer, Double> getLevelProbabilities() {
        return Map.of(1, 1.0); // 100% chance for level 1 if this enchantment is chosen
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
        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".full_critical.tooltip.description")
                .withStyle(ChatFormatting.GOLD));
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.FULLCRITICAL_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        // Mana cost per critical hit guaranteed
        return Configs.COMMON.FULLCRITICAL_L1_ACTIVATION_MANA.get();
    }
}
