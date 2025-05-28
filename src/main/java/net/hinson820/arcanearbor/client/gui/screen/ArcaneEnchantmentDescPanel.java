package net.hinson820.arcanearbor.client.gui.screen;

import com.mojang.blaze3d.vertex.Tesselator;
import net.hinson820.arcanearbor.ArcaneArbor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

import java.util.ArrayList;
import java.util.List;

public class ArcaneEnchantmentDescPanel extends ScrollPanel {

    private final ArcaneEnchantmentScreen parentScreen;
    private final Font font;
    private List<Component> currentDescriptionLines = new ArrayList<>();
    private List<FormattedCharSequence> formattedLinesToRender = new ArrayList<>();

    // Dimension constants
    private final int textHorizontalPadding;
    private final int textVerticalPadding;
    private final int lineHeight;
    private final int panelContentWidth;

    public ArcaneEnchantmentDescPanel(Minecraft client, int width, int height, int top, int left, ArcaneEnchantmentScreen parentScreen) {
        super(client, width, height, top, left, 0);
        this.parentScreen = parentScreen;
        this.font = parentScreen.getFont();

        this.textHorizontalPadding = 3;
        this.textVerticalPadding = 3;
        this.lineHeight = this.font.lineHeight + 1;

        this.panelContentWidth = this.width - (this.border * 2) - (this.textHorizontalPadding * 2);
    }

    public void setDescriptionLines(List<Component> newDescriptionLines) {
        this.currentDescriptionLines = new ArrayList<>(newDescriptionLines); // Take a copy
        this.formattedLinesToRender.clear();
        int calculatedContentHeight = 0;

        if (this.currentDescriptionLines.isEmpty()){
            Component noSelection = Component.translatable("gui." + ArcaneArbor.MODID + ".no_enchant_selected_desc");
            this.formattedLinesToRender.addAll(this.font.split(noSelection, this.panelContentWidth));
        } else {
            for (Component line : this.currentDescriptionLines) {
                if (line == null) continue;
                this.formattedLinesToRender.addAll(this.font.split(line, this.panelContentWidth));
            }
        }

        for (FormattedCharSequence ignored : this.formattedLinesToRender) {
            calculatedContentHeight += this.lineHeight;
        }

        if (!this.formattedLinesToRender.isEmpty()) {
            calculatedContentHeight += (this.textVerticalPadding * 2) - this.lineHeight;
        } else {
            calculatedContentHeight = this.lineHeight + (this.textVerticalPadding * 2);
        }

        this.scrollDistance = Math.min(this.scrollDistance, Math.max(0, getContentHeight() - (this.height - this.border * 2))); // Re-clamp scroll
    }

    @Override
    protected int getContentHeight() {
        if (this.formattedLinesToRender.isEmpty()) {
            return this.lineHeight + (this.textVerticalPadding * 2);
        }
        int totalTextHeight = this.formattedLinesToRender.size() * this.lineHeight;

        return Math.max(this.height - this.border * 2, totalTextHeight - (this.lineHeight > 0 ? 1 : 0) + (this.textVerticalPadding * 2));
    }

    @Override
    protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {

    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int topYToDrawAt, Tesselator tess, int mouseX, int mouseY) {
        int currentRelativeY = this.textVerticalPadding;

        int textDrawX = this.left + this.border + this.textHorizontalPadding;

        for (FormattedCharSequence line : this.formattedLinesToRender) {
            int actualDrawY = topYToDrawAt + currentRelativeY;
            guiGraphics.drawString(this.font, line, textDrawX, actualDrawY, 0x404040, false); // Dark gray text
            currentRelativeY += this.lineHeight;
        }
    }

    @Override
    protected boolean clickPanel(double panelRelativeMouseX, double panelScrolledRelativeMouseY, int button) {
        return false;
    }

    @Override
    protected int getScrollAmount() {
        return this.font.lineHeight * 2;
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
        pNarrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.description_panel"));
        if (this.isFocused()) {
            pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("gui.narrate.usage.scroll"));
        }
    }
}
