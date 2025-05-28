package net.hinson820.arcanearbor.common.block.entity;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantApplyOutcome;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.hinson820.arcanearbor.common.menu.ArcaneEnchantmentMenu;
import net.hinson820.arcanearbor.core.init.BlockEntitiesInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class ArcaneEnchantmentTableBlockEntity extends BlockEntity implements MenuProvider {

    public static final int EQUIPMENT_SLOT = 0;
    public static final int CONSUMABLE_SLOT_1 = 1;
    public static final int CONSUMABLE_SLOT_2 = 2;
    public static final int CONSUMABLE_SLOT_3 = 3;
    public static final int BE_SLOT_COUNT = 4;

    private final ItemStackHandler itemHandler = new ItemStackHandler(BE_SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    protected final ContainerData containerData = new SimpleContainerData(0);

    public ArcaneEnchantmentTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesInit.ARCANE_ENCHANTMENT_TABLE_BE.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + ArcaneArbor.MODID + ".arcane_enchantment_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ArcaneEnchantmentMenu(pContainerId, pPlayerInventory, this, this.containerData);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.saveAdditional(pTag, provider);
        pTag.put("inventory", itemHandler.serializeNBT(provider));
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.loadAdditional(pTag, provider);
        if (pTag.contains("inventory")) {
            itemHandler.deserializeNBT(provider, pTag.getCompound("inventory"));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ArcaneEnchantmentTableBlockEntity be) {
        // Not used yet
    }

    public boolean attemptEnchantment(Player player, String enchantmentId) {
        ArcaneEnchantment enchantmentToApply = ArcaneEnchantmentRegistry.get(enchantmentId).orElse(null);
        if (enchantmentToApply == null) {
            player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".enchant_not_found"));
            return false;
        }

        ItemStack originalItemInSlot = this.itemHandler.getStackInSlot(EQUIPMENT_SLOT);

        if (originalItemInSlot.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".no_item_to_enchant"));
            return false;
        }

        ItemStack itemToProcess = originalItemInSlot.copy();

        // --- TODO: Material Requirements Check ---
        // If material check fails:
        // player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".missing_materials"));
        // return false;

        ArcaneEnchantApplyOutcome outcome = ArcaneEnchantmentManager.tryApplyEnchantment(
                itemToProcess,
                enchantmentToApply,
                this.level,
                player
        );

        switch (outcome.type) {
            case SUCCESS:

                int fixedManaCost = enchantmentToApply.getManaCost();
                if (!player.isCreative() && !ManaManager.consumeMana(player, fixedManaCost)) {
                    player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".not_enough_mana", fixedManaCost));
                    return false;
                }

                // TODO: Actually consume materials from CONSUMABLE_SLOT_1, _2, _3 now.

                this.itemHandler.setStackInSlot(EQUIPMENT_SLOT, itemToProcess);
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".enchant_success",
                        enchantmentToApply.getDisplayName(outcome.appliedLevel)));
                ArcaneArbor.LOGGER.info("Player {} successfully enchanted {} with {} Lvl {}", player.getName().getString(), itemToProcess.getHoverName().getString(), enchantmentToApply.getId(), outcome.appliedLevel);
                return true;

            case FAILURE_ITEM_BROKEN:
                this.itemHandler.setStackInSlot(EQUIPMENT_SLOT, ItemStack.EMPTY);
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".enchant_fail_destroyed",
                        originalItemInSlot.getHoverName()));
                ArcaneArbor.LOGGER.info("Player {} failed enchanting {} (item broke) with {}", player.getName().getString(), originalItemInSlot.getHoverName().getString(), enchantmentToApply.getId());
                // TODO: Potentially consume materials here as a penalty/cost (design choice).
                return false;

            case FAILURE_ITEM_SAFE:
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".enchant_fail_safe"));
                ArcaneArbor.LOGGER.info("Player {} failed enchanting {} (item safe) with {}", player.getName().getString(), originalItemInSlot.getHoverName().getString(), enchantmentToApply.getId());
                // TODO: Potentially consume some materials as an attempt cost.
                return false;

            case ALREADY_HAS_ARCANE_ENCHANTMENT:
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".already_has_enchant"));
                return false;

            case CANNOT_APPLY_TO_ITEM:
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".cannot_apply_to_item"));
                return false;

            case PREREQUISITES_NOT_MET:
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".prerequisites_not_met_for_level"));
                return false;

            case NO_VALID_LEVEL_ROLL:
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".enchant_fail_no_level"));
                return false;

            default:
                player.sendSystemMessage(Component.translatable("message." + ArcaneArbor.MODID + ".enchant_fail_unknown"));
                ArcaneArbor.LOGGER.warn("Unknown enchantment failure outcome: {} for player {} with enchantment {}", outcome.type, player.getName().getString(), enchantmentToApply.getId());
                return false;
        }
    }
}