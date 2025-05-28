package net.hinson820.arcanearbor.common.enchantment.types;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IgnorePainEnchantment extends ArcaneEnchantment {
    public static final String ID = "ignore_pain";

    public IgnorePainEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".ignore_pain"),
                stack -> {
                    Item item = stack.getItem();
                    if (item instanceof ArmorItem armorItem) {
                        ArcaneArbor.LOGGER.debug("IgnorePain canApplyTo: Item is ArmorItem. Type: {}, Expected Type: CHESTPLATE, Match: {}",
                                armorItem.getType(), ArmorItem.Type.CHESTPLATE, (armorItem.getType() == ArmorItem.Type.CHESTPLATE));
                        return armorItem.getType() == ArmorItem.Type.CHESTPLATE;
                    }
                    ArcaneArbor.LOGGER.debug("IgnorePain canApplyTo: Item is NOT ArmorItem. It is: {}", item.getClass().getName());
                    return false;
                }
        );
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public Map<Integer, Double> getLevelProbabilities() {
        return Map.of(
                1, 0.50,
                2, 0.40,
                3, 0.10
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
        float healthRatio = enchantingPlayer.getHealth() / enchantingPlayer.getMaxHealth();
        return healthRatio < 0.5f;
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        float absorptionPercent = getDamageAbsorptionPercent(level) * 100f;
        int bleedSeconds = getBleedDurationSeconds(level);

        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".ignore_pain.tooltip.absorption",
                        String.format("%.0f%%", absorptionPercent))
                .withStyle(ChatFormatting.BLUE));
        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".ignore_pain.tooltip.bleed",
                        bleedSeconds)
                .withStyle(ChatFormatting.RED));

        if (grantsStrengthOnBleed(level)) {
            tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".ignore_pain.tooltip.strength")
                    .withStyle(ChatFormatting.GOLD));
        }
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.IGNORE_PAIN_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        return switch (level) {
            case 1 -> Configs.COMMON.IGNORE_PAIN_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.IGNORE_PAIN_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.IGNORE_PAIN_L3_ACTIVATION_MANA.get();
            default -> 9999;
        };
    }


    public float getDamageAbsorptionPercent(int level) {
        return switch (level) {
            case 1 -> Configs.COMMON.IGNORE_PAIN_L1_ABSORPTION.get().floatValue();
            case 2 -> Configs.COMMON.IGNORE_PAIN_L2_ABSORPTION.get().floatValue();
            case 3 -> Configs.COMMON.IGNORE_PAIN_L3_ABSORPTION.get().floatValue();
            default -> 0.0f;
        };
    }

    public int getBleedDurationSeconds(int level) {
        return Configs.COMMON.IGNORE_PAIN_BLEED_SECONDS.get();
    }

    public boolean grantsStrengthOnBleed(int level) {
        return level >= 3;
    }
}
