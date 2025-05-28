package net.hinson820.arcanearbor.common.enchantment;

public enum ArcaneEnchantApplyResultType {
    SUCCESS,
    FAILURE_ITEM_SAFE,
    FAILURE_ITEM_BROKEN,
    PREREQUISITES_NOT_MET,
    NO_VALID_LEVEL_ROLL,
    CANNOT_APPLY_TO_ITEM,
    NOT_ENOUGH_MANA,
    ALREADY_HAS_ARCANE_ENCHANTMENT,
    INTERNAL_ERROR
}