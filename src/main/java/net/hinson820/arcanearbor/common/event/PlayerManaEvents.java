package net.hinson820.arcanearbor.common.event;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.mana.PlayerMana;
import net.hinson820.arcanearbor.common.network.ManaSyncPacket;
import net.hinson820.arcanearbor.config.Configs;
import net.hinson820.arcanearbor.core.init.ManaAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class PlayerManaEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerMana mana = serverPlayer.getData(ManaAttachment.PLAYER_MANA.get());
            PacketDistributor.sendToPlayer(serverPlayer, new ManaSyncPacket(mana.getMana(), mana.getMaxMana()));
            ArcaneArbor.LOGGER.debug("Synced mana for {} on login: {}/{}", serverPlayer.getName().getString(), mana.getMana(), mana.getMaxMana());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerMana mana = serverPlayer.getData(ManaAttachment.PLAYER_MANA.get());
            PacketDistributor.sendToPlayer(serverPlayer, new ManaSyncPacket(mana.getMana(), mana.getMaxMana()));
            ArcaneArbor.LOGGER.debug("Synced mana for {} on respawn: {}/{}", serverPlayer.getName().getString(), mana.getMana(), mana.getMaxMana());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerMana mana = serverPlayer.getData(ManaAttachment.PLAYER_MANA.get());
            PacketDistributor.sendToPlayer(serverPlayer, new ManaSyncPacket(mana.getMana(), mana.getMaxMana()));
            ArcaneArbor.LOGGER.debug("Synced mana for {} on dimension change: {}/{}", serverPlayer.getName().getString(), mana.getMana(), mana.getMaxMana());
        }
    }

    private static final int REGEN_TICK_RATE = 20;
    private static final int REGEN_AMOUNT = 1;


    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.isSpectator()) {
                return;
            }

            int regenAmount = Configs.COMMON.PLAYER_MANA_REGEN_AMOUNT.get();
            if (regenAmount <= 0) {
                return;
            }

            int regenTickRate = Configs.COMMON.PLAYER_MANA_REGEN_TICK_RATE.get();
            if (regenTickRate <= 0) {
                ArcaneArbor.LOGGER.warn("Player manaRegenTickRate is configured to {} which is invalid. Regeneration disabled.", regenTickRate);
                return;
            }

            if (serverPlayer.tickCount % REGEN_TICK_RATE == 0) {
                PlayerMana mana = serverPlayer.getData(ManaAttachment.PLAYER_MANA.get());
                if (mana.getMana() < mana.getMaxMana()) {
                    mana.addMana(REGEN_AMOUNT);
                    PacketDistributor.sendToPlayer(serverPlayer, new ManaSyncPacket(mana.getMana(), mana.getMaxMana()));
                    ArcaneArbor.LOGGER.debug("Regenerated mana for {}: {}/{}", serverPlayer.getName().getString(), mana.getMana(), mana.getMaxMana());
                }
            }
        }
    }
}
