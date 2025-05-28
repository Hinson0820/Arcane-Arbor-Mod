package net.hinson820.arcanearbor.data;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.core.init.BiomeModifierInit;
import net.hinson820.arcanearbor.core.init.ConfiguredFeatureInit;
import net.hinson820.arcanearbor.core.init.PlacedFeatureInit;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureInit::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatureInit::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                HolderGetter<Biome> biomes = bootstrap.lookup(Registries.BIOME);
                HolderGetter<PlacedFeature> placedFeatures = bootstrap.lookup(Registries.PLACED_FEATURE);

                bootstrap.register(
                        BiomeModifierInit.ADD_LUMINWOOD_TO_DARK_FOREST,
                        new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
                                HolderSet.direct(placedFeatures.getOrThrow(PlacedFeatureInit.LUMINWOOD_PLACED_KEY)),
                                GenerationStep.Decoration.VEGETAL_DECORATION
                        )
                );
            });

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArcaneArbor.MODID));
    }

    @Override
    public String getName() {
        return ArcaneArbor.MODID + " World Gen Features and Modifiers";
    }
}
