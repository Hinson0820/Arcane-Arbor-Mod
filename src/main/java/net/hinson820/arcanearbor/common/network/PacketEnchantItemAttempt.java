package net.hinson820.arcanearbor.common.network;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.block.entity.ArcaneEnchantmentTableBlockEntity;
import net.hinson820.arcanearbor.common.menu.ArcaneEnchantmentMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketEnchantItemAttempt(BlockPos tablePos, String enchantmentId) implements CustomPacketPayload {

    public static final Type<PacketEnchantItemAttempt> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "enchant_item_attempt"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketEnchantItemAttempt> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketEnchantItemAttempt::tablePos,
            ByteBufCodecs.STRING_UTF8,
            PacketEnchantItemAttempt::enchantmentId,
            PacketEnchantItemAttempt::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PacketEnchantItemAttempt message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) {
                ArcaneArbor.LOGGER.warn("PacketEnchantItemAttempt received with no player context.");
                return;
            }

            Level level = player.level();
            BlockPos tablePos = message.tablePos();

            if (!level.isLoaded(tablePos)) {
                ArcaneArbor.LOGGER.warn("Player {} tried to enchant at unloaded position: {}", player.getName().getString(), tablePos);
                return;
            }
            if (player.distanceToSqr(tablePos.getX() + 0.5, tablePos.getY() + 0.5, tablePos.getZ() + 0.5) > 64.0D) {
                ArcaneArbor.LOGGER.warn("Player {} tried to enchant from too far away: {}", player.getName().getString(), tablePos);
                return;
            }

            BlockEntity be = level.getBlockEntity(tablePos);
            if (be instanceof ArcaneEnchantmentTableBlockEntity aetBe) {
                if (player.containerMenu instanceof ArcaneEnchantmentMenu currentMenu &&
                        currentMenu.blockEntity == aetBe) {

                    boolean success = aetBe.attemptEnchantment(player, message.enchantmentId());

                } else {
                    ArcaneArbor.LOGGER.warn("Player {} tried to enchant via packet but doesn't have the correct menu open for BE at {}",
                            player.getName().getString(), tablePos);
                }
            } else {
                ArcaneArbor.LOGGER.warn("Player {} tried to enchant at a position without an ArcaneEnchantmentTableBlockEntity: {}",
                        player.getName().getString(), tablePos);
            }
        }).exceptionally(e -> {
            ArcaneArbor.LOGGER.error("Exception handling PacketEnchantItemAttempt for player {}: {}", context.player() != null ? context.player().getName().getString() : "UNKNOWN", e);
            return null;
        });
    }
}
