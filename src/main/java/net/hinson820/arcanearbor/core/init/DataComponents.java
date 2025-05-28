package net.hinson820.arcanearbor.core.init;

import com.mojang.serialization.Codec;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ArcaneArbor.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<String, Integer>>> ARCANE_ENCHANTMENTS =
            DATA_COMPONENT_TYPES.register("arcane_enchantments", () ->
                    DataComponentType.<Map<String, Integer>>builder()
                            .persistent(Codec.unboundedMap(Codec.STRING, Codec.INT))
                            .networkSynchronized(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT))
                            .build()
            );
}