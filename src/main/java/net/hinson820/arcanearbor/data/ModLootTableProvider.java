package net.hinson820.arcanearbor.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(
            PackOutput pOutput,
            Set<ResourceKey<LootTable>> pRequiredTables,
            CompletableFuture<HolderLookup.Provider> pRegistries
    ) {
        super(pOutput,
                pRequiredTables,
                List.of(
                        new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)
                ),
                pRegistries
        );
    }
}
