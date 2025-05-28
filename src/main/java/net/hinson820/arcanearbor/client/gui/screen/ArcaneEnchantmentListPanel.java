package net.hinson820.arcanearbor.client.gui.screen;

import com.mojang.blaze3d.vertex.Tesselator;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantment;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentManager;
import net.hinson820.arcanearbor.common.enchantment.ArcaneEnchantmentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArcaneEnchantmentListPanel extends ScrollPanel {

    private final ArcaneEnchantmentScreen parentScreen;
    private final Font font;
    private final List<ArcaneEnchantment> enchantments = new ArrayList<>();
    ItemStack lastItemChecked = ItemStack.EMPTY;
    private ArcaneEnchantment hoveredEnchantment = null;


    private final int entryBoxHeight;
    private final int interEntrySpacing;
    private final int totalEntryHeight;
    private final int entryTextVPadding;
    private final int entryTextHPadding;

    public ArcaneEnchantmentListPanel(Minecraft client, int width, int height, int top, int left, ArcaneEnchantmentScreen parentScreen) {
        super(client, width, height, top, left, 0);

        this.parentScreen = parentScreen;
        this.font = parentScreen.getFont();

        this.entryTextVPadding = 2;
        this.entryTextHPadding = 3;

        this.entryBoxHeight = this.font.lineHeight + (this.entryTextVPadding * 2);
        this.interEntrySpacing = 1;
        this.totalEntryHeight = this.entryBoxHeight + this.interEntrySpacing;
    }

    public void updateList(ItemStack itemToEnchant) {
        this.lastItemChecked = itemToEnchant.copy();
        this.enchantments.clear();

        if (!itemToEnchant.isEmpty()) {
            boolean itemHasAnyArcaneEnchant = ArcaneEnchantmentManager.hasAnyArcaneEnchantment(itemToEnchant);
            if (!itemHasAnyArcaneEnchant) {
                ArcaneArbor.LOGGER.debug("Populating enchantment list for: {}", itemToEnchant.getHoverName().getString());

                List<ArcaneEnchantment> allRegisteredEnchantments = ArcaneEnchantmentRegistry.getAllEnchantments().stream().toList();
                ArcaneArbor.LOGGER.debug("Registry provides {} enchantments: {}",
                        allRegisteredEnchantments.size(),
                        allRegisteredEnchantments.stream().map(ArcaneEnchantment::getId).toList()
                );

                allRegisteredEnchantments.stream()
                        .filter(e -> {
                            ArcaneArbor.LOGGER.debug("Filtering enchantment: {}. Attempting canApplyTo...", e.getId());
                            boolean canApply = e.canApplyTo(itemToEnchant);
                            if (!canApply) {
                                ArcaneArbor.LOGGER.debug(" -> Enchantment {} CANNOT apply to {}", e.getId(), itemToEnchant.getHoverName().getString());
                            } else {
                                ArcaneArbor.LOGGER.debug(" -> Enchantment {} CAN apply to {}", e.getId(), itemToEnchant.getHoverName().getString());
                            }
                            return canApply;
                        })
                        .forEach(e -> {
                            this.enchantments.add(e);
                            ArcaneArbor.LOGGER.debug("Added {} to available list.", e.getId());
                        });

                if (this.enchantments.isEmpty()) {
                    ArcaneArbor.LOGGER.debug("No enchantments found applicable to {} after filtering.", itemToEnchant.getHoverName().getString()); // You see this
                }
            } else {
                ArcaneArbor.LOGGER.debug("Item {} already has an arcane enchantment. List not populated.", itemToEnchant.getHoverName().getString());
            }
        } else {
            ArcaneArbor.LOGGER.debug("Item slot is empty. List not populated.");
        }
    }

    @Override
    protected int getContentHeight() {
        if (this.enchantments.isEmpty()) {
            return this.totalEntryHeight;
        }
        return (this.enchantments.size() * this.totalEntryHeight) - this.interEntrySpacing;
    }

    @Override
    protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {

    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int topYToDrawAt, Tesselator tess, int mouseX, int mouseY) {
        this.hoveredEnchantment = null; // Reset for this frame
        int currentContentRelativeY = 0;
        int panelContentLeftX = this.left + this.border;
        int panelContentWidth = this.width - (this.border * 2);

        if (this.enchantments.isEmpty()) {
            return;
        }

        for (ArcaneEnchantment enchantment : this.enchantments) {
            double effectiveMouseX = mouseX - panelContentLeftX;
            double effectiveMouseY = mouseY - topYToDrawAt;

            int entryVisualTopY = currentContentRelativeY;
            int entryVisualBottomY = currentContentRelativeY + this.entryBoxHeight;

            boolean isMouseOverThisEntry =
                    effectiveMouseX >= 0 && effectiveMouseX < panelContentWidth &&
                            effectiveMouseY >= entryVisualTopY && effectiveMouseY < entryVisualBottomY;

            if (isMouseOverThisEntry) {
                this.hoveredEnchantment = enchantment;
            }

            int actualDrawY = topYToDrawAt + currentContentRelativeY + this.entryTextVPadding + 1;
            int actualDrawX = panelContentLeftX + this.entryTextHPadding;
            boolean isSelected = enchantment == this.parentScreen.getSelectedEnchantment();
            int actualTextColor = isSelected ? 0xFFFFE0 : (isMouseOverThisEntry ? 0xFFFFA0 : 0xFFFFFF);

            guiGraphics.drawString(this.font, enchantment.getBaseDisplayName(), actualDrawX, actualDrawY, actualTextColor, false);

            currentContentRelativeY += this.totalEntryHeight;
        }
    }

    @Override
    protected boolean clickPanel(double panelRelativeMouseX, double panelScrolledRelativeMouseY, int button) {
        if (button != 0) {
            return false;
        }

        int currentContentRelativeY = 0;
        int panelContentWidth = this.width - (this.border * 2);

        for (ArcaneEnchantment enchantment : this.enchantments) {
            int entryVisualTopY = currentContentRelativeY;
            int entryVisualBottomY = currentContentRelativeY + this.entryBoxHeight;

            if (panelRelativeMouseX >= 0 && panelRelativeMouseX < panelContentWidth &&
                    panelScrolledRelativeMouseY >= entryVisualTopY && panelScrolledRelativeMouseY < entryVisualBottomY) {

                ArcaneEnchantment oldSelection = this.parentScreen.getSelectedEnchantment();
                this.parentScreen.setSelectedEnchantment(enchantment);

                this.parentScreen.getMinecraft().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));

                if (oldSelection != this.parentScreen.getSelectedEnchantment()) {
                    this.parentScreen.updateUIDataPublic();
                }
                return true;
            }
            currentContentRelativeY += this.totalEntryHeight;
        }
        return false;
    }

    public void renderPanelTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.hoveredEnchantment != null && this.isMouseOver(mouseX, mouseY)) {
            List<Component> tooltipLines = new ArrayList<>();
            Player player = this.parentScreen.getMinecraftInstance().player;
            ItemStack itemInSlot = this.parentScreen.getMenu().getItemToEnchant();


            tooltipLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".cost_display", this.hoveredEnchantment.getManaCost()) // NEW: Use getManaCost()
                    .withStyle(ChatFormatting.AQUA));


            if (player != null) {
                double successChance = this.hoveredEnchantment.getOverallSuccessChance(itemInSlot, player.level(), player);
                tooltipLines.add(Component.translatable("gui." + ArcaneArbor.MODID + ".success_rate", String.format("%.0f%%", successChance * 100))
                        .withStyle(ChatFormatting.GREEN));
            }


            tooltipLines.addAll(this.hoveredEnchantment.getTooltip(itemInSlot, 1));

            guiGraphics.renderTooltip(this.font, tooltipLines, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected int getScrollAmount() {
        return this.totalEntryHeight;
    }

    @Override
    public NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarrationPriority.FOCUSED;
        } else if (this.isMouseOver(this.parentScreen.getMinecraftInstance().mouseHandler.xpos(),
                this.parentScreen.getMinecraftInstance().mouseHandler.ypos())) {

            return NarrationPriority.HOVERED;
        }
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.enactment_list"));

        if (this.isFocused()) {
            pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("gui.narrate.usage.scroll"));
        }

        ArcaneEnchantment enchantmentToNarrate = null;
        boolean mouseIsOverPanel = this.isMouseOver(this.parentScreen.getMinecraftInstance().mouseHandler.xpos(),
                this.parentScreen.getMinecraftInstance().mouseHandler.ypos());

        if (this.hoveredEnchantment != null && mouseIsOverPanel) {
            enchantmentToNarrate = this.hoveredEnchantment;

            pNarrationElementOutput.add(NarratedElementType.HINT, Component.translatable("gui.narrate.hovered_enactment",
                    enchantmentToNarrate.getBaseDisplayName()));
        } else if (this.isFocused() && this.parentScreen.getSelectedEnchantment() != null) {
            enchantmentToNarrate = this.parentScreen.getSelectedEnchantment();
            pNarrationElementOutput.add(NarratedElementType.HINT, Component.translatable("gui.narrate.selected_enactment",
                    enchantmentToNarrate.getBaseDisplayName()));
        }

        if (enchantmentToNarrate != null) {
            List<Component> narrationDetails = new ArrayList<>();
            int representativeLevel = 1;

            narrationDetails.add(Component.translatable("gui." + ArcaneArbor.MODID + ".cost_display",
                    enchantmentToNarrate.getManaCost()));


            narrationDetails.addAll(enchantmentToNarrate.getTooltip(
                    this.parentScreen.getMenu().getItemToEnchant(),
                    representativeLevel
            ));


            narrationDetails.addAll(enchantmentToNarrate.getTooltip(
                    this.parentScreen.getMenu().getItemToEnchant(),
                    representativeLevel // Provide the representative level
            ));

            for (Component line : narrationDetails) {
                pNarrationElementOutput.add(NarratedElementType.HINT, line);
            }

        } else if (!this.enchantments.isEmpty()) {
            pNarrationElementOutput.add(NarratedElementType.HINT, Component.translatable("gui.narrate.enactment_list_count", this.enchantments.size()));
        }
    }

}