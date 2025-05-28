package net.hinson820.arcanearbor.data;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ArcaneArbor.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleBlockItem(BlockInit.LUMINWOOD_LOG.get());
        simpleBlockItem(BlockInit.STRIPPED_LUMINWOOD_LOG.get());
        simpleBlockItem(BlockInit.LUMINWOOD_WOOD.get());
        simpleBlockItem(BlockInit.STRIPPED_LUMINWOOD_WOOD.get());
        simpleBlockItem(BlockInit.LUMINWOOD_LEAVES.get());

        generatedItem(BlockInit.LUMINWOOD_SAPLING.get().asItem(), modLoc("block/" + getBlockPath(BlockInit.LUMINWOOD_SAPLING.get())));
    }

    private String getItemPath(DeferredHolder<Item, ? extends Item> itemHolder) {
        return itemHolder.getId().getPath();
    }

    private String getBlockPath(DeferredHolder<Block, ? extends Block> blockHolder) {
        return blockHolder.getId().getPath();
    }

    private String getBlockPath(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        if (key.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
            throw new IllegalStateException("Block " + block + " is not properly registered or has default key!");
        }
        return key.getPath();
    }

    private ItemModelBuilder generatedItem(Item item, ResourceLocation texture) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item).getPath(), mcLoc("item/generated"))
                .texture("layer0", texture);
    }
}
