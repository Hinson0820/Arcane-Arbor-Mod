package net.hinson820.arcanearbor.common.enchantment.types;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.config.Configs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LifeStealEnchantment extends ArcaneEnchantment {
    public static final String ID = "lifesteal";

    public LifeStealEnchantment() {
        super(ID, Component.translatable("enchantment." + ArcaneArbor.MODID + ".lifesteal"),
                stack -> stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem
        );
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public Map<Integer, Double> getLevelProbabilities() {
        return Map.of(
                1, 0.30,
                2, 0.30,
                3, 0.25,
                4, 0.10,
                5, 0.05
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
        long witherSkeletonsNearby = world.getEntitiesOfClass(LivingEntity.class,
                new AABB(enchantingPlayer.blockPosition()).inflate(10),
                entity -> entity.getType() == EntityType.WITHER_SKELETON && entity.isAlive()).size();
        return witherSkeletonsNearby >= 3 && enchantingPlayer.hasEffect(MobEffects.WITHER);
    }

    @Override
    public List<Component> getTooltip(ItemStack stack, int level) {
        List<Component> tooltipLines = new ArrayList<>();
        float absorptionPercent = getLifestealPercent(level) * 100f;

        tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".lifesteal.tooltip.absorb",
                        String.format("%.0f%%", absorptionPercent))
                .withStyle(ChatFormatting.GREEN));

        if (grantsRegeneration(level)) {
            tooltipLines.add(Component.translatable("enchantment." + ArcaneArbor.MODID + ".lifesteal.tooltip.regen")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        return tooltipLines;
    }

    @Override
    public int getManaCost() {
        return Configs.COMMON.LIFESTEAL_APPLICATION_MANA.get();
    }

    @Override
    public int getActivationManaCost(int level, ItemStack stack, LivingEntity user) {
        return switch (level) {
            case 1 -> Configs.COMMON.LIFESTEAL_L1_ACTIVATION_MANA.get();
            case 2 -> Configs.COMMON.LIFESTEAL_L2_ACTIVATION_MANA.get();
            case 3 -> Configs.COMMON.LIFESTEAL_L3_ACTIVATION_MANA.get();
            case 4 -> Configs.COMMON.LIFESTEAL_L4_ACTIVATION_MANA.get();
            case 5 -> Configs.COMMON.LIFESTEAL_L5_ACTIVATION_MANA.get();
            default -> 9999;
        };
    }

    // --- Level-dependent effect scaling ---
    public float getLifestealPercent(int level) {
        return switch (level) {
            case 1 -> Configs.COMMON.LIFESTEAL_L1_PERCENT.get().floatValue();
            case 2 -> Configs.COMMON.LIFESTEAL_L2_PERCENT.get().floatValue();
            case 3 -> Configs.COMMON.LIFESTEAL_L3_PERCENT.get().floatValue();
            case 4 -> Configs.COMMON.LIFESTEAL_L4_PERCENT.get().floatValue();
            case 5 -> Configs.COMMON.LIFESTEAL_L5_PERCENT.get().floatValue();
            default -> 0.0f;
        };
    }

    public boolean grantsRegeneration(int level) {
        return level >= 4;
    }

    public int getRegenerationDurationTicks(int level) {
        return (level >= 4) ? 20 : 0;
    }

    public int getRegenerationAmplifier(int level) {
        return (level >= 4) ? 0 : -1;
    }
}
