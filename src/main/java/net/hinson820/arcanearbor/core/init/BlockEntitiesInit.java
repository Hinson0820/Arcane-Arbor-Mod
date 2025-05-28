package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.block.entity.ArcaneEnchantmentTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntitiesInit {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ArcaneArbor.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArcaneEnchantmentTableBlockEntity>> ARCANE_ENCHANTMENT_TABLE_BE =
            BLOCK_ENTITIES.register("arcane_enchantment_table",
                    () -> BlockEntityType.Builder.of(ArcaneEnchantmentTableBlockEntity::new,
                            BlockInit.ARCANE_ENCHANTMENT_TABLE.get()).build(null));

}
