package net.hinson820.arcanearbor.common.worldgen.tree;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.core.init.FeatureInit;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class LuminwoodTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<LuminwoodTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    IntProvider.codec(20, 200).fieldOf("base_height").forGetter(p -> p.customBaseHeight),
                    IntProvider.codec(0, 50).fieldOf("height_rand_a").forGetter(p -> p.customHeightRandA),
                    IntProvider.codec(0, 50).fieldOf("height_rand_b").forGetter(p -> p.customHeightRandB),
                    IntProvider.codec(3, 8).fieldOf("num_strands").forGetter(p -> p.numStrands),
                    Codec.FLOAT.fieldOf("revolutions_in_stem").forGetter(p -> p.revolutionsInStem),
                    IntProvider.codec(10, 25).fieldOf("flare_base_radius").forGetter(p -> p.flareBaseRadius),
                    IntProvider.codec(2, 6).fieldOf("stem_radius").forGetter(p -> p.stemRadius),
                    IntProvider.codec(8, 25).fieldOf("bowl_max_radius").forGetter(p -> p.bowlMaxRadius),
                    IntProvider.codec(0, 3).fieldOf("strand_thickness").forGetter(p -> p.strandThickness)
            ).apply(instance, LuminwoodTrunkPlacer::new)
    );

    private final IntProvider customBaseHeight;
    private final IntProvider customHeightRandA;
    private final IntProvider customHeightRandB;
    private final IntProvider numStrands;
    private final float revolutionsInStem;
    private final IntProvider flareBaseRadius;
    private final IntProvider stemRadius;
    private final IntProvider bowlMaxRadius;
    private final IntProvider strandThickness;

    private static final float BASE_FLARE_END_H = 0.1f;
    private static final float STEM_END_H = 0.45f;
    private static final float BOWL_LOWER_EXPAND_END_H = 0.65f;
    private static final float BOWL_UPPER_EXPAND_END_H = 0.80f;
    private static final float BOWL_CONVERGE_END_H = 0.98f;

    public LuminwoodTrunkPlacer(IntProvider baseHeight, IntProvider heightRandA, IntProvider heightRandB,
                                IntProvider numStrands, float revolutionsInStem,
                                IntProvider flareBaseRadius, IntProvider stemRadius, IntProvider bowlMaxRadius,
                                IntProvider strandThickness) {
        super(1, 0, 0);
        this.customBaseHeight = baseHeight;
        this.customHeightRandA = heightRandA;
        this.customHeightRandB = heightRandB;
        this.numStrands = numStrands;
        this.revolutionsInStem = revolutionsInStem;
        this.flareBaseRadius = flareBaseRadius;
        this.stemRadius = stemRadius;
        this.bowlMaxRadius = bowlMaxRadius;
        this.strandThickness = strandThickness;
        ArcaneArbor.LOGGER.debug("LuminwoodTrunkPlacer INSTANCE CREATED.");
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return FeatureInit.LUMINWOOD_TRUNK_PLACER_TYPE.get();
    }

    @Override
    public int getTreeHeight(RandomSource pRandom) {
        int h = this.customBaseHeight.sample(pRandom) +
                this.customHeightRandA.sample(pRandom) +
                this.customHeightRandB.sample(pRandom);
        return Math.max(20, h);
    }

    private float getRadiusProfile(float normalizedHeight, float actualFlareBaseRadius, float actualStemRadius, float actualBowlMaxRadius, RandomSource random) {
        if (normalizedHeight < BASE_FLARE_END_H) {
            float progress = Mth.inverseLerp(normalizedHeight, 0.0f, BASE_FLARE_END_H);
            return Mth.lerp(progress, actualFlareBaseRadius, actualStemRadius);
        } else if (normalizedHeight < STEM_END_H) {
            return actualStemRadius;
        } else if (normalizedHeight < BOWL_LOWER_EXPAND_END_H) {
            float progress = Mth.inverseLerp(normalizedHeight, STEM_END_H, BOWL_LOWER_EXPAND_END_H);
            return Mth.lerp(Mth.sin(progress * Mth.HALF_PI), actualStemRadius, actualBowlMaxRadius);
        } else if (normalizedHeight < BOWL_UPPER_EXPAND_END_H) {
            float progress = Mth.inverseLerp(normalizedHeight, BOWL_LOWER_EXPAND_END_H, BOWL_UPPER_EXPAND_END_H);
            float bulgeRadius = actualBowlMaxRadius * (1.0f + Mth.sin(progress * Mth.PI) * 0.15f);
            return bulgeRadius;
        } else if (normalizedHeight < BOWL_CONVERGE_END_H) {
            float progress = Mth.inverseLerp(normalizedHeight, BOWL_UPPER_EXPAND_END_H, BOWL_CONVERGE_END_H);
            float targetConvergenceRadius = actualBowlMaxRadius * 0.4f;
            targetConvergenceRadius = Math.max(actualStemRadius * 0.8f, targetConvergenceRadius);
            targetConvergenceRadius = Math.max(1.0f, targetConvergenceRadius);
            return Mth.lerp(progress, actualBowlMaxRadius, targetConvergenceRadius);
        } else {
            float radiusAtConvergenceEnd = actualBowlMaxRadius * 0.4f;
            radiusAtConvergenceEnd = Math.max(actualStemRadius * 0.8f, radiusAtConvergenceEnd);
            radiusAtConvergenceEnd = Math.max(1.0f, radiusAtConvergenceEnd);
            float progress = Mth.inverseLerp(normalizedHeight, BOWL_CONVERGE_END_H, 1.0f);
            return Mth.lerp(progress, radiusAtConvergenceEnd, 0.5f);
        }
    }

    protected boolean canPlaceLogAt(LevelSimulatedReader level, BlockPos pos) {
        if (level instanceof LevelHeightAccessor heightAccessor) {
            if (heightAccessor.isOutsideBuildHeight(pos.getY())) {
                return false;
            }
        } else {
            ArcaneArbor.LOGGER.warn("LevelSimulatedReader is not a LevelHeightAccessor, cannot check build height for logs at {}.", pos);
            return false;
        }

        if (level instanceof LevelSimulatedRW levelSimulatedRW) {
            return TreeFeature.validTreePos(levelSimulatedRW, pos);
        } else {
            ArcaneArbor.LOGGER.warn("LevelSimulatedReader is not a LevelSimulatedRW (or BlockGetter), cannot use TreeFeature.validTreePos at {}.", pos);
            return false;
        }
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                            RandomSource random, int treeHeight, BlockPos startPos,
                                                            TreeConfiguration treeConfig) {
        setDirtAt(level, blockSetter, random, startPos.below(), treeConfig);

        int actualNumStrands = this.numStrands.sample(random);
        float actualFlareBaseRadius = this.flareBaseRadius.sample(random);
        float actualStemRadius = this.stemRadius.sample(random);
        float actualBowlMaxRadius = this.bowlMaxRadius.sample(random);
        int baseStrandThicknessRadiusConfig = this.strandThickness.sample(random);

        List<FoliagePlacer.FoliageAttachment> foliageAttachments = Lists.newArrayList();
        BlockPos.MutableBlockPos mutablePosForDisc = new BlockPos.MutableBlockPos();

        double[] initialStrandAngles = new double[actualNumStrands];
        for (int i = 0; i < actualNumStrands; ++i) {
            initialStrandAngles[i] = (Math.PI * 2.0 * i) / actualNumStrands;
        }

        final float detailStrandStartNormalizedH = STEM_END_H;
        final float detailStrandEndNormalizedH = BOWL_CONVERGE_END_H;

        BlockPos[] prevStrandTipPositions = new BlockPos[actualNumStrands];

        for (int i = 0; i < actualNumStrands; i++) {
            float initialOverallRadius = getRadiusProfile(0.0f, actualFlareBaseRadius, actualStemRadius, actualBowlMaxRadius, random);
            double initialAngle = initialStrandAngles[i];
            double initialDx = initialOverallRadius * Math.cos(initialAngle);
            double initialDz = initialOverallRadius * Math.sin(initialAngle);
            prevStrandTipPositions[i] = new BlockPos(
                    startPos.getX() + (int) Math.round(initialDx),
                    startPos.getY() -1,
                    startPos.getZ() + (int) Math.round(initialDz)
            );
        }


        int actualHighestLogY = startPos.getY() - 1;

        for (int yOffset = 0; yOffset < treeHeight; ++yOffset) {
            int currentBlockAbsoluteY = startPos.getY() + yOffset;

            if (level instanceof LevelHeightAccessor heightAccessor) {
                if (heightAccessor.isOutsideBuildHeight(currentBlockAbsoluteY)) break;
            }

            float normalizedHeight = treeHeight <= 0 ? 0 : (float) yOffset / (float) treeHeight;
            float currentOverallRadius = getRadiusProfile(normalizedHeight, actualFlareBaseRadius, actualStemRadius, actualBowlMaxRadius, random);

            float thicknessLerpFactor = Mth.clamp(1.0f - normalizedHeight * 1.2f, 0.1f, 1.0f);
            int currentStrandEffectiveThicknessRadius = Math.max(0, (int) (baseStrandThicknessRadiusConfig * thicknessLerpFactor));
            if (normalizedHeight > BASE_FLARE_END_H && normalizedHeight < STEM_END_H) {
                currentStrandEffectiveThicknessRadius = Math.min(currentStrandEffectiveThicknessRadius, 1);
            }


            boolean logsPlacedThisY = false;
            boolean actuallyPlaceCentralPillar = (normalizedHeight >= BOWL_CONVERGE_END_H) && currentOverallRadius < 1.5f;

            if (actuallyPlaceCentralPillar) {
                mutablePosForDisc.set(startPos.getX(), currentBlockAbsoluteY, startPos.getZ());
                placeLog(level, blockSetter, random, mutablePosForDisc, treeConfig);
                logsPlacedThisY = true;
                BlockPos pillarPosAtPrevY = new BlockPos(startPos.getX(), currentBlockAbsoluteY -1, startPos.getZ());
                for(int i=0; i<actualNumStrands; ++i) prevStrandTipPositions[i] = pillarPosAtPrevY;
            } else {
                for (int strandIdx = 0; strandIdx < actualNumStrands; ++strandIdx) {
                    double strandCurrentAngle;
                    if (normalizedHeight < STEM_END_H) {
                        float normalizedHeightInTwistableRegion = Mth.inverseLerp(normalizedHeight, 0.0f, STEM_END_H);
                        strandCurrentAngle = initialStrandAngles[strandIdx] + (normalizedHeightInTwistableRegion * (Math.PI * 2.0 * this.revolutionsInStem));
                    } else {
                        strandCurrentAngle = initialStrandAngles[strandIdx] + (Math.PI * 2.0 * this.revolutionsInStem);
                    }

                    double dx = currentOverallRadius * Math.cos(strandCurrentAngle);
                    double dz = currentOverallRadius * Math.sin(strandCurrentAngle);

                    BlockPos currentStrandTargetCenter = new BlockPos(
                            startPos.getX() + (int) Math.round(dx),
                            currentBlockAbsoluteY,
                            startPos.getZ() + (int) Math.round(dz)
                    );

                    BlockPos startInterpolationBlock = prevStrandTipPositions[strandIdx];
                    if (startInterpolationBlock.getY() != currentBlockAbsoluteY -1 ) {
                        startInterpolationBlock = currentStrandTargetCenter.below();
                    }

                    BlockPos lastPlacedBlockForThisStrandAtThisY = startInterpolationBlock;

                    int numSteps = Math.max(
                            Math.abs(currentStrandTargetCenter.getX() - startInterpolationBlock.getX()),
                            Math.abs(currentStrandTargetCenter.getZ() - startInterpolationBlock.getZ())
                    ) + 1;

                    for (int step = 0; step < numSteps; ++step) {
                        float progress = (numSteps == 1) ? 1.0f : (float) step / (float) (numSteps - 1);

                        int interpX = (int) Math.round(Mth.lerp(progress, startInterpolationBlock.getX(), currentStrandTargetCenter.getX()));
                        int interpZ = (int) Math.round(Mth.lerp(progress, startInterpolationBlock.getZ(), currentStrandTargetCenter.getZ()));
                        BlockPos blockToPlaceDiscAt = new BlockPos(interpX, currentBlockAbsoluteY, interpZ);

                        placeStrandDisc(level, blockSetter, random, blockToPlaceDiscAt, currentStrandEffectiveThicknessRadius, treeConfig, mutablePosForDisc);
                        logsPlacedThisY = true;
                        lastPlacedBlockForThisStrandAtThisY = blockToPlaceDiscAt;
                    }
                    prevStrandTipPositions[strandIdx] = lastPlacedBlockForThisStrandAtThisY;

                    if (normalizedHeight >= detailStrandStartNormalizedH && normalizedHeight < detailStrandEndNormalizedH) {
                        if (random.nextFloat() < 0.30f) {
                            int wispMaxLength = random.nextIntBetweenInclusive(5, 10);
                            double wispAngle = strandCurrentAngle + (random.nextFloat() - 0.5) * (Mth.PI / 1.5f);
                            float wispRadialFactor = 1.0f + (random.nextFloat() - 0.5f) * 0.2f;
                            BlockPos wispCursor = lastPlacedBlockForThisStrandAtThisY;

                            for (int l = 0; l < wispMaxLength; ++l) {
                                int nextWispBlockAbsoluteY = wispCursor.getY() + 1;
                                if (nextWispBlockAbsoluteY >= startPos.getY() + treeHeight -1) break;

                                float wispNormH = (float)(nextWispBlockAbsoluteY - startPos.getY()) / (float)treeHeight;
                                float treeRadiusAtWispY = getRadiusProfile(wispNormH, actualFlareBaseRadius, actualStemRadius, actualBowlMaxRadius, random);

                                wispAngle += (random.nextFloat() - 0.5) * (Mth.PI / 8.0f);
                                wispRadialFactor += (random.nextFloat() - 0.5f) * 0.03f;
                                wispRadialFactor = Mth.clamp(wispRadialFactor, 0.6f, 1.4f);
                                float currentWispRadius = Math.max(0.5f, treeRadiusAtWispY * wispRadialFactor);

                                BlockPos idealTargetPos = new BlockPos(
                                        startPos.getX() + (int)Math.round(currentWispRadius * Math.cos(wispAngle)),
                                        nextWispBlockAbsoluteY,
                                        startPos.getZ() + (int)Math.round(currentWispRadius * Math.sin(wispAngle))
                                );
                                BlockPos actualNextWispBlock = new BlockPos(
                                        Mth.clamp(idealTargetPos.getX(), wispCursor.getX() - 1, wispCursor.getX() + 1),
                                        nextWispBlockAbsoluteY,
                                        Mth.clamp(idealTargetPos.getZ(), wispCursor.getZ() - 1, wispCursor.getZ() + 1)
                                );

                                if (canPlaceLogAt(level, actualNextWispBlock)) {
                                    placeLog(level, blockSetter, random, actualNextWispBlock, treeConfig);
                                    wispCursor = actualNextWispBlock;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if(logsPlacedThisY) {
                actualHighestLogY = currentBlockAbsoluteY;
            }
        }

        BlockPos foliageAttachmentPoint = null;
        if (actualHighestLogY >= startPos.getY()) {
            foliageAttachmentPoint = new BlockPos(startPos.getX(), actualHighestLogY, startPos.getZ());
        } else {
            /* ... fallback logic ... */
        }
        if (foliageAttachmentPoint != null) {
            foliageAttachments.add(new FoliagePlacer.FoliageAttachment(foliageAttachmentPoint, 0, false));
        }

        return foliageAttachments;
    }

    private void placeStrandDisc(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                 BlockPos center, int thicknessRadius, TreeConfiguration config, BlockPos.MutableBlockPos mutablePos) {
        if (thicknessRadius <= 0) {
            placeLog(level, blockSetter, random, center, config);
        } else {
            for (int offX = -thicknessRadius; offX <= thicknessRadius; ++offX) {
                for (int offZ = -thicknessRadius; offZ <= thicknessRadius; ++offZ) {
                    if (offX * offX + offZ * offZ <= thicknessRadius * thicknessRadius + 0.5f) {
                        mutablePos.set(center).move(offX, 0, offZ);
                        placeLog(level, blockSetter, random, mutablePos, config);
                    }
                }
            }
        }
    }
}