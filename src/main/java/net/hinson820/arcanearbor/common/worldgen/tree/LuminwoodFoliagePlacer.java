package net.hinson820.arcanearbor.common.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hinson820.arcanearbor.core.init.FeatureInit;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class LuminwoodFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<LuminwoodFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(
                    IntProvider.codec(1, 3).fieldOf("cap_height").forGetter(fp -> fp.capHeight)
            ).apply(instance, LuminwoodFoliagePlacer::new));

    private final IntProvider capHeight;

    public LuminwoodFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider capHeight) {
        super(radius, offset);
        this.capHeight = capHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FeatureInit.LUMINWOOD_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter,
                                 RandomSource random, TreeConfiguration config, int trunkHeight,
                                 FoliageAttachment attachment, int foliageHeight, int foliageRadius, int layerOffset) {

        BlockPos centerOfCap = attachment.pos().above(layerOffset);

        for (int yRel = 0; yRel < foliageHeight; ++yRel) {
            for (int xRel = -foliageRadius; xRel <= foliageRadius; ++xRel) {
                for (int zRel = -foliageRadius; zRel <= foliageRadius; ++zRel) {
                    if (foliageRadius == 0 || Math.abs(xRel) + Math.abs(zRel) <= foliageRadius) {
                        if (random.nextFloat() < 0.4F) {
                            tryPlaceLeaf(level, foliageSetter, random, config, centerOfCap.offset(xRel, yRel, zRel));
                        }
                    }
                }
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int trunkHeight, TreeConfiguration config) {
        return this.capHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int dx, int dy, int dz, int range, boolean giantTrunk) {
        return false;
    }

}
