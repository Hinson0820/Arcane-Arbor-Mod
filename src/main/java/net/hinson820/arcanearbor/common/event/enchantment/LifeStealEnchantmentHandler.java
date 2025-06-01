package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.LifeStealEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class LifeStealEnchantmentHandler {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player playerAttacker) {
            ItemStack mainHandItem = playerAttacker.getMainHandItem();
            if (mainHandItem.isEmpty()) return;

            int lifeStealLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(mainHandItem, LifeStealEnchantment.ID);
            if (lifeStealLevel > 0) {
                ArcaneEnchantmentRegistry.get(LifeStealEnchantment.ID).ifPresent(arcaneEnch -> {
                    if (arcaneEnch instanceof LifeStealEnchantment lifeStealInstance) {
                        float actualDamageTakenByTarget = event.getNewDamage();

                        if (actualDamageTakenByTarget > 0) {
                            int activationCost = lifeStealInstance.getActivationManaCost(lifeStealLevel, mainHandItem, playerAttacker);
                            if (ManaManager.consumeMana(playerAttacker, activationCost)) {
                                float lifestealPercent = lifeStealInstance.getLifestealPercent(lifeStealLevel);
                                float healAmount = actualDamageTakenByTarget * lifestealPercent;
                                playerAttacker.heal(healAmount);

                                if (lifeStealInstance.grantsRegeneration(lifeStealLevel)) {
                                    playerAttacker.addEffect(new MobEffectInstance(
                                            MobEffects.REGENERATION,
                                            lifeStealInstance.getRegenerationDurationTicks(lifeStealLevel),
                                            lifeStealInstance.getRegenerationAmplifier(lifeStealLevel),
                                            false, true, true
                                    ));
                                }
                                ArcaneArbor.LOGGER.debug("Lifesteal Lvl {}: Attacker {}, Target {}, Damage Dealt: {}, Healed: {}, Regen: {}, Mana Cost: {}",
                                        lifeStealLevel, playerAttacker.getName().getString(), event.getEntity().getName().getString(),
                                        actualDamageTakenByTarget, healAmount,
                                        (lifeStealInstance.grantsRegeneration(lifeStealLevel) ? "Yes" : "No"),
                                        activationCost
                                );
                            } else {
                                ArcaneArbor.LOGGER.debug("Lifesteal Lvl {} for {} failed: not enough mana (need {}, has {})",
                                        lifeStealLevel, playerAttacker.getName().getString(), activationCost, ManaManager.getManaData(playerAttacker));
                            }
                        }
                    } else {
                        ArcaneArbor.LOGGER.warn("Retrieved ArcaneEnchantment for ID '{}' but it was not an instance of LifeStealEnchantment. Type: {}",
                                LifeStealEnchantment.ID, arcaneEnch.getClass().getName());
                    }
                });
            }
        }
    }
}