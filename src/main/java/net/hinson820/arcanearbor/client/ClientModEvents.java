package net.hinson820.arcanearbor.client;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.client.gui.ManaHudOverlay;
import net.hinson820.arcanearbor.client.gui.screen.ArcaneEnchantmentScreen;
import net.hinson820.arcanearbor.core.init.MenuTypesInit;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = ArcaneArbor.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    public static final ResourceLocation MANA_BAR_LAYER_ID = ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "mana_bar");

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        try {
            event.register(MenuTypesInit.ARCANE_ENCHANTMENT_MENU.get(), ArcaneEnchantmentScreen::new);
        } catch (Exception e) {
            ArcaneArbor.LOGGER.error("ClientModEvents: Failed to register Arcane Enchantment Screen", e);
        }
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        ArcaneArbor.LOGGER.info("ClientModEvents: Registering GUI layers for " + ArcaneArbor.MODID);
        try {
            event.registerAbove(
                    VanillaGuiLayers.EXPERIENCE_BAR,
                    MANA_BAR_LAYER_ID, // Use the ID defined here or from ArcaneArbor
                    new ManaHudOverlay()
            );
        } catch (Exception e) {
            ArcaneArbor.LOGGER.error("ClientModEvents: Failed to register Mana Bar GUI Layer", e);
        }
    }
}