package net.hinson820.arcanearbor.data;

import net.hinson820.arcanearbor.core.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(BlockInit.LUMINWOOD_LOG.get());
        dropSelf(BlockInit.STRIPPED_LUMINWOOD_LOG.get());
        dropSelf(BlockInit.LUMINWOOD_WOOD.get());
        dropSelf(BlockInit.STRIPPED_LUMINWOOD_WOOD.get());
        dropSelf(BlockInit.LUMINWOOD_SAPLING.get());

        dropSelf(BlockInit.ARCANE_ENCHANTMENT_TABLE.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BlockInit.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .collect(java.util.stream.Collectors.toList());
    }
}
