package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.ChillEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class ChillEnchantmentHandler {

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player playerAttacker)) return;
        if (event.getNewDamage() <= 0) return; // Only on successful damage

        LivingEntity target = event.getEntity();
        if (!target.isAlive()) return;

        ItemStack mainHandItem = playerAttacker.getMainHandItem();
        if (mainHandItem.isEmpty()) return;

        int chillLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(mainHandItem, ChillEnchantment.ID);
        if (chillLevel > 0) {
            ArcaneEnchantmentRegistry.get(ChillEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof ChillEnchantment chillInstance) {
                    int activationCost = chillInstance.getActivationManaCost(chillLevel, mainHandItem, playerAttacker);
                    if (ManaManager.consumeMana(playerAttacker, activationCost)) {
                        int durationTicks = chillInstance.getSlownessDurationTicks(chillLevel);
                        int amplifier = chillInstance.getSlownessAmplifier(chillLevel);

                        // Apply Slowness
                        // The last boolean 'true' makes the effect icon visible. 'false' for ambient, 'true' for particles
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, amplifier, false, true, true));

                        ArcaneArbor.LOGGER.debug("Chill Lvl {}: Attacker {}, Target {} applied Slowness {} for {} ticks. Mana Cost: {}",
                                chillLevel, playerAttacker.getName().getString(), target.getName().getString(),
                                amplifier + 1, durationTicks, activationCost);
                    } else {
                        ArcaneArbor.LOGGER.debug("Chill Lvl {} for {} (target {}) failed: not enough mana (need {}, has {})",
                                chillLevel, playerAttacker.getName().getString(), target.getName().getString(),
                                activationCost, ManaManager.getManaData(playerAttacker));
                    }
                }
            });
        }
    }
}
