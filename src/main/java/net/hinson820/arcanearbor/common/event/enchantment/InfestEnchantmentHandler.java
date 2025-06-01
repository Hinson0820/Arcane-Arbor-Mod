package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.InfestEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class InfestEnchantmentHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        HitResult hitResult = event.getRayTraceResult();
        Projectile projectileEntity = event.getProjectile();

        if (hitResult.getType() != HitResult.Type.ENTITY) {
            return;
        }

        EntityHitResult entityHitResult = (EntityHitResult) hitResult;
        Entity hitEntity = entityHitResult.getEntity();

        if (!(hitEntity instanceof LivingEntity initialTargetEntity)) { // Renamed for clarity and to make it new
            return;
        }
        if (!(projectileEntity instanceof AbstractArrow arrowEntity)) {
            return;
        }
        if (!(arrowEntity.getOwner() instanceof Player shooterEntity)) { // Renamed for clarity
            return;
        }

        ItemStack pickupItemStackOrigin = arrowEntity.getPickupItemStackOrigin();
        PotionContents potionContents = pickupItemStackOrigin.get(DataComponents.POTION_CONTENTS);

        if (potionContents == null || potionContents.equals(PotionContents.EMPTY)) {
            // ArcaneArbor.LOGGER.debug("Infest: Arrow from {} had no PotionContents to spread.", shooterEntity.getName().getString());
            return;
        }

        final List<MobEffectInstance> finalEffectsToSpread = new ArrayList<>(); // Make it final
        Optional<Holder<Potion>> potionHolder = potionContents.potion();
        if (potionHolder.isPresent()) {
            for (MobEffectInstance effectInstance : potionHolder.get().value().getEffects()) {
                finalEffectsToSpread.add(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon()));
            }
        }
        for (MobEffectInstance customEffect : potionContents.customEffects()) {
            finalEffectsToSpread.add(new MobEffectInstance(customEffect.getEffect(), customEffect.getDuration(), customEffect.getAmplifier(), customEffect.isAmbient(), customEffect.isVisible(), customEffect.showIcon()));
        }

        if (finalEffectsToSpread.isEmpty()) {
            // ArcaneArbor.LOGGER.debug("Infest: Arrow PotionContents for {} had no actual effects listed to spread.", shooterEntity.getName().getString());
            return;
        }

        final ItemStack shooterBowOrCrossbow = shooterEntity.getMainHandItem(); // Make it final
        if (!(shooterBowOrCrossbow.getItem() instanceof net.minecraft.world.item.BowItem || shooterBowOrCrossbow.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
            // If checking offhand, that would also need to be effectively final if used in lambda.
            // For simplicity here, assuming main hand or return.
            // You might need to handle the offhand case more carefully regarding effective finality.
            ItemStack offHandItem = shooterEntity.getOffhandItem();
            if (!(offHandItem.getItem() instanceof net.minecraft.world.item.BowItem || offHandItem.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
                return;
            }
            // If you use offHandItem in the lambda, it needs to be effectively final from here.
            // This example will use shooterBowOrCrossbow which is reassigned if main hand is not the one.
            // Let's fix that:
        }

        ItemStack bowUsed; // This will be the effectively final one
        ItemStack mainHand = shooterEntity.getMainHandItem();
        ItemStack offHand = shooterEntity.getOffhandItem();

        if (mainHand.getItem() instanceof net.minecraft.world.item.BowItem || mainHand.getItem() instanceof net.minecraft.world.item.CrossbowItem) {
            bowUsed = mainHand;
        } else if (offHand.getItem() instanceof net.minecraft.world.item.BowItem || offHand.getItem() instanceof net.minecraft.world.item.CrossbowItem) {
            bowUsed = offHand;
        } else {
            return; // No bow/crossbow
        }
        final ItemStack finalBowUsed = bowUsed; // Now this is effectively final for the lambda

        final int enchantmentLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(finalBowUsed, InfestEnchantment.ID); // Make it final

        if (enchantmentLevel > 0) {
            // These are now effectively final or explicitly final when passed to the lambda:
            // enchantmentLevel, finalBowUsed, shooterEntity, initialTargetEntity, finalEffectsToSpread

            ArcaneEnchantmentRegistry.get(InfestEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof InfestEnchantment infestInstance) {
                    // All variables from outer scope used here are now effectively final:
                    // enchantmentLevel, finalBowUsed, shooterEntity, initialTargetEntity, finalEffectsToSpread
                    int activationCost = infestInstance.getActivationManaCost(enchantmentLevel, finalBowUsed, shooterEntity);
                    if (ManaManager.consumeMana(shooterEntity, activationCost)) {
                        int radius = infestInstance.getSpreadRadius(enchantmentLevel);
                        int maxTargets = infestInstance.getMaxSpreadTargets(enchantmentLevel);

                        AABB searchBox = new AABB(initialTargetEntity.blockPosition()).inflate(radius);
                        // Pass effectively final shooterEntity and initialTargetEntity to the lambda predicate
                        List<LivingEntity> nearbyEntities = shooterEntity.level().getEntitiesOfClass(LivingEntity.class, searchBox, entity ->
                                entity.isAlive() && entity != shooterEntity && entity != initialTargetEntity);

                        List<LivingEntity> targetsToAffect = new ArrayList<>();
                        if (nearbyEntities.isEmpty()) {
                            ArcaneArbor.LOGGER.debug("Infest Lvl {}: No valid nearby entities to spread effects from {}'s arrow to.",
                                    enchantmentLevel, shooterEntity.getName().getString());
                            ManaManager.addMana(shooterEntity, activationCost); // Refund
                            return; // Return from the lambda
                        }

                        if (enchantmentLevel == 3 || nearbyEntities.size() <= maxTargets) {
                            targetsToAffect.addAll(nearbyEntities);
                        } else {
                            Collections.shuffle(nearbyEntities);
                            targetsToAffect.addAll(nearbyEntities.subList(0, Math.min(maxTargets, nearbyEntities.size())));
                        }

                        if (targetsToAffect.isEmpty()) {
                            ManaManager.addMana(shooterEntity, activationCost); // Refund
                            ArcaneArbor.LOGGER.debug("Infest Lvl {}: Refunding mana as no targets were selected for {}'s arrow.",
                                    enchantmentLevel, shooterEntity.getName().getString());
                            return; // Return from the lambda
                        }

                        int spreadCount = 0;
                        for (LivingEntity target : targetsToAffect) {
                            for (MobEffectInstance effectInstance : finalEffectsToSpread) {
                                target.addEffect(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon()), shooterEntity);
                            }
                            spreadCount++;
                        }
                        ArcaneArbor.LOGGER.debug("Infest Lvl {}: {}'s arrow hit {}, spread effects to {} entities. Mana Cost: {}",
                                enchantmentLevel, shooterEntity.getName().getString(), initialTargetEntity.getName().getString(), spreadCount, activationCost);

                    } else {
                        ArcaneArbor.LOGGER.debug("Infest Lvl {} for {} failed: not enough mana (need {}, has {})",
                                enchantmentLevel, shooterEntity.getName().getString(), activationCost, ManaManager.getManaData(shooterEntity));
                    }
                }
            });
        }
    }
}