package net.hinson820.arcanearbor.config;

import net.hinson820.arcanearbor.common.mana.PlayerMana;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public final ModConfigSpec.IntValue DEFAULT_MAX_MANA;
    public final ModConfigSpec.IntValue PLAYER_MANA_REGEN_TICK_RATE;
    public final ModConfigSpec.IntValue PLAYER_MANA_REGEN_AMOUNT;

    public final ModConfigSpec.DoubleValue IGNORE_PAIN_L1_ABSORPTION;
    public final ModConfigSpec.DoubleValue IGNORE_PAIN_L2_ABSORPTION;
    public final ModConfigSpec.DoubleValue IGNORE_PAIN_L3_ABSORPTION;
    public final ModConfigSpec.IntValue IGNORE_PAIN_BLEED_SECONDS;
    public final ModConfigSpec.IntValue IGNORE_PAIN_L1_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue IGNORE_PAIN_L2_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue IGNORE_PAIN_L3_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue IGNORE_PAIN_APPLICATION_MANA;

    public final ModConfigSpec.DoubleValue LIFESTEAL_L1_PERCENT;
    public final ModConfigSpec.DoubleValue LIFESTEAL_L2_PERCENT;
    public final ModConfigSpec.DoubleValue LIFESTEAL_L3_PERCENT;
    public final ModConfigSpec.DoubleValue LIFESTEAL_L4_PERCENT;
    public final ModConfigSpec.DoubleValue LIFESTEAL_L5_PERCENT;
    public final ModConfigSpec.IntValue LIFESTEAL_L1_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue LIFESTEAL_L2_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue LIFESTEAL_L3_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue LIFESTEAL_L4_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue LIFESTEAL_L5_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue LIFESTEAL_APPLICATION_MANA;

    public CommonConfig(ModConfigSpec.Builder builder) {
        builder.comment("Arcane Arbor Common Configuration").push("player_mana");
        DEFAULT_MAX_MANA = builder
                .comment("Default maximum mana for a new player when their mana data is first created.")
                .defineInRange("defaultMaxMana", 100, PlayerMana.MIN_MANA_CAP, 100000);
        PLAYER_MANA_REGEN_TICK_RATE = builder
                .comment("How often (in game ticks) player mana regenerates. 20 ticks = 1 second.")
                .defineInRange("manaRegenTickRate", 20, 1, 72000);
        PLAYER_MANA_REGEN_AMOUNT = builder
                .comment("How much mana is regenerated each time the regeneration occurs.")
                .defineInRange("manaRegenAmount", 1, 0, 1000);
        builder.pop();


        builder.comment("Ignore Pain Enchantment Settings").push("ignore_pain");
        IGNORE_PAIN_L1_ABSORPTION = builder
                .comment("Damage absorption percentage for Ignore Pain Level 1 (e.g., 0.20 for 20%).")
                .defineInRange("level1Absorption", 0.20, 0.0, 1.0);
        IGNORE_PAIN_L2_ABSORPTION = builder.defineInRange("level2Absorption", 0.30, 0.0, 1.0);
        IGNORE_PAIN_L3_ABSORPTION = builder.defineInRange("level3Absorption", 0.50, 0.0, 1.0);
        IGNORE_PAIN_BLEED_SECONDS = builder
                .comment("Duration of the bleed effect in seconds.")
                .defineInRange("bleedDurationSeconds", 3, 1, 60);
        IGNORE_PAIN_L1_ACTIVATION_MANA = builder.comment("Mana cost to activate Ignore Pain Level 1 effect.").defineInRange("level1ActivationMana", 20, 0, 1000);
        IGNORE_PAIN_L2_ACTIVATION_MANA = builder.defineInRange("level2ActivationMana", 40, 0, 1000);
        IGNORE_PAIN_L3_ACTIVATION_MANA = builder.defineInRange("level3ActivationMana", 80, 0, 1000);
        IGNORE_PAIN_APPLICATION_MANA = builder.comment("Mana cost to apply Ignore Pain at the table.").defineInRange("level1ApplicationMana", 1500, 0, 50000);
        builder.pop();


        builder.comment("Lifesteal Enchantment Settings").push("lifesteal");
        LIFESTEAL_L1_PERCENT = builder
                .comment("Percentage of damage dealt healed for Lifesteal Level 1 (e.g., 0.10 for 10%).")
                .defineInRange("level1Percent", 0.15, 0.0, 1.0);
        LIFESTEAL_L2_PERCENT = builder.defineInRange("level2Percent", 0.30, 0.0, 1.0);
        LIFESTEAL_L3_PERCENT = builder.defineInRange("level3Percent", 0.50, 0.0, 1.0);
        LIFESTEAL_L4_PERCENT = builder.defineInRange("level4Percent", 0.50, 0.0, 1.0);
        LIFESTEAL_L5_PERCENT = builder.defineInRange("level5Percent", 0.80, 0.0, 1.0);
        LIFESTEAL_L1_ACTIVATION_MANA = builder.comment("Mana cost to activate Lifesteal Level 1 effect.").defineInRange("level1ActivationMana", 40, 0, 1000);
        LIFESTEAL_L2_ACTIVATION_MANA = builder.defineInRange("level2ActivationMana", 80, 0, 1000);
        LIFESTEAL_L3_ACTIVATION_MANA = builder.defineInRange("level3ActivationMana", 120, 0, 1000);
        LIFESTEAL_L4_ACTIVATION_MANA = builder.defineInRange("level2ActivationMana", 180, 0, 1000);
        LIFESTEAL_L5_ACTIVATION_MANA = builder.defineInRange("level3ActivationMana", 250, 0, 1000);
        LIFESTEAL_APPLICATION_MANA = builder.comment("Mana cost to apply Lifesteal at the table.").defineInRange("level1ApplicationMana", 1000, 0, 50000);
        builder.pop();
    }
}
