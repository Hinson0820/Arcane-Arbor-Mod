package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockInit {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ArcaneArbor.MODID);

    public static final DeferredBlock<Block> LUMINWOOD_LOG = registerBlock("luminwood_log",
            () -> new LuminwoodLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
                    .mapColor(MapColor.GOLD)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 8)));

    public static final DeferredBlock<Block> STRIPPED_LUMINWOOD_LOG = registerBlock("stripped_luminwood_log",
            () -> new LuminwoodLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 8)));

    public static final DeferredBlock<Block> LUMINWOOD_WOOD = registerBlock("luminwood_wood",
            () -> new LuminwoodLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)
                    .mapColor(MapColor.GOLD)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 3)));

    public static final DeferredBlock<Block> STRIPPED_LUMINWOOD_WOOD = registerBlock("stripped_luminwood_wood",
            () -> new LuminwoodLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 3)));

    public static final DeferredBlock<Block> LUMINWOOD_LEAVES = registerBlock("luminwood_leaves",
            () -> new LuminwoodLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.2f)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .lightLevel(state -> 10).noLootTable()));

    public static final DeferredBlock<Block> LUMINFRUIT_BLOCK = registerBlock("luminfruit_block",
            () -> new LuminFruitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(0.1f)
                    .sound(SoundType.HANGING_SIGN)
                    .noOcclusion()
                    .lightLevel(state -> 15).noLootTable()));

    public static final TreeGrower LUMINWOOD_TREE_GROWER = new TreeGrower(
            ArcaneArbor.MODID + "_luminwood",
            0.0F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(ConfiguredFeatureInit.LUMINWOOD_KEY),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final DeferredBlock<Block> LUMINWOOD_SAPLING = registerBlock("luminwood_sapling",
            () -> new LuminwoodSaplingBlock(LUMINWOOD_TREE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .lightLevel(state -> 5)));

    public static final DeferredBlock<Block> ARCANE_ENCHANTMENT_TABLE = registerBlock("arcane_enchantment_table",
            () -> new ArcaneEnchantmentTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 1200.0F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 7)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    // public static void registerFlammability() {}
}
