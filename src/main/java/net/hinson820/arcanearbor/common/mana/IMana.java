package net.hinson820.arcanearbor.common.mana;

public interface IMana {
    int getMana();
    void setMana(int mana);
    void addMana(int amount);
    void consumeMana(int amount);

    int getMaxMana();
    void setMaxMana(int maxMana);
    void addMaxMana(int amount);

    default float getManaNormalized() {
        if (getMaxMana() <= 0) {
            return 0;
        }
        return (float) getMana() / getMaxMana();
    }

    // void resetToDefault();
}
