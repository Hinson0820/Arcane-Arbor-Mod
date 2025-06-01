package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.ExecuteEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class ExecuteEnchantmentHandler {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player playerAttacker)) return;

        LivingEntity target = event.getEntity();
        // If the target is already dead from the initial hit, no need to execute
        if (!target.isAlive()) return;

        ItemStack mainHandItem = playerAttacker.getMainHandItem();
        if (mainHandItem.isEmpty()) return;

        int executeLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(mainHandItem, ExecuteEnchantment.ID);
        if (executeLevel > 0) {
            ArcaneEnchantmentRegistry.get(ExecuteEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof ExecuteEnchantment executeInstance) {
                    float currentHealth = target.getHealth(); // Health after the initial damage of this hit
                    float absoluteThreshold = executeInstance.getExecuteThresholdAbsoluteHealth(executeLevel);
                    float percentThresholdValue = target.getMaxHealth() * executeInstance.getExecuteThresholdPercentMaxHealth(executeLevel);

                    // Check if target's current health is below either threshold
                    if (currentHealth > 0 && (currentHealth < absoluteThreshold || currentHealth < percentThresholdValue)) {
                        int activationCost = executeInstance.getActivationManaCost(executeLevel, mainHandItem, playerAttacker);
                        if (ManaManager.consumeMana(playerAttacker, activationCost)) {
                            target.kill(); // The actual execution
                            ArcaneArbor.LOGGER.debug("Execute Lvl {}: Attacker {}, Target {} executed. Initial Dmg: {}. Mana Cost: {}",
                                    executeLevel, playerAttacker.getName().getString(), target.getName().getString(),
                                    event.getNewDamage(), activationCost);
                        } else {
                            ArcaneArbor.LOGGER.debug("Execute Lvl {} for {} (target {}) failed execution (not enough mana): {} needed, has {}. Initial Dmg: {}",
                                    executeLevel, playerAttacker.getName().getString(), target.getName().getString(),
                                    activationCost, ManaManager.getManaData(playerAttacker), event.getNewDamage());
                        }
                    }
                }
            });
        }
    }
}
