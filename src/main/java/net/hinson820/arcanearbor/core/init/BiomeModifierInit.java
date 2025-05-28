package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BiomeModifierInit {

    public static final ResourceKey<BiomeModifier> ADD_LUMINWOOD_TO_DARK_FOREST =
            ResourceKey.create(
                    NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                    ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "add_luminwood_to_dark_forest")
            );
}
