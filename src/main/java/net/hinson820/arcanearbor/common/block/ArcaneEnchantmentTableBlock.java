package net.hinson820.arcanearbor.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.block.entity.ArcaneEnchantmentTableBlockEntity;
import net.hinson820.arcanearbor.core.init.BlockEntitiesInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ArcaneEnchantmentTableBlock extends BaseEntityBlock {

    public static final MapCodec<ArcaneEnchantmentTableBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec()
            ).apply(instance, ArcaneEnchantmentTableBlock::new)
    );


    public ArcaneEnchantmentTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ArcaneEnchantmentTableBlockEntity(pPos, pState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide) {
            MenuProvider menuProvider = pState.getMenuProvider(pLevel, pPos);
            if (menuProvider != null && pPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(menuProvider, buffer -> buffer.writeBlockPos(pPos));
            } else if (menuProvider == null) {
                ArcaneArbor.LOGGER.error("MenuProvider is NULL for ArcaneEnchantmentTableBlock at " + pPos + " on server.");
            } else {
                ArcaneArbor.LOGGER.warn("Player is not ServerPlayer, cannot open menu with custom data writer.");
            }
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, BlockEntitiesInit.ARCANE_ENCHANTMENT_TABLE_BE.get(),
                ArcaneEnchantmentTableBlockEntity::serverTick);
    }
}
