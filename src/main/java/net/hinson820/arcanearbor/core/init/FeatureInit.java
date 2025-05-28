package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.worldgen.tree.LuminwoodFoliagePlacer;
import net.hinson820.arcanearbor.common.worldgen.tree.LuminwoodTrunkPlacer;
import net.hinson820.arcanearbor.common.worldgen.tree.decorator.LuminFruitDecorator;
import net.hinson820.arcanearbor.common.worldgen.tree.decorator.LuminwoodTrunkLeafDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FeatureInit {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, ArcaneArbor.MODID);
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, ArcaneArbor.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, ArcaneArbor.MODID);


    public static final Supplier<TrunkPlacerType<LuminwoodTrunkPlacer>> LUMINWOOD_TRUNK_PLACER_TYPE =
            TRUNK_PLACER_TYPES.register("luminwood_trunk_placer", () -> new TrunkPlacerType<>(LuminwoodTrunkPlacer.CODEC));

    public static final Supplier<FoliagePlacerType<LuminwoodFoliagePlacer>> LUMINWOOD_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPES.register("luminwood_foliage_placer", () -> new FoliagePlacerType<>(LuminwoodFoliagePlacer.CODEC));

    public static final Supplier<TreeDecoratorType<LuminwoodTrunkLeafDecorator>> LUMINWOOD_TRUNK_LEAF_DECORATOR =
            TREE_DECORATOR_TYPES.register("luminwood_trunk_leaf_decorator", () -> new TreeDecoratorType<>(LuminwoodTrunkLeafDecorator.CODEC));

    public static final Supplier<TreeDecoratorType<LuminFruitDecorator>> LUMINFRUIT_DECORATOR =
            TREE_DECORATOR_TYPES.register("luminfruit_decorator", () -> new TreeDecoratorType<>(LuminFruitDecorator.CODEC));

}
