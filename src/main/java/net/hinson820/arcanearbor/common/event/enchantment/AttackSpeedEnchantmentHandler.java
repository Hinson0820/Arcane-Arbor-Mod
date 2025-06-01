package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.AttackSpeedEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class AttackSpeedEnchantmentHandler {

    private static final String RAGEBLADE_STACKS_KEY = ArcaneArbor.MODID + "_RagebladeStacks";
    private static final String RAGEBLADE_TICKS_KEY = ArcaneArbor.MODID + "_RagebladeDecayTicks";

    // Store the ResourceLocation directly for convenience
    private static final ResourceLocation RAGEBLADE_ATTACK_SPEED_MODIFIER_RL =
            ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "rageblade_speed_boost");

    // If you still need the UUID for other purposes, keep it, but the RL is used for the modifier.
    // Alternatively, you can generate the RL from the UUID if you prefer, e.g.,
    // ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, RAGEBLADE_ATTACK_SPEED_MODIFIER_UUID.toString());
    // But a human-readable path is often better for the RL.

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) { // Player is attacker
        if (!(event.getSource().getEntity() instanceof Player playerAttacker)) return;
        if (event.getNewDamage() <= 0) return; // Only trigger on actual damage dealt

        ItemStack mainHandItem = playerAttacker.getMainHandItem();
        if (mainHandItem.isEmpty()) return;

        int asLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(mainHandItem, AttackSpeedEnchantment.ID);
        if (asLevel > 0) {
            ArcaneEnchantmentRegistry.get(AttackSpeedEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof AttackSpeedEnchantment asInstance) {
                    int activationCost = asInstance.getActivationManaCost(asLevel, mainHandItem, playerAttacker);
                    if (ManaManager.consumeMana(playerAttacker, activationCost)) {
                        CompoundTag data = playerAttacker.getPersistentData();
                        int currentStacks = data.getInt(RAGEBLADE_STACKS_KEY);
                        int maxStacks = asInstance.getMaxStacks(asLevel);

                        if (currentStacks < maxStacks) {
                            currentStacks++;
                        }
                        data.putInt(RAGEBLADE_STACKS_KEY, currentStacks);
                        data.putInt(RAGEBLADE_TICKS_KEY, asInstance.getStackDecayTicks(asLevel)); // Reset decay timer

                        applyOrUpdateAttackSpeedModifier(playerAttacker, asInstance, asLevel, currentStacks);

                        ArcaneArbor.LOGGER.debug("Rageblade Lvl {}: {} gained/refreshed stack. Stacks: {}/{}, Mana Cost: {}",
                                asLevel, playerAttacker.getName().getString(), currentStacks, maxStacks, activationCost);
                    } else {
                        ArcaneArbor.LOGGER.debug("Rageblade Lvl {} for {} failed to add stack: not enough mana (need {}, has {})",
                                asLevel, playerAttacker.getName().getString(), activationCost, ManaManager.getManaData(playerAttacker));
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !player.getPersistentData().contains(RAGEBLADE_STACKS_KEY)) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        int ticksRemaining = data.getInt(RAGEBLADE_TICKS_KEY);

        if (ticksRemaining > 0) {
            ticksRemaining--;
            data.putInt(RAGEBLADE_TICKS_KEY, ticksRemaining);
        } else {
            // Stacks expired
            data.remove(RAGEBLADE_STACKS_KEY);
            data.remove(RAGEBLADE_TICKS_KEY);
            removeAttackSpeedModifier(player);
            ArcaneArbor.LOGGER.debug("Rageblade stacks for {} expired.", player.getName().getString());
        }
    }

    private static void applyOrUpdateAttackSpeedModifier(Player player, AttackSpeedEnchantment enchantment, int level, int stacks) {
        AttributeInstance attributeInstance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance == null) return;

        // Remove existing modifier first to re-calculate
        // The removeModifier method in AttributeInstance can take a ResourceLocation or a UUID.
        // Since we define our modifier with a ResourceLocation, we should use that for removal.
        attributeInstance.removeModifier(RAGEBLADE_ATTACK_SPEED_MODIFIER_RL);

        if (stacks == 0) return; // No stacks, no modifier needed

        float totalCooldownReduction = enchantment.getCooldownReductionPerStack(level) * stacks;
        totalCooldownReduction = Math.min(totalCooldownReduction, 0.90f); // Cap reduction

        double modifierValue = (1.0 / (1.0 - totalCooldownReduction)) - 1.0;

        AttributeModifier modifier = new AttributeModifier(
                RAGEBLADE_ATTACK_SPEED_MODIFIER_RL, // Use the ResourceLocation ID
                modifierValue,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL // Use your enum's value
        );
        // Use addPermanentModifier. "Permanent" here means the modifier persists until explicitly removed.
        // We manage its lifetime by adding it on stack gain and removing it on stack loss/expiration.
        attributeInstance.addPermanentModifier(modifier);
        ArcaneArbor.LOGGER.debug("Applied Rageblade modifier to {}. Stacks: {}, CooldownReduction: {}%, AttribModValue: {}",
                player.getName().getString(), stacks, String.format("%.1f", totalCooldownReduction * 100), String.format("%.3f", modifierValue));
    }

    private static void removeAttackSpeedModifier(Player player) {
        AttributeInstance attributeInstance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null) {
            // Use the ResourceLocation to remove the modifier
            if(attributeInstance.removeModifier(RAGEBLADE_ATTACK_SPEED_MODIFIER_RL)) {
                ArcaneArbor.LOGGER.debug("Removed Rageblade modifier for {}.", player.getName().getString());
            }
        }
    }
}