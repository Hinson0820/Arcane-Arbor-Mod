package net.hinson820.arcanearbor.common.enchantment;

public class ArcaneEnchantApplyOutcome {
    public final ArcaneEnchantApplyResultType type;
    public final int appliedLevel;

    public ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType type, int appliedLevel) {
        this.type = type;
        this.appliedLevel = appliedLevel;
    }

    public ArcaneEnchantApplyOutcome(ArcaneEnchantApplyResultType type) {
        this(type, 0);
    }

    public boolean wasSuccessful() {
        return type == ArcaneEnchantApplyResultType.SUCCESS;
    }

    public boolean didItemBreak() {
        return type == ArcaneEnchantApplyResultType.FAILURE_ITEM_BROKEN;
    }
}
