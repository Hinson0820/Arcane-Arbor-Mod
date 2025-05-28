package net.hinson820.arcanearbor.core.init;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.mana.PlayerMana;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ManaAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ArcaneArbor.MODID);

    public static final Supplier<AttachmentType<PlayerMana>> PLAYER_MANA =
            ATTACHMENT_TYPES.register("player_mana",
                    () -> AttachmentType.builder(PlayerMana::new)
                            .serialize(PlayerMana.CODEC)
                            .copyOnDeath()
                            .build()
            );

}