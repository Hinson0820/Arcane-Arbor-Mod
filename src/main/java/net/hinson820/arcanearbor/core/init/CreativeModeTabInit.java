package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeModeTabInit {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArcaneArbor.MODID);

    public static final Supplier<CreativeModeTab> LUMINWOOD_TAB = CREATIVE_MODE_TABS.register("arcanearbor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.arcanearbor_tab"))
                    .icon(() -> new ItemStack(BlockInit.ARCANE_ENCHANTMENT_TABLE.get()))
                    .displayItems((params, output) -> {
                        output.accept(BlockInit.LUMINWOOD_LOG.get());
                        output.accept(BlockInit.STRIPPED_LUMINWOOD_LOG.get());
                        output.accept(BlockInit.LUMINWOOD_WOOD.get());
                        output.accept(BlockInit.STRIPPED_LUMINWOOD_WOOD.get());
                        output.accept(BlockInit.LUMINWOOD_LEAVES.get());
                        output.accept(BlockInit.LUMINFRUIT_BLOCK.get());
                        output.accept(BlockInit.LUMINWOOD_SAPLING.get());
                        output.accept(BlockInit.ARCANE_ENCHANTMENT_TABLE.get());
                    }).build());

}
