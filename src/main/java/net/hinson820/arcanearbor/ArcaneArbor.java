package net.hinson820.arcanearbor;

import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.config.Configs;
import net.hinson820.arcanearbor.core.init.*;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ArcaneArbor.MODID)
public class ArcaneArbor {
    public static final String MODID = "arcanearbor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArcaneArbor(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Arcane Arbor Mod Loading...");

        ManaAttachment.ATTACHMENT_TYPES.register(modEventBus);
        DataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        BlockInit.BLOCKS.register(modEventBus);
        BlockEntitiesInit.BLOCK_ENTITIES.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        CreativeModeTabInit.CREATIVE_MODE_TABS.register(modEventBus);

        FeatureInit.TRUNK_PLACER_TYPES.register(modEventBus);
        FeatureInit.FOLIAGE_PLACER_TYPES.register(modEventBus);
        FeatureInit.TREE_DECORATOR_TYPES.register(modEventBus);

        MenuTypesInit.MENUS.register(modEventBus);

        ConfiguredFeatureInit.CONFIGURED_FEATURES.register(modEventBus);
        PlacedFeatureInit.PLACED_FEATURES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Configs.COMMON_SPEC);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ArcaneEnchantmentRegistry.initialize();
    }

}
