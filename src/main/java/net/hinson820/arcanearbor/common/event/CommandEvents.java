package net.hinson820.arcanearbor.common.event;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.command.ManaCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ArcaneArbor.MODID)
public class CommandEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ArcaneArbor.LOGGER.info("Registering Arcane Arbor commands...");
        ManaCommands.register(event.getDispatcher());
        ArcaneArbor.LOGGER.info("Arcane Arbor commands registered.");
    }

}
