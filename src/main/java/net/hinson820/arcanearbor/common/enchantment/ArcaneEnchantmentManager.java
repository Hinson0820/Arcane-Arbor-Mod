package net.hinson820.arcanearbor.common.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.hinson820.arcanearbor.core.init.DataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class ArcaneEnchantmentManager {

    private static DataComponentType<Map<String, Integer>> getComponentType() {
        return DataComponents.ARCANE_ENCHANTMENTS.get();
    }

    public static void setArcaneEnchantment(ItemStack stack, String enchantmentId, int level) {
        if (stack.isEmpty() || enchantmentId == null || enchantmentId.isBlank()) return;

        if (level <= 0) {
            stack.remove(getComponentType());
        } else {
            Map<String, Integer> enchantmentMap = new HashMap<>();
            enchantmentMap.put(enchantmentId, level);
            stack.set(getComponentType(), Collections.unmodifiableMap(enchantmentMap));
        }
    }

    public static Optional<Map.Entry<String, Integer>> getActiveArcaneEnchantment(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        Map<String, Integer> enchantments = stack.get(getComponentType());
        if (enchantments != null && !enchantments.isEmpty()) {
            return enchantments.entrySet().stream().findFirst();
        }
        return Optional.empty();
    }

    public static boolean hasAnyArcaneEnchantment(ItemStack stack) {
        return getActiveArcaneEnchantment(stack).isPresent();
    }

    public static int getArcaneEnchantmentLevel(ItemStack stack, String enchantmentId) {
        return getActiveArcaneEnchantment(stack)
                .filter(entry -> entry.getKey().equals(enchantmentId))
                .map(Map.Entry::getValue)
                .orElse(0);
    }

    public static void clearArcaneEnchantment(ItemStack stack) {
        if (stack.isEmpty()) return;
        stack.remove(getComponentType());
    }

    public static ArcaneEnchantApplyOutcome tryApplyEnchantment(ItemStack stack, ArcaneEnchantment enchantment, Level world, LivingEntity enchantingLivingEntity) {
        if (!(enchantingLivingEntity instanceof Player enchantingPlayer)) {
            ArcaneArbor.LOGGER.warn("Arcane enchantment attempt by non-player entity: {}", enchantingLivingEntity.getName().getString());
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.INTERNAL_ERROR);
        }

        if (stack.isEmpty() || enchantment == null) {
            ArcaneArbor.LOGGER.warn("tryApplyEnchantment called with empty stack or null enchantment.");
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.CANNOT_APPLY_TO_ITEM);
        }
        if (!enchantment.canApplyTo(stack)) {
            ArcaneArbor.LOGGER.debug("Enchantment {} cannot apply to item {}.", enchantment.getId(), stack.getHoverName().getString());
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.CANNOT_APPLY_TO_ITEM);
        }

        if (hasAnyArcaneEnchantment(stack)) {
            ArcaneArbor.LOGGER.debug("Item {} already has an arcane enchantment. Cannot apply {}.", stack.getHoverName().getString(), enchantment.getId());
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.ALREADY_HAS_ARCANE_ENCHANTMENT);
        }

        if (!enchantment.checkEnchantmentPrerequisites(world, enchantingPlayer)) {
            ArcaneArbor.LOGGER.debug("Prerequisites not met for enchantment {} on {}. Item is safe.",
                    enchantment.getId(), stack.getHoverName().getString());
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.PREREQUISITES_NOT_MET);
        }

        int fixedManaCost = enchantment.getManaCost();
        if (!enchantingPlayer.isCreative() && ManaManager.getManaData(enchantingPlayer).getMana() < fixedManaCost) {
            ArcaneArbor.LOGGER.debug("Player {} does not have enough mana (needs {}, has {}) for enchantment {}.",
                    enchantingPlayer.getName().getString(), fixedManaCost, ManaManager.getManaData(enchantingPlayer).getMana(), enchantment.getId());
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.NOT_ENOUGH_MANA); // New result type needed
        }

        double successChance = enchantment.getOverallSuccessChance(stack, world, enchantingPlayer);
        if (Math.random() >= successChance) {
            ArcaneArbor.LOGGER.info("Enchantment {} initial success roll failed for {}.",
                    enchantment.getId(), stack.getHoverName().getString());

            double breakChance = enchantment.getBreakChanceOnFailure(stack, world, enchantingPlayer);
            if (Math.random() < breakChance) {
                stack.setCount(0);
                ArcaneArbor.LOGGER.info(" -> Item {} broke due to enchantment failure.", stack.getHoverName().getString());
                return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.FAILURE_ITEM_BROKEN);
            } else {
                ArcaneArbor.LOGGER.info(" -> Item {} is safe despite enchantment failure.", stack.getHoverName().getString());
                return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.FAILURE_ITEM_SAFE);
            }
        }

        ArcaneArbor.LOGGER.debug("Enchantment {} passed initial success roll and prerequisites for {}. Rolling level...",
                enchantment.getId(), stack.getHoverName().getString());
        int targetLevel = enchantment.rollLevel();

        if (targetLevel == 0 && enchantment.getMaxLevel() > 0) {
            ArcaneArbor.LOGGER.debug("Rolled invalid level 0 for levelable enchantment {} on {}. Considered failure.",
                    enchantment.getId(), stack.getHoverName().getString());
            return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.NO_VALID_LEVEL_ROLL);
        }
        if (targetLevel > enchantment.getMaxLevel() && enchantment.getMaxLevel() > 0) {
            ArcaneArbor.LOGGER.warn("Rolled level {} for {} which is > max {}. Capping at max.",
                    targetLevel, enchantment.getId(), enchantment.getMaxLevel());
            targetLevel = enchantment.getMaxLevel();
        }
        if (enchantment.getMaxLevel() <= 1 && targetLevel == 0) {
            targetLevel = 1;
        }

        setArcaneEnchantment(stack, enchantment.getId(), targetLevel);
        enchantment.onEnchanted(stack, targetLevel);
        ArcaneArbor.LOGGER.info("Successfully applied {} Lvl {} to {}",
                enchantment.getId(), targetLevel, stack.getHoverName().getString());
        return new ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType.SUCCESS, targetLevel);
    }
}
