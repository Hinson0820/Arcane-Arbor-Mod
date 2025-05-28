package net.hinson820.arcanearbor.data;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.block.LuminFruitBlock;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ArcaneArbor.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        logBlock((RotatedPillarBlock) BlockInit.LUMINWOOD_LOG.get());
        logBlock((RotatedPillarBlock) BlockInit.STRIPPED_LUMINWOOD_LOG.get());
        axisBlock((RotatedPillarBlock) BlockInit.LUMINWOOD_WOOD.get(),
                blockTexture(BlockInit.LUMINWOOD_LOG.get()), // Uses BlockInit.LUMINWOOD_LOG.get() to find texture name
                blockTexture(BlockInit.LUMINWOOD_LOG.get()));
        axisBlock((RotatedPillarBlock) BlockInit.STRIPPED_LUMINWOOD_WOOD.get(),
                blockTexture(BlockInit.STRIPPED_LUMINWOOD_LOG.get()),
                blockTexture(BlockInit.STRIPPED_LUMINWOOD_LOG.get()));
        simpleBlockWithItem(BlockInit.LUMINWOOD_LEAVES.get(), cubeAll(BlockInit.LUMINWOOD_LEAVES.get()));
        simpleBlock(BlockInit.LUMINWOOD_SAPLING.get(), models().cross(name(BlockInit.LUMINWOOD_SAPLING), blockTexture(BlockInit.LUMINWOOD_SAPLING.get())).renderType("cutout"));

        registerLuminFruitDebug();

        ModelFile arcaneTableBase = models().getBuilder("block/" + name(BlockInit.ARCANE_ENCHANTMENT_TABLE))
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("top", modLoc("block/" + name(BlockInit.ARCANE_ENCHANTMENT_TABLE) + "_top"))
                .texture("bottom", modLoc("block/" + name(BlockInit.ARCANE_ENCHANTMENT_TABLE) + "_bottom"))
                .texture("side", modLoc("block/" + name(BlockInit.ARCANE_ENCHANTMENT_TABLE) + "_side"))
                .texture("particle", modLoc("block/" + name(BlockInit.ARCANE_ENCHANTMENT_TABLE) + "_side")) // Particle texture
                .element()
                .from(0, 0, 0)
                .to(16, 12, 16)
                .allFaces((direction, faceBuilder) -> {
                    switch (direction) {
                        case NORTH, SOUTH, EAST, WEST -> faceBuilder.texture("#side").cullface(direction).end();
                        case UP -> faceBuilder.texture("#top").cullface(direction).end();
                        case DOWN -> faceBuilder.texture("#bottom").cullface(direction).end();
                    }
                })
                .end();

        simpleBlockWithItem(BlockInit.ARCANE_ENCHANTMENT_TABLE.get(), arcaneTableBase);
    }

    /*
    private void registerLuminFruit() {
        Block block = BlockInit.LUMINFRUIT_BLOCK.get();
        String blockName = name(BlockInit.LUMINFRUIT_BLOCK);

        ModelFile standingLanternModel = models().withExistingParent(blockName, mcLoc("block/template_lantern"))
                .texture("lantern", modLoc("block/" + blockName));

        ModelFile hangingLanternModel = models().withExistingParent(blockName + "_hanging", mcLoc("block/template_hanging_lantern"))
                .texture("lantern", modLoc("block/" + blockName));

        getVariantBuilder(block)
                .partialState().with(LuminFruitBlock.HANGING, false)
                .addModels(new ConfiguredModel(standingLanternModel))
                .partialState().with(LuminFruitBlock.HANGING, true)
                .addModels(new ConfiguredModel(hangingLanternModel));

        simpleBlockItem(block, standingLanternModel);
    }
    */

    private void registerLuminFruitDebug() {
        Block block = BlockInit.LUMINFRUIT_BLOCK.get();
        String baseName = name(BlockInit.LUMINFRUIT_BLOCK);
        ModelFile standingFruitModel = models().withExistingParent(baseName, mcLoc("block/template_lantern"))
                .texture("lantern", modLoc("block/" + baseName))
                .renderType("cutout");

        ModelFile hangingFruitModel = models().withExistingParent(baseName + "_hanging", mcLoc("block/template_hanging_lantern"))
                .texture("lantern", modLoc("block/" + baseName))
                .renderType("cutout");

        getVariantBuilder(block)
                .partialState().with(LuminFruitBlock.HANGING, false)
                .addModels(new ConfiguredModel(standingFruitModel))
                .partialState().with(LuminFruitBlock.HANGING, true)
                .addModels(new ConfiguredModel(hangingFruitModel));

        simpleBlockItem(block, standingFruitModel);
    }

    private String name(DeferredHolder<Block, ? extends Block> blockHolder) {
        return blockHolder.getId().getPath();
    }

}
