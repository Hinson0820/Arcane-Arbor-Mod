package net.hinson820.arcanearbor.common.mana;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hinson820.arcanearbor.config.Configs;

public class PlayerMana implements IMana {
    private int mana;
    private int maxMana;

    public static final int DEFAULT_STARTING_MANA = 0;
    public static final int MIN_MANA_CAP = 10;

    public static final Codec<PlayerMana> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("current_mana").forGetter(PlayerMana::getMana),
                    Codec.INT.fieldOf("max_mana").forGetter(PlayerMana::getMaxMana)
            ).apply(instance, PlayerMana::new)
    );

    public PlayerMana(int mana, int maxMana) {
        this.maxMana = Math.max(MIN_MANA_CAP, maxMana);
        this.mana = Math.max(0, Math.min(mana, this.maxMana));
    }

    public PlayerMana() {
        this(DEFAULT_STARTING_MANA, Configs.COMMON.DEFAULT_MAX_MANA.get());
    }


    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public void setMana(int mana) {
        this.mana = Math.max(0, Math.min(mana, this.maxMana));
    }

    @Override
    public void addMana(int amount) {
        this.setMana(this.mana + amount);
    }

    @Override
    public void consumeMana(int amount) {
        this.setMana(this.mana - amount);
    }

    @Override
    public int getMaxMana() {
        return this.maxMana;
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(MIN_MANA_CAP, maxMana);
        if (this.mana > this.maxMana) {
            this.setMana(this.maxMana);
        }
    }

    @Override
    public void addMaxMana(int amount) {
        this.setMaxMana(this.maxMana + amount);
    }

    // getManaNormalized() is provided by the interface's default method.

    // @Override
    // public void resetToDefault() {
    //     this.mana = 0;
    //     this.maxMana = DEFAULT_MAX_MANA;
    // }
}