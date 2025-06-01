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

    public final ModConfigSpec.IntValue EXECUTE_APPLICATION_MANA;
    public final ModConfigSpec.IntValue EXECUTE_L1_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue EXECUTE_L2_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue EXECUTE_L3_ACTIVATION_MANA;

    public final ModConfigSpec.IntValue RAGEBLADE_APPLICATION_MANA;
    public final ModConfigSpec.IntValue RAGEBLADE_L1_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue RAGEBLADE_L2_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue RAGEBLADE_L3_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue RAGEBLADE_L4_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue RAGEBLADE_STACK_DECAY_TICKS;

    public final ModConfigSpec.IntValue FULLCRITICAL_APPLICATION_MANA;
    public final ModConfigSpec.IntValue FULLCRITICAL_L1_ACTIVATION_MANA;

    public final ModConfigSpec.IntValue CHILL_APPLICATION_MANA;
    public final ModConfigSpec.IntValue CHILL_L1_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue CHILL_L2_ACTIVATION_MANA;
    public final ModConfigSpec.IntValue CHILL_L3_ACTIVATION_MANA;

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


        builder.comment("Execute Enchantment Settings").push("execute");
        EXECUTE_APPLICATION_MANA = builder
                .comment("Mana cost to apply the Execute enchantment at the enchanting table.")
                .defineInRange("applicationMana", 1500, 0, 50000); // One application cost
        EXECUTE_L1_ACTIVATION_MANA = builder
                .comment("Mana cost to activate Execute Level 1 effect per execution attempt.")
                .defineInRange("level1ActivationMana", 100, 0, 1000);
        EXECUTE_L2_ACTIVATION_MANA = builder
                .defineInRange("level2ActivationMana", 150, 0, 1000);
        EXECUTE_L3_ACTIVATION_MANA = builder
                .defineInRange("level3ActivationMana", 250, 0, 1000);
        builder.pop();


        builder.comment("Attack Speed (Rageblade) Enchantment Settings").push("rageblade");
        RAGEBLADE_APPLICATION_MANA = builder
                .comment("Mana cost to apply the Rageblade enchantment at the enchanting table.")
                .defineInRange("applicationMana", 2000, 0, 50000);
        RAGEBLADE_L1_ACTIVATION_MANA = builder
                .comment("Mana cost per hit to gain/refresh a Rageblade Level 1 stack.")
                .defineInRange("level1ActivationMana", 10, 0, 1000);
        RAGEBLADE_L2_ACTIVATION_MANA = builder
                .defineInRange("level2ActivationMana", 15, 0, 1000);
        RAGEBLADE_L3_ACTIVATION_MANA = builder
                .defineInRange("level3ActivationMana", 20, 0, 1000);
        RAGEBLADE_L4_ACTIVATION_MANA = builder
                .defineInRange("level4ActivationMana", 25, 0, 1000);
        RAGEBLADE_STACK_DECAY_TICKS = builder
                .comment("How long Rageblade stacks last without attacking, in ticks (20 ticks = 1 second).")
                .defineInRange("stackDecayTicks", 60, 20, 600);
        builder.pop();


        builder.comment("Full Critical Enchantment Settings (Only Level 1)").push("full_critical");
        FULLCRITICAL_APPLICATION_MANA = builder
                .comment("Mana cost to apply the Full Critical enchantment at the enchanting table.")
                .defineInRange("applicationMana", 2500, 0, 50000);
        FULLCRITICAL_L1_ACTIVATION_MANA = builder
                .comment("Mana cost per hit to guarantee a critical hit with Full Critical Level 1.")
                .defineInRange("level1ActivationMana", 25, 0, 1000);
        builder.pop();


        builder.comment("Chill Enchantment Settings").push("chill");
        CHILL_APPLICATION_MANA = builder
                .comment("Mana cost to apply the Chill enchantment at the enchanting table.")
                .defineInRange("applicationMana", 1000, 0, 50000);
        CHILL_L1_ACTIVATION_MANA = builder
                .comment("Mana cost per hit to apply Chill Level 1 (Slowness I for 1s).")
                .defineInRange("level1ActivationMana", 10, 0, 1000);
        CHILL_L2_ACTIVATION_MANA = builder
                .defineInRange("level2ActivationMana", 15, 0, 1000);
        CHILL_L3_ACTIVATION_MANA = builder
                .defineInRange("level3ActivationMana", 25, 0, 1000);
        builder.pop();


    }
}
