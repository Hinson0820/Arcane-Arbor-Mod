package net.hinson820.arcanearbor.common.block;

import net.hinson820.arcanearbor.core.init.BlockInit;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

public class LuminwoodLogBlock extends RotatedPillarBlock {

    public LuminwoodLogBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        // ^^^ --- Parameter type changed to ItemAbility itemAbility

        if (itemAbility == ItemAbilities.AXE_STRIP) { // <<< --- Comparison changed to ItemAbilities.AXE_STRIP
            if (state.is(BlockInit.LUMINWOOD_LOG.get())) {
                return BlockInit.STRIPPED_LUMINWOOD_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
            if (state.is(BlockInit.LUMINWOOD_WOOD.get())) {
                return BlockInit.STRIPPED_LUMINWOOD_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

}
