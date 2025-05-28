package net.hinson820.arcanearbor.client.gui;

import net.hinson820.arcanearbor.ArcaneArbor;
import net.hinson820.arcanearbor.common.mana.ManaManager;
import net.hinson820.arcanearbor.common.mana.PlayerMana;
import net.hinson820.arcanearbor.core.init.ManaAttachment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public class ManaHudOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation MANA_BAR_BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "textures/gui/mana_bar_background.png");
    private static final ResourceLocation MANA_BAR_PROGRESS_TEXTURE = ResourceLocation.fromNamespaceAndPath(ArcaneArbor.MODID, "textures/gui/mana_bar_progress.png");

    private static final int TEXTURE_WIDTH = 5;
    private static final int TEXTURE_HEIGHT = 182;

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int LABEL_TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_PADDING_FROM_BAR = 5;
    private static final int TEXT_LINE_SPACING = 2;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || player.isSpectator() || mc.options.hideGui) {
            return;
        }

        PlayerMana manaData = ManaManager.getManaData(player);

        if (manaData == null || manaData.getMaxMana() <= 0) {
            return;
        }

        int currentMana = manaData.getMana();
        int maxMana = manaData.getMaxMana();
        float manaNormalized = manaData.getManaNormalized();

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int barMarginFromEdge = 20;

        int barX = screenWidth - barMarginFromEdge - TEXTURE_WIDTH;

        int barTopY = (screenHeight / 2) - (TEXTURE_HEIGHT / 2);

        guiGraphics.blit(MANA_BAR_BACKGROUND_TEXTURE, barX, barTopY, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int filledPixelHeight = (int) (manaNormalized * TEXTURE_HEIGHT);
        if (filledPixelHeight > 0) {
            int sourceU = 0;
            int sourceV = TEXTURE_HEIGHT - filledPixelHeight;
            int blitWidth = TEXTURE_WIDTH;
            int blitHeight = filledPixelHeight;
            int filledOnScreenY = barTopY + (TEXTURE_HEIGHT - filledPixelHeight);
            guiGraphics.blit(MANA_BAR_PROGRESS_TEXTURE,
                    barX, filledOnScreenY,
                    sourceU, sourceV,
                    blitWidth, blitHeight,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        Font font = mc.font;

        String manaLabelText = "Mana:";
        String manaNumbersText = currentMana + "/" + maxMana;

        int labelTextWidth = font.width(manaLabelText);
        int numbersTextWidth = font.width(manaNumbersText);

        int widestTextWidth = Math.max(labelTextWidth, numbersTextWidth);
        int textBlockX = barX - TEXT_PADDING_FROM_BAR - widestTextWidth;

        int totalTextBlockHeight = font.lineHeight + TEXT_LINE_SPACING + font.lineHeight;

        int labelTextY = barTopY + (TEXTURE_HEIGHT / 2) - (totalTextBlockHeight / 2);

        int numbersTextY = labelTextY + font.lineHeight + TEXT_LINE_SPACING;

        guiGraphics.drawString(font, manaLabelText, textBlockX + (widestTextWidth - labelTextWidth), labelTextY, LABEL_TEXT_COLOR, true);
        guiGraphics.drawString(font, manaNumbersText, textBlockX + (widestTextWidth - numbersTextWidth), numbersTextY, TEXT_COLOR, true);
    }
}

