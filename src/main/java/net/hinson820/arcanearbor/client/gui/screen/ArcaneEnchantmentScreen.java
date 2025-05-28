package net.hinson820.arcanearbor.client.gui.screen;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.common.menu.ArcaneEnchantmentMenu;
import net.hinson820.arcanearbor.common.network.PacketEnchantItemAttempt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArcaneEnchantmentScreen extends AbstractContainerScreen<ArcaneEnchantmentMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "textures/gui/arcane_enchantment_table_256x244.png");

    private ArcaneEnchantmentListPanel enchantmentListPanel;
    private ArcaneEnchantmentDescPanel descriptionScrollPanel;
    private Button enchantButton;
    private ArcaneEnchantment selectedEnchantment = null;

    public ArcaneEnchantmentScreen(ArcaneEnchantmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        ArcaneArbor.LOGGER.info("ArcaneEnchantmentScreen: Constructor called. Menu is " + (pMenu == null ? "null" : "not null"));

        this.imageWidth = 256;
        this.imageHeight = 244;
        this.titleLabelY = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int guiLeft = (this.width - this.imageWidth) / 2;
        int guiTop = (this.height - this.imageHeight) / 2;

        int listPanelX = guiLeft + 132;
        int listPanelY = guiTop + 19;
        int listPanelWidth = 109;
        int listPanelHeight = 72;
        this.enchantmentListPanel = new ArcaneEnchantmentListPanel(this.minecraft, listPanelWidth, listPanelHeight, listPanelY, listPanelX, this);
        this.addRenderableWidget(this.enchantmentListPanel);

        int descPanelX = guiLeft + 14;
        int descPanelY = guiTop + 19;
        int descPanelWidth = 109;
        int descPanelHeight = 108;
        this.descriptionScrollPanel = new ArcaneEnchantmentDescPanel(this.minecraft, descPanelWidth, descPanelHeight, descPanelY, descPanelX, this);
        this.addRenderableWidget(this.descriptionScrollPanel);

        // Enchant Button
        int buttonWidth = 65;
        int buttonHeight = 20;
        int buttonX = guiLeft + 118;
        int buttonY = guiTop + 136;
        this.enchantButton = Button.builder(Component.translatable("gui." + ArcaneArbor.MODID + ".arcane_enchantment_table.enchant"),
                        (button) -> {
                            if (selectedEnchantment != null && minecraft != null && minecraft.player != null && this.minecraft.getConnection() != null) {
                                PacketEnchantItemAttempt payload = new PacketEnchantItemAttempt(
                                        this.menu.blockEntity.getBlockPos(),
                                        selectedEnchantment.getId()
                                );

                                this.minecraft.getConnection().send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(payload));
                                ArcaneArbor.LOGGER.debug("Client sent enchant request for: " + selectedEnchantment.getId());
                            }
                        })
                .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                .build();
        this.enchantButton.active = false;
        this.addRenderableWidget(this.enchantButton);

        updateUIData();
    }


    public Font getFont() { return this.font; }
    @Override public ArcaneEnchantmentMenu getMenu() { return this.menu; }
    public Minecraft getMinecraftInstance() { return this.minecraft; }
    public ArcaneEnchantment getSelectedEnchantment() { return this.selectedEnchantment; }
    public void setSelectedEnchantment(ArcaneEnchantment enchantment) { this.selectedEnchantment = enchantment; }

    public void updateUIDataPublic() {
        this.updateUIData();
    }

    private void updateUIData() {
        ItemStack itemInSlot = this.menu.getItemToEnchant();
        Player player = this.minecraft != null ? this.minecraft.player : null;

        if (this.enchantmentListPanel != null) {
            this.enchantmentListPanel.updateList(itemInSlot);
        }

        this.enchantButton.active = selectedEnchantment != null &&
                !itemInSlot.isEmpty() &&
                player != null &&
                selectedEnchantment.canApplyTo(itemInSlot);

        List<Component> descriptionLines = new ArrayList<>();
        if (selectedEnchantment != null && player != null) {
            descriptionLines.add(selectedEnchantment.getBaseDisplayName().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE));
            descriptionLines.add(Component.literal(" "));

            int displayLevel = 1;
            if (selectedEnchantment.getMaxLevel() > 0) {
                descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".showing_stats_for_level", Component.literal("I")).withStyle(ChatFormatting.DARK_GRAY));
                descriptionLines.addAll(selectedEnchantment.getTooltip(itemInSlot, displayLevel));
            } else {
                descriptionLines.addAll(selectedEnchantment.getTooltip(itemInSlot, 1));
            }
            descriptionLines.add(Component.literal(" "));


            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".level_probabilities").withStyle(ChatFormatting.BLUE));
            Map<Integer, Double> probs = selectedEnchantment.getLevelProbabilities();
            if (probs != null && !probs.isEmpty()) {
                probs.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> {
                            descriptionLines.add(Component.literal(String.format("  Lvl %s: %.0f%%",
                                            RomanNumerals.toRoman(entry.getKey()), entry.getValue() * 100))
                                    .withStyle(ChatFormatting.GRAY));
                        });
            } else if (selectedEnchantment.getMaxLevel() == 1) {
                descriptionLines.add(Component.literal(String.format("  Lvl I: 100%%")).withStyle(ChatFormatting.GRAY));
            }
            descriptionLines.add(Component.literal(" "));

            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".cost_display", selectedEnchantment.getManaCost()) // NEW: Use getManaCost()
                    .withStyle(ChatFormatting.AQUA));


            double successChance = selectedEnchantment.getOverallSuccessChance(itemInSlot, player.level(), player);
            double breakChance = selectedEnchantment.getBreakChanceOnFailure(itemInSlot, player.level(), player);
            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".success_rate", String.format("%.0f%%", successChance * 100))
                    .withStyle(ChatFormatting.GREEN));
            if (breakChance > 0) {
                descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".break_chance_on_fail", String.format("%.0f%%", breakChance * 100))
                        .withStyle(ChatFormatting.RED));
            }
            descriptionLines.add(Component.literal(" "));

            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".prerequisites").withStyle(ChatFormatting.YELLOW));
            boolean meetsLvl1Prereqs = selectedEnchantment.checkEnchantmentPrerequisites(player.level(), player);
            Component prereqStatus = meetsLvl1Prereqs ?
                    Component.translatable("gui." + ArcaneArbor.MODID + ".prerequisites.met").withStyle(ChatFormatting.GREEN) :
                    Component.translatable("gui." + ArcaneArbor.MODID + ".prerequisites.not_met").withStyle(ChatFormatting.RED);
            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".prereq_desc." + selectedEnchantment.getId() + ".level1")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

            // --- TODO: Material Display ---
            // descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".materials_required").withStyle(ChatFormatting.GOLD));
            // Iterate through selectedEnchantment.getMaterialRequirements(itemInSlot, player)
            // and display them, perhaps with a checkmark if present in consumable slots.

        } else if (!itemInSlot.isEmpty()) {
            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".select_enchantment").withStyle(ChatFormatting.GRAY));
        } else {
            descriptionLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".insert_item").withStyle(ChatFormatting.GRAY));
        }

        if (this.descriptionScrollPanel != null) {
            this.descriptionScrollPanel.setDescriptionLines(descriptionLines);
        }
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        if (this.enchantmentListPanel != null && this.enchantmentListPanel.isMouseOver(pMouseX, pMouseY)) {
            this.enchantmentListPanel.renderPanelTooltips(pGuiGraphics, pMouseX, pMouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        ItemStack currentItem = this.menu.getItemToEnchant();

        boolean itemChanged = this.enchantmentListPanel != null &&
                !ItemStack.matches(currentItem, enchantmentListPanel.lastItemChecked);

        ArcaneEnchantment previousSelection = this.selectedEnchantment;

        if (itemChanged) {
            this.selectedEnchantment = null; // Reset selection
        }

        if (itemChanged || previousSelection != this.selectedEnchantment) {
            updateUIData();
        }
    }

    public static class RomanNumerals {
        private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        private static final String[] SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        public static String toRoman(int num) {
            if (num < 1 || num > 3999) {
                return Integer.toString(num);
            }
            StringBuilder roman = new StringBuilder();
            int i = 0;
            while (num > 0) {
                while (num >= VALUES[i]) {
                    num -= VALUES[i];
                    roman.append(SYMBOLS[i]);
                }
                i++;
            }
            return roman.toString();
        }
    }

}