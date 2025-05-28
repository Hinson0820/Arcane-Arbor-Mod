package net.hinson820.arcanearbor.common.mana;

import net.hinson820.arcanearbor.common.network.ManaSyncPacket;
import net.hinson820.arcanearbor.core.init.ManaAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;


public class ManaManager {

    public static PlayerMana getManaData(Player player) {
        return player.getData(ManaAttachment.PLAYER_MANA.get());
    }

    public static boolean consumeMana(Player player, int amountToConsume) {
        if (player.level().isClientSide()) {
            return false;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerMana manaData = getManaData(serverPlayer);
            if (manaData.getMana() >= amountToConsume) {
                manaData.consumeMana(amountToConsume);
                syncManaToClient(serverPlayer, manaData);
                return true;
            }
        }
        return false;
    }

    public static void addMana(Player player, int amountToAdd) {
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerMana manaData = getManaData(serverPlayer);
            manaData.addMana(amountToAdd);
            syncManaToClient(serverPlayer, manaData);
        }
    }

    public static void setMaxMana(Player player, int newMaxMana) {
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerMana manaData = getManaData(serverPlayer);
            manaData.setMaxMana(newMaxMana);
            syncManaToClient(serverPlayer, manaData);
        }
    }

    public static void setMana(Player player, int newMana) {
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerMana manaData = getManaData(serverPlayer);
            manaData.setMana(newMana);
            syncManaToClient(serverPlayer, manaData);
        }
    }

    public static void forceSyncMana(ServerPlayer serverPlayer) {
        PlayerMana manaData = getManaData(serverPlayer);
        syncManaToClient(serverPlayer, manaData);
    }

    private static void syncManaToClient(ServerPlayer serverPlayer, PlayerMana manaData) {
        PacketDistributor.sendToPlayer(serverPlayer, new ManaSyncPacket(manaData.getMana(), manaData.getMaxMana()));
    }
}