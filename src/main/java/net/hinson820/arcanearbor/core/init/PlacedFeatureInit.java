package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class PlacedFeatureInit {

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registries.PLACED_FEATURE, ArcaneArbor.MODID);

    public static final ResourceKey<PlacedFeature> LUMINWOOD_PLACED_KEY =
            createKey("luminwood_tree_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> luminwoodConfigured = configuredFeatures.getOrThrow(ConfiguredFeatureInit.LUMINWOOD_KEY);

        // Rarity: 1 per 50000x50000 blocks.
        // A chunk is 16x16 blocks.
        // 50000 / 16 = 3125 chunks.
        // Area in chunks: 3125 * 3125 = 9,765,625 chunks.
        // So, RarityFilter.onAverageOnceEvery(9,765,625)
        // This is extremely rare.
        // int rarity = 9765625; // PRODUCTION VALUE
        int rarity = 50; // TEST VALUE

        context.register(LUMINWOOD_PLACED_KEY, new PlacedFeature(luminwoodConfigured,
                List.of(
                        RarityFilter.onAverageOnceEvery(rarity),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )));
    }

    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, name));
    }

}
