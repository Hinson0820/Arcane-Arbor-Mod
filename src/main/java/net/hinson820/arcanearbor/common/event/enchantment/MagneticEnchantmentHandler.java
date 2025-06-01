package net.hinson820.arcanearbor.common.event.enchantment;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.enchantment.types.MagneticEnchantment;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class MagneticEnchantmentHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        HitResult hitResult = event.getRayTraceResult();
        Projectile projectileEntity = event.getProjectile();

        if (hitResult.getType() != HitResult.Type.ENTITY) {
            return;
        }

        EntityHitResult entityHitResult = (EntityHitResult) hitResult;
        Entity hitEntity = entityHitResult.getEntity();

        if (!(hitEntity instanceof LivingEntity initialTarget)) {
            return;
        }
        if (!(projectileEntity instanceof AbstractArrow arrowEntity)) {
            return;
        }
        if (!(arrowEntity.getOwner() instanceof Player playerShooter)) {
            return;
        }

        // Ensure we are on the server side for teleportation
        if (!(playerShooter.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack bowOrCrossbow = playerShooter.getMainHandItem();
        if (!(bowOrCrossbow.getItem() instanceof net.minecraft.world.item.BowItem || bowOrCrossbow.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
            bowOrCrossbow = playerShooter.getOffhandItem();
            if (!(bowOrCrossbow.getItem() instanceof net.minecraft.world.item.BowItem || bowOrCrossbow.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
                return;
            }
        }

        final int magneticLevel = ArcaneEnchantmentManager.getArcaneEnchantmentLevel(bowOrCrossbow, MagneticEnchantment.ID);
        final ItemStack finalBowOrCrossbow = bowOrCrossbow;
        final Player finalPlayerShooter = playerShooter;
        final LivingEntity finalInitialTarget = initialTarget;
        final ServerLevel finalServerLevel = serverLevel; // Final for lambda

        if (magneticLevel > 0) {
            ArcaneEnchantmentRegistry.get(MagneticEnchantment.ID).ifPresent(arcaneEnch -> {
                if (arcaneEnch instanceof MagneticEnchantment magneticInstance) {
                    int activationCost = magneticInstance.getActivationManaCost(magneticLevel, finalBowOrCrossbow, finalPlayerShooter);
                    if (ManaManager.consumeMana(finalPlayerShooter, activationCost)) {
                        List<LivingEntity> entitiesToTeleport = new ArrayList<>();
                        entitiesToTeleport.add(finalInitialTarget);

                        int pullRadius = magneticInstance.getPullRadius(magneticLevel);
                        if (pullRadius > 0) {
                            AABB searchBox = new AABB(finalInitialTarget.blockPosition()).inflate(pullRadius);
                            List<LivingEntity> nearbyEntities = finalPlayerShooter.level().getEntitiesOfClass(LivingEntity.class, searchBox, entity ->
                                    entity.isAlive() && entity != finalPlayerShooter && entity != finalInitialTarget && !entitiesToTeleport.contains(entity));
                            entitiesToTeleport.addAll(nearbyEntities);
                        }

                        Vec3 lookVec = finalPlayerShooter.getLookAngle();
                        Vec3 playerEyePos = finalPlayerShooter.getEyePosition();
                        // Calculate the destination point
                        Vec3 teleportDestinationBase = playerEyePos.add(lookVec.scale(magneticInstance.getPullDestinationOffset()));

                        int teleportedCount = 0;
                        for (LivingEntity entityToTeleport : entitiesToTeleport) {
                            if (!entityToTeleport.isAlive()) continue;

                            // Find a safe teleport location near the base destination
                            double destX = teleportDestinationBase.x();
                            double destY = teleportDestinationBase.y(); // Start at the calculated Y
                            double destZ = teleportDestinationBase.z();

                            // Attempt to find a safe spot, crucial for teleportation
                            // This is a very basic check, might need improvement (e.g., checking for head space)
                            Level level = entityToTeleport.level();
                            if (!level.noCollision(entityToTeleport.getBoundingBox().move(destX - entityToTeleport.getX(), destY - entityToTeleport.getY(), destZ - entityToTeleport.getZ()))) {
                                destY += 1.0;
                                if (!level.noCollision(entityToTeleport.getBoundingBox().move(destX - entityToTeleport.getX(), destY - entityToTeleport.getY(), destZ - entityToTeleport.getZ()))) {
                                    ArcaneArbor.LOGGER.warn("Magnetic Teleport: Could not find safe spot for {} near {}, teleport aborted for this entity.",
                                            entityToTeleport.getName().getString(), teleportDestinationBase);
                                    continue; // Skip teleporting this entity
                                }
                            }


                            // Perform the teleportation (server-side)
                            entityToTeleport.teleportTo(destX, destY, destZ); // Simpler teleport, retains orientation
                            // To also set orientation: entityToTeleport.teleportTo(destX, destY, destZ, entityToTeleport.getYRot(), entityToTeleport.getXRot());

                            level.playSound(null, destX, destY, destZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
                            if (level instanceof ServerLevel sl) {
                               sl.sendParticles(ParticleTypes.PORTAL, destX, destY + entityToTeleport.getBbHeight() / 2.0, destZ, 20, 0.5, 0.5, 0.5, 0.1);
                            }

                            teleportedCount++;
                        }

                        if (teleportedCount > 0) {
                            ArcaneArbor.LOGGER.debug("Magnetic Lvl {}: {}'s arrow hit {}, teleported {} entities to player. Mana Cost: {}",
                                    magneticLevel, finalPlayerShooter.getName().getString(), finalInitialTarget.getName().getString(), teleportedCount, activationCost);
                        } else if (activationCost > 0) {
                            // If mana was spent but no one was teleported (e.g., all failed safe spot check)
                            ManaManager.addMana(finalPlayerShooter, activationCost);
                            ArcaneArbor.LOGGER.debug("Magnetic Lvl {}: No entities teleported for {}'s arrow, mana refunded.",
                                    magneticLevel, finalPlayerShooter.getName().getString());
                        }


                    } else {
                        ArcaneArbor.LOGGER.debug("Magnetic Lvl {} for {} failed: not enough mana (need {}, has {})",
                                magneticLevel, finalPlayerShooter.getName().getString(), activationCost, ManaManager.getManaData(finalPlayerShooter));
                    }
                }
            });
        }
    }
}