package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.FullCriticalEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class FullCriticalEnchantmentHandler {

    @SubscribeEvent
    public static void onCriticalHitCheck(CriticalHitEvent event) {
        Player playerAttacker = event.getEntity();

        if (event.isCriticalHit()) {
            return;
        }

        ItemStack mainHandItem = playerAttacker.getMainHandItem();
        if (mainHandItem.isEmpty()) return;

        int critLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(mainHandItem, FullCriticalEnchantment.ID);
        if (critLevel > 0) { // Since max level is 1, this just checks for its presence
            ArcaneEnchantmentRegistry.get(FullCriticalEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof FullCriticalEnchantment critInstance) {
                    int activationCost = critInstance.getActivationManaCost(critLevel, mainHandItem, playerAttacker);
                    if (ManaManager.consumeMana(playerAttacker, activationCost)) {
                        event.setCriticalHit(true); // Force the critical hit

                        // You can set a custom damage multiplier if your enchantment should behave differently
                        // than vanilla crits, or if you want to ensure a specific multiplier.
                        // Vanilla default is usually 1.5f.
                        // event.setDamageMultiplier(1.5f); // Or critInstance.getCriticalMultiplier(critLevel);

                        ArcaneArbor.LOGGER.debug("Full Critical: {} landed a guaranteed critical hit via enchantment. Mana Cost: {}",
                                playerAttacker.getName().getString(), activationCost);
                    } else {
                        // Not enough mana, so it's not a critical hit from this enchant.
                        // If it wasn't a crit before, and we don't have mana, it remains non-critical.
                        // event.setCriticalHit(false); // This would ensure it's NOT a crit if we lacked mana.
                        ArcaneArbor.LOGGER.debug("Full Critical for {} failed to force critical: not enough mana (need {}, has {})",
                                playerAttacker.getName().getString(), activationCost, ManaManager.getManaData(playerAttacker));
                    }
                }
            });
        }
    }
}
