package net.hinson820.arcanearbor.common.network;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ArcaneArbor.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ArcaneArbor.MODID)
                .versioned(PROTOCOL_VERSION);

        registrar.playToClient(
                ManaSyncPacket.TYPE,
                ManaSyncPacket.STREAM_CODEC,
                ManaSyncPacket::handle
        );

        registrar.playToServer(
                PacketEnchantItemAttempt.TYPE,
                PacketEnchantItemAttempt.STREAM_CODEC,
                PacketEnchantItemAttempt::handle
        );

        ArcaneArbor.LOGGER.info("Registered {} network payloads.", ArcaneArbor.MODID);
    }
}