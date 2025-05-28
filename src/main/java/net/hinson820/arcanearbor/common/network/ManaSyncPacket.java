package net.hinson820.arcanearbor.common.network;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.mana.PlayerMana;
import net.hinson820.arcanearbor.core.init.ManaAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ManaSyncPacket(int mana, int maxMana) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "mana_sync");
    public static final CustomPacketPayload.Type<ManaSyncPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, ManaSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ManaSyncPacket::mana,
            ByteBufCodecs.INT, ManaSyncPacket::maxMana,
            ManaSyncPacket::new
    );

    @Override
    public CustomPacketPayload.Type<ManaSyncPacket> type() {
        return TYPE;
    }

    public static void handle(ManaSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                PlayerMana manaData = player.getData(ManaAttachment.PLAYER_MANA.get());
                manaData.setMaxMana(packet.maxMana());
                manaData.setMana(packet.mana());
            }
        });
    }
}