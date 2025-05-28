package net.hinson820.arcanearbor.common.worldgen.tree.decorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hinson820.arcanearbor.common.block.LuminFruitBlock;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.hinson820.arcanearbor.core.init.FeatureInit;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

public class LuminFruitDecorator extends TreeDecorator {

    public static final MapCodec<LuminFruitDecorator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(d -> d.probability),
                    Codec.floatRange(0.0F, 1.0F).optionalFieldOf("stem_end_normalized_height_for_fruit", 0.45f).forGetter(d -> d.stemEndNormalizedHeightForFruit)
            ).apply(instance, LuminFruitDecorator::new)
    );

    private final float probability;
    private final float stemEndNormalizedHeightForFruit;

    public LuminFruitDecorator(float probability, float stemEndNormalizedHeightForFruit) {
        this.probability = probability;
        this.stemEndNormalizedHeightForFruit = stemEndNormalizedHeightForFruit;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return FeatureInit.LUMINFRUIT_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        List<BlockPos> allLogs = context.logs();

        if (allLogs.isEmpty()) {
            return;
        }

        int minYLog = Integer.MAX_VALUE;
        int maxYLog = Integer.MIN_VALUE;
        for (BlockPos logPos : allLogs) {
            minYLog = Math.min(minYLog, logPos.getY());
            maxYLog = Math.max(maxYLog, logPos.getY());
        }

        int trunkActualHeight = maxYLog - minYLog + 1;
        if (trunkActualHeight <= 0) {
            return;
        }

        int stemTopAbsoluteY = minYLog + (int) (trunkActualHeight * this.stemEndNormalizedHeightForFruit);

        List<BlockPos> potentialHangingSupports = Lists.newArrayList();
        potentialHangingSupports.addAll(allLogs);
        potentialHangingSupports.addAll(context.leaves());

        for (BlockPos supportPos : potentialHangingSupports) {
            if (supportPos.getY() > stemTopAbsoluteY) {
                if (random.nextFloat() < this.probability) {
                    BlockPos fruitPos = supportPos.below();

                    if (canPlaceFruitAt(context, fruitPos)) {
                        context.setBlock(fruitPos, BlockInit.LUMINFRUIT_BLOCK.get().defaultBlockState()
                                .setValue(LuminFruitBlock.HANGING, true)
                                .setValue(LuminFruitBlock.WATERLOGGED, false) // Usually false for fruit from decorators
                        );
                    }
                }
            }
        }
    }

    private boolean canPlaceFruitAt(Context context, BlockPos pos) {
        if (context.level() instanceof net.minecraft.world.level.LevelSimulatedRW levelSimulatedRW) {
            return context.isAir(pos) || levelSimulatedRW.isStateAtPosition(pos, BlockBehaviour.BlockStateBase::canBeReplaced);
        } else if (context.level() instanceof net.minecraft.world.level.BlockGetter blockGetter) {
            return context.isAir(pos) || blockGetter.getBlockState(pos).canBeReplaced();
        }
        return false;
    }
}
