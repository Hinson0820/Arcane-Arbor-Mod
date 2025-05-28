package net.hinson820.arcanearbor.data;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ArcaneArbor.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.LOGS)
                .add(BlockInit.LUMINWOOD_LOG.get())
                .add(BlockInit.STRIPPED_LUMINWOOD_LOG.get())
                .add(BlockInit.LUMINWOOD_WOOD.get())
                .add(BlockInit.STRIPPED_LUMINWOOD_WOOD.get());
        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(BlockInit.LUMINWOOD_LOG.get())
                .add(BlockInit.STRIPPED_LUMINWOOD_LOG.get())
                .add(BlockInit.LUMINWOOD_WOOD.get())
                .add(BlockInit.STRIPPED_LUMINWOOD_WOOD.get());

        this.tag(BlockTags.LEAVES)
                .add(BlockInit.LUMINWOOD_LEAVES.get());
        this.tag(BlockTags.SAPLINGS)
                .add(BlockInit.LUMINWOOD_SAPLING.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(BlockInit.LUMINWOOD_LOG.get())
                .add(BlockInit.STRIPPED_LUMINWOOD_LOG.get())
                .add(BlockInit.LUMINWOOD_WOOD.get())
                .add(BlockInit.STRIPPED_LUMINWOOD_WOOD.get())
                .add(BlockInit.LUMINFRUIT_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_HOE)
                .add(BlockInit.LUMINWOOD_LEAVES.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BlockInit.ARCANE_ENCHANTMENT_TABLE.get());

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .add(BlockInit.ARCANE_ENCHANTMENT_TABLE.get());

    }
}