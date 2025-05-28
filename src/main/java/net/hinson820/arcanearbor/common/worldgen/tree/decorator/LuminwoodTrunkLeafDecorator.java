package net.hinson820.arcanearbor.common.worldgen.tree.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.hinson820.arcanearbor.core.init.FeatureInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class LuminwoodTrunkLeafDecorator extends TreeDecorator {

    public static final MapCodec<LuminwoodTrunkLeafDecorator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(d -> d.probability),
                    Codec.floatRange(0.0F, 1.0F).optionalFieldOf("second_leaf_chance", 0.3f).forGetter(d -> d.secondLeafChance)
            ).apply(instance, LuminwoodTrunkLeafDecorator::new)
    );

    private final float probability;
    private final float secondLeafChance;

    public LuminwoodTrunkLeafDecorator(float probability, float secondLeafChance) {
        this.probability = probability;
        this.secondLeafChance = secondLeafChance;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return FeatureInit.LUMINWOOD_TRUNK_LEAF_DECORATOR.get();
    }

    private static final float STEM_END_H = 0.45f;
    private static final float BOWL_TOP_APPROX_H = 0.98f;

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        if (context.logs().isEmpty()) {
            return;
        }

        int minYLog = context.logs().getFirst().getY();
        int maxYLog = context.logs().getLast().getY();
        int trunkActualHeight = maxYLog - minYLog + 1;
        if (trunkActualHeight <= 0) return;

        context.logs().forEach(logPos -> {
            float normalizedLogY = (float)(logPos.getY() - minYLog) / trunkActualHeight;

            if (normalizedLogY >= STEM_END_H && normalizedLogY < BOWL_TOP_APPROX_H) {
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < this.probability) {
                        BlockPos leafPos = logPos.relative(side);

                        if (canPlaceLeaf(context, leafPos)) {
                            context.setBlock(leafPos, BlockInit.LUMINWOOD_LEAVES.get().defaultBlockState());

                            if (random.nextFloat() < this.secondLeafChance) {
                                Direction secondLeafSide = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                                BlockPos secondLeafPos = leafPos.relative(secondLeafSide);
                                if (!secondLeafPos.equals(logPos) && !secondLeafPos.equals(leafPos.relative(secondLeafSide.getOpposite()))) {
                                    if (canPlaceLeaf(context, secondLeafPos)) {
                                        context.setBlock(secondLeafPos, BlockInit.LUMINWOOD_LEAVES.get().defaultBlockState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean canPlaceLeaf(Context context, BlockPos pos) {
        if (context.level() instanceof net.minecraft.world.level.LevelSimulatedRW levelSimulatedRW) {
            return context.isAir(pos) || levelSimulatedRW.isStateAtPosition(pos, state -> state.canBeReplaced());
        } else if (context.level() instanceof net.minecraft.world.level.BlockGetter blockGetter) {
            return context.isAir(pos) || blockGetter.getBlockState(pos).canBeReplaced();
        }
        return false;
    }
}
