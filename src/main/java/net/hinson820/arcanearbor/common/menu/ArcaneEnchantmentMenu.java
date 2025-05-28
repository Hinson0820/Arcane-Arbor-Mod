package net.hinson820.arcanearbor.common.menu;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.block.entity.ArcaneEnchantmentTableBlockEntity;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.hinson820.arcanearbor.core.init.MenuTypesInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ArcaneEnchantmentMenu extends AbstractContainerMenu {
    public final ArcaneEnchantmentTableBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private static final int EQUIP_SLOT_X = 85;
    private static final int EQUIP_SLOT_Y = 138;

    private static final int CONSUMABLE_C1_X = 158;
    private static final int CONSUMABLE_C1_Y = 106;
    private static final int CONSUMABLE_C2_X = 158 + 18 + 2; // 177
    private static final int CONSUMABLE_C2_Y = 106;
    private static final int CONSUMABLE_C3_X = 178 + 18 + 2; // 197
    private static final int CONSUMABLE_C3_Y = 106;

    private static final int PLAYER_INV_START_X = 48;
    private static final int PLAYER_INV_START_Y = 162;
    private static final int PLAYER_HOTBAR_Y = 220;


    public ArcaneEnchantmentMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, playerInventory, getBlockEntity(playerInventory, extraData), new SimpleContainerData(0));
        ArcaneArbor.LOGGER.info("ArcaneEnchantmentMenu: Client constructor called.");
    }

    public ArcaneEnchantmentMenu(int pContainerId, Inventory playerInventory, ArcaneEnchantmentTableBlockEntity blockEntity, ContainerData data) {
        super(MenuTypesInit.ARCANE_ENCHANTMENT_MENU.get(), pContainerId);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.data = data;

        checkContainerSize(playerInventory, 36);

        IItemHandler beItemHandler = blockEntity.getItemHandler();
        if (beItemHandler.getSlots() < ArcaneEnchantmentTableBlockEntity.BE_SLOT_COUNT) {
            throw new IllegalArgumentException("BlockEntity ItemStackHandler has " + beItemHandler.getSlots() +
                    " slots, but menu expects " + ArcaneEnchantmentTableBlockEntity.BE_SLOT_COUNT);
        }

        this.addSlot(new SlotItemHandler(beItemHandler, ArcaneEnchantmentTableBlockEntity.EQUIPMENT_SLOT, EQUIP_SLOT_X, EQUIP_SLOT_Y));
        this.addSlot(new SlotItemHandler(beItemHandler, ArcaneEnchantmentTableBlockEntity.CONSUMABLE_SLOT_1, CONSUMABLE_C1_X, CONSUMABLE_C1_Y));
        this.addSlot(new SlotItemHandler(beItemHandler, ArcaneEnchantmentTableBlockEntity.CONSUMABLE_SLOT_2, CONSUMABLE_C2_X, CONSUMABLE_C2_Y));
        this.addSlot(new SlotItemHandler(beItemHandler, ArcaneEnchantmentTableBlockEntity.CONSUMABLE_SLOT_3, CONSUMABLE_C3_X, CONSUMABLE_C3_Y));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, PLAYER_INV_START_X + j * 18, PLAYER_INV_START_Y + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, PLAYER_INV_START_X + k * 18, PLAYER_HOTBAR_Y));
        }

        addDataSlots(data);
    }

    private static ArcaneEnchantmentTableBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        final BlockPos pos = data.readBlockPos();
        final BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof ArcaneEnchantmentTableBlockEntity aetBe) {
            return aetBe;
        }
        throw new IllegalStateException("Incorrect BlockEntity at " + pos + ". Expected ArcaneEnchantmentTableBlockEntity, got: " + (be != null ? be.getClass().getName() : "null"));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);

        if (slot == null || !slot.hasItem()) {
            return itemstack;
        }

        ItemStack slotStack = slot.getItem();
        itemstack = slotStack.copy();

        final int BE_EQUIP_SLOT_INDEX = ArcaneEnchantmentTableBlockEntity.EQUIPMENT_SLOT; // 0
        final int BE_CONSUMABLE_START_INDEX = ArcaneEnchantmentTableBlockEntity.CONSUMABLE_SLOT_1; // 1
        final int BE_CONSUMABLE_END_INDEX = ArcaneEnchantmentTableBlockEntity.CONSUMABLE_SLOT_3;   // 3

        final int PLAYER_INV_START_INDEX = ArcaneEnchantmentTableBlockEntity.BE_SLOT_COUNT; // 4
        final int PLAYER_INV_END_INDEX = PLAYER_INV_START_INDEX + 27 - 1; // 30
        final int PLAYER_HOTBAR_START_INDEX = PLAYER_INV_END_INDEX + 1; // 31
        final int PLAYER_HOTBAR_END_INDEX = PLAYER_HOTBAR_START_INDEX + 9 - 1; // 39

        if (pIndex <= BE_CONSUMABLE_END_INDEX) {
            if (!this.moveItemStackTo(slotStack, PLAYER_INV_START_INDEX, PLAYER_HOTBAR_END_INDEX + 1, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(slotStack, itemstack);
        } else {
            boolean isEnchantable = true;
            boolean isConsumable = false;

            if (isEnchantable) {
                if (this.moveItemStackTo(slotStack, BE_EQUIP_SLOT_INDEX, BE_EQUIP_SLOT_INDEX + 1, false)) {
                } else if (isConsumable) {
                    if (!this.moveItemStackTo(slotStack, BE_CONSUMABLE_START_INDEX, BE_CONSUMABLE_END_INDEX + 1, false)) {
                        if (pIndex < PLAYER_HOTBAR_START_INDEX) {
                            if (!this.moveItemStackTo(slotStack, PLAYER_HOTBAR_START_INDEX, PLAYER_HOTBAR_END_INDEX + 1, false)) return ItemStack.EMPTY;
                        } else {
                            if (!this.moveItemStackTo(slotStack, PLAYER_INV_START_INDEX, PLAYER_INV_END_INDEX + 1, false)) return ItemStack.EMPTY;
                        }
                    }
                } else {
                    if (pIndex < PLAYER_HOTBAR_START_INDEX) {
                        if (!this.moveItemStackTo(slotStack, PLAYER_HOTBAR_START_INDEX, PLAYER_HOTBAR_END_INDEX + 1, false)) return ItemStack.EMPTY;
                    } else {
                        if (!this.moveItemStackTo(slotStack, PLAYER_INV_START_INDEX, PLAYER_INV_END_INDEX + 1, false)) return ItemStack.EMPTY;
                    }
                }
            } else if (isConsumable) {
                if (!this.moveItemStackTo(slotStack, BE_CONSUMABLE_START_INDEX, BE_CONSUMABLE_END_INDEX + 1, false)) {
                    if (pIndex < PLAYER_HOTBAR_START_INDEX) {
                        if (!this.moveItemStackTo(slotStack, PLAYER_HOTBAR_START_INDEX, PLAYER_HOTBAR_END_INDEX + 1, false)) return ItemStack.EMPTY;
                    } else {
                        if (!this.moveItemStackTo(slotStack, PLAYER_INV_START_INDEX, PLAYER_INV_END_INDEX + 1, false)) return ItemStack.EMPTY;
                    }
                }
            } else {
                if (pIndex < PLAYER_HOTBAR_START_INDEX) {
                    if (!this.moveItemStackTo(slotStack, PLAYER_HOTBAR_START_INDEX, PLAYER_HOTBAR_END_INDEX + 1, false)) return ItemStack.EMPTY;
                } else {
                    if (!this.moveItemStackTo(slotStack, PLAYER_INV_START_INDEX, PLAYER_INV_END_INDEX + 1, false)) return ItemStack.EMPTY;
                }
            }
        }

        if (slotStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(pPlayer, slotStack);
        return itemstack;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, BlockInit.ARCANE_ENCHANTMENT_TABLE.get());
    }

    public ItemStack getItemToEnchant() {
        return this.blockEntity.getItemHandler().getStackInSlot(ArcaneEnchantmentTableBlockEntity.EQUIPMENT_SLOT);
    }
}
