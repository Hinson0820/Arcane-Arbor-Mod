package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.IgnorePainEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class IgnorePainEnchantmentHandler {

    // NBT Keys specific to Ignore Pain
    private static final String BLEED_DAMAGE_KEY = ArcaneArbor.MODID + "_BleedDamagePerTick";
    private static final String BLEED_TICKS_KEY = ArcaneArbor.MODID + "_BleedTicksTotal";
    private static final String IGNORE_PAIN_STRENGTH_TICKS_KEY = ArcaneArbor.MODID + "_IgnorePainStrengthTicks";

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        ItemStack chestplate = target.getItemBySlot(EquipmentSlot.CHEST);

        if (chestplate.isEmpty()) return;

        int ignorePainLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(chestplate, IgnorePainEnchantment.ID);
        if (ignorePainLevel > 0) {
            ArcaneEnchantmentRegistry.get(IgnorePainEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof IgnorePainEnchantment ignorePainInstance) {
                    boolean canActivate = true;
                    int activationCost = 0;

                    if (target instanceof Player playerTarget) {
                        activationCost = ignorePainInstance.getActivationManaCost(ignorePainLevel, chestplate, playerTarget);
                        if (!ManaManager.consumeMana(playerTarget, activationCost)) {
                            canActivate = false;
                            ArcaneArbor.LOGGER.debug("Ignore Pain Lvl {} for {} did not activate: not enough mana (need {}, has {})",
                                    ignorePainLevel, playerTarget.getName().getString(), activationCost, ManaManager.getManaData(playerTarget));
                        }
                    }

                    if (canActivate) {
                        float absorptionPercent = ignorePainInstance.getDamageAbsorptionPercent(ignorePainLevel);
                        float damageBeforeIgnorePain = event.getAmount();

                        if (damageBeforeIgnorePain > 0 && absorptionPercent > 0) {
                            float storedDamageForBleed = damageBeforeIgnorePain * absorptionPercent;
                            float reducedAmount = damageBeforeIgnorePain - storedDamageForBleed;

                            event.setAmount(reducedAmount);

                            CompoundTag persistentData = target.getPersistentData();
                            float existingBleedDamagePerTick = persistentData.getFloat(BLEED_DAMAGE_KEY);
                            int bleedTotalTicks = ignorePainInstance.getBleedDurationSeconds(ignorePainLevel) * 20;

                            float newBleedDamagePerTick = (bleedTotalTicks > 0) ? storedDamageForBleed / bleedTotalTicks : 0;
                            persistentData.putFloat(BLEED_DAMAGE_KEY, existingBleedDamagePerTick + newBleedDamagePerTick);

                            int currentBleedTicks = persistentData.getInt(BLEED_TICKS_KEY);
                            persistentData.putInt(BLEED_TICKS_KEY, Math.max(currentBleedTicks, bleedTotalTicks));

                            if (ignorePainInstance.grantsStrengthOnBleed(ignorePainLevel)) {
                                int currentStrengthTicks = persistentData.getInt(IGNORE_PAIN_STRENGTH_TICKS_KEY);
                                persistentData.putInt(IGNORE_PAIN_STRENGTH_TICKS_KEY, Math.max(currentStrengthTicks, bleedTotalTicks));
                            }
                            ArcaneArbor.LOGGER.debug("Ignore Pain Lvl {}: Target: {}, Original Dmg: {}, Reduced To: {}, Stored: {}, Bleed Ticks: {}, Mana Cost: {}",
                                    ignorePainLevel, target.getName().getString(), damageBeforeIgnorePain, reducedAmount, storedDamageForBleed, bleedTotalTicks, (target instanceof Player ? activationCost : "N/A"));
                        } else {
                            if (target instanceof Player playerTarget && activationCost > 0) {
                                ManaManager.addMana(playerTarget, activationCost); // Refund mana if not used
                                ArcaneArbor.LOGGER.debug("Ignore Pain Lvl {} for {} no damage to absorb, mana {} refunded",
                                        ignorePainLevel, playerTarget.getName().getString(), activationCost);
                            }
                        }
                    }
                } else {
                    ArcaneArbor.LOGGER.warn("Retrieved ArcaneEnchantment for ID '{}' but it was not an instance of IgnorePainEnchantment. Type: {}",
                            IgnorePainEnchantment.ID, arcaneEnch.getClass().getName());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide() || !entity.isAlive()) return;

        CompoundTag persistentData = entity.getPersistentData();
        boolean isBleeding = false;

        // Only handle Ignore Pain related NBT data
        if (persistentData.contains(BLEED_TICKS_KEY)) {
            int ticksRemaining = persistentData.getInt(BLEED_TICKS_KEY);
            if (ticksRemaining > 0) {
                isBleeding = true;
                float damageThisTick = persistentData.getFloat(BLEED_DAMAGE_KEY);
                if (damageThisTick > 0 && entity.isAlive()) {
                    entity.hurt(entity.damageSources().magic(), damageThisTick); // Consider a custom damage source for bleed
                }
                ticksRemaining--;
                persistentData.putInt(BLEED_TICKS_KEY, ticksRemaining);
                if (ticksRemaining <= 0) {
                    persistentData.remove(BLEED_DAMAGE_KEY);
                    persistentData.remove(BLEED_TICKS_KEY);
                    // Also remove strength ticks if bleed ends
                    if (persistentData.contains(IGNORE_PAIN_STRENGTH_TICKS_KEY)) {
                        persistentData.remove(IGNORE_PAIN_STRENGTH_TICKS_KEY);
                    }
                }
            } else {
                persistentData.remove(BLEED_DAMAGE_KEY);
                persistentData.remove(BLEED_TICKS_KEY);
                if (persistentData.contains(IGNORE_PAIN_STRENGTH_TICKS_KEY)) {
                    persistentData.remove(IGNORE_PAIN_STRENGTH_TICKS_KEY);
                }
            }
        }

        if (persistentData.contains(IGNORE_PAIN_STRENGTH_TICKS_KEY)) {
            int strengthTicksRemaining = persistentData.getInt(IGNORE_PAIN_STRENGTH_TICKS_KEY);
            if (strengthTicksRemaining > 0 && isBleeding) { // Ensure still bleeding to grant strength
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25, 0, false, false, true)); // Strength I
                strengthTicksRemaining--;
                persistentData.putInt(IGNORE_PAIN_STRENGTH_TICKS_KEY, strengthTicksRemaining);
                if (strengthTicksRemaining <= 0) {
                    persistentData.remove(IGNORE_PAIN_STRENGTH_TICKS_KEY);
                }
            } else if (strengthTicksRemaining <= 0 || !isBleeding) { // Remove if not bleeding or ticks ran out
                persistentData.remove(IGNORE_PAIN_STRENGTH_TICKS_KEY);
            }
        }
    }
}
