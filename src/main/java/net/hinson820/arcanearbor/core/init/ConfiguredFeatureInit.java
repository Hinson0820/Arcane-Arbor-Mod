package net.hinson820.arcanearbor.core.init;

import com.google.common.collect.ImmutableList;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.worldgen.tree.LuminwoodFoliagePlacer;
import net.hinson820.arcanearbor.common.worldgen.tree.LuminwoodTrunkPlacer;
import net.hinson820.arcanearbor.common.worldgen.tree.decorator.LuminFruitDecorator;
import net.hinson820.arcanearbor.common.worldgen.tree.decorator.LuminwoodTrunkLeafDecorator;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConfiguredFeatureInit {

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, ArcaneArbor.MODID);

    public static final ResourceKey<ConfiguredFeature<?, ?>> LUMINWOOD_KEY =
            createKey("luminwood_tree");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> blockRegistry = context.lookup(Registries.BLOCK);

        context.register(LUMINWOOD_KEY, new ConfiguredFeature<>(Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(BlockInit.LUMINWOOD_LOG.get()),
                        new LuminwoodTrunkPlacer(
                                ConstantInt.of(70),
                                ConstantInt.of(10),
                                ConstantInt.of(5),
                                ConstantInt.of(8),
                                2.0f,
                                ConstantInt.of(20),
                                ConstantInt.of(4),
                                ConstantInt.of(18),
                                ConstantInt.of(0)
                        ),
                        BlockStateProvider.simple(BlockInit.LUMINWOOD_LEAVES.get()),
                        new LuminwoodFoliagePlacer(
                                ConstantInt.of(1),
                                ConstantInt.of(0),
                                ConstantInt.of(2)
                        ),
                        new TwoLayersFeatureSize(1, 0, 1)
                )
                        .dirt(BlockStateProvider.simple(Blocks.DIRT))
                        .forceDirt()
                        .decorators(ImmutableList.of(
                                new LuminFruitDecorator(
                                        0.05f,
                                        0.45f
                                ),
                                new LuminwoodTrunkLeafDecorator(0.9f, 0.4f) // probability, second_leaf_chance
                                )
                        )
                        .build()));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, name));
    }

}
