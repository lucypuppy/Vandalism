/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.hud.impl;

import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.effect.fill.BackgroundFillEffect;
import de.nekosarekawaii.vandalism.util.render.effect.fill.GaussianBlurFillEffect;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;

import java.awt.*;

public class ScoreboardHUDElement extends HUDElement {

    private final BooleanValue showRedNumbers = new BooleanValue(this, "Show Red Numbers", "Enables Or disables the red numbers", true);

    private final ColorValue backgroundColor = new ColorValue(
            this,
            "Background Color",
            "Color of the hotbar background.",
            new Color(168, 10, 225, 150)
    );

    private final BooleanValue blurEnabled = new BooleanValue(
            this,
            "Blur",
            "Enable / Disable hotbar blur.",
            true
    );

    public ScoreboardHUDElement() {
        super("Scoreboard", false, AlignmentX.RIGHT, AlignmentY.MIDDLE);
    }

    @Override
    protected void onRender(final DrawContext context, final float delta, final boolean inGame) {
        if (!inGame)
            return;

        final Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreboardObjective scanObjective = null;
        final Team team = scoreboard.getScoreHolderTeam(mc.player.getNameForScoreboard());

        if (team != null) {
            final ScoreboardDisplaySlot scoreboardDisplaySlot = ScoreboardDisplaySlot.fromFormatting(team.getColor());

            if (scoreboardDisplaySlot != null) {
                scanObjective = scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
            }
        }

        final ScoreboardObjective scoreboardObjective = scanObjective != null ? scanObjective : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (scoreboardObjective != null) {
            renderScoreboardSidebar(context, scoreboardObjective);
        }
    }

    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
        final Scoreboard scoreboard = objective.getScoreboard();
        final NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.RED);

        final SidebarEntry[] sidebarEntrys = scoreboard.getScoreboardEntries(objective).stream()
                .filter((score) -> !score.hidden())
                .sorted(InGameHud.SCOREBOARD_ENTRY_COMPARATOR)
                .limit(15L)
                .map((scoreboardEntry) -> {
                    final Team team = scoreboard.getScoreHolderTeam(scoreboardEntry.owner());
                    final Text entryName = Team.decorateName(team, scoreboardEntry.name());
                    final Text score = scoreboardEntry.formatted(numberFormat);
                    final int width = getTextWidth(score);
                    return new SidebarEntry(entryName, score, width);
                })
                .toArray(SidebarEntry[]::new);

        final Text text = objective.getDisplayName();
        int scoreboardWidth = getTextWidth(text);
        int scoreboardHeight = getTextHeight(text);

        int x = 2;
        int y = 2;
        switch (this.alignmentX.getValue()) {
            case MIDDLE -> x = mc.getWindow().getScaledWidth() / 2 - this.width / 2;
            case RIGHT -> x = mc.getWindow().getScaledWidth() - this.width - 7;
        }

        switch (this.alignmentY.getValue()) {
            case MIDDLE -> y = mc.getWindow().getScaledHeight() / 2 - this.height / 2;
            case BOTTOM -> y = mc.getWindow().getScaledHeight() - this.height - 7;
        }

        x += xOffset.getValue();
        y += yOffset.getValue();

        renderBackground(x, y, this.width + 6, this.height);
        drawText(text, context, x + this.width / 2f - getTextWidth(text) / 2f + 2, y + 2f, false, new Color(255, 255, 255).getRGB());

        final int k = getTextWidth(": ");
        for (final SidebarEntry sidebarEntry : sidebarEntrys) {
            drawText(sidebarEntry.name, context, x + 2, y + scoreboardHeight, false, new Color(255, 255, 255).getRGB());

            if (this.showRedNumbers.getValue())
                drawText(sidebarEntry.score, context, x + this.width - sidebarEntry.scoreWidth + 2, y + scoreboardHeight, false, new Color(255, 255, 255).getRGB());

            scoreboardWidth = Math.max(scoreboardWidth, getTextWidth(sidebarEntry.name) + (sidebarEntry.scoreWidth > 0 ? k + sidebarEntry.scoreWidth : 0));
            scoreboardHeight += getTextHeight(sidebarEntry.name);
        }

        this.width = scoreboardWidth;
        this.height = scoreboardHeight;
    }

    private void renderBackground(float x, float y, float width, float height) {
        if (this.blurEnabled.getValue()) {
            final GaussianBlurFillEffect gaussianBlurFillEffect = Shaders.getGaussianBlurFillEffect();
            gaussianBlurFillEffect.setDirections(16.0f);
            gaussianBlurFillEffect.setQuality(8.0f);
            gaussianBlurFillEffect.setRadius(16.0f);
            gaussianBlurFillEffect.setTextureId(mc.getFramebuffer().getColorAttachment());
            gaussianBlurFillEffect.bindMask();
            RenderUtil.drawRoundedRect(x, y, width, height, 8, Color.BLACK);
            gaussianBlurFillEffect.renderScissoredScaled(mc.getFramebuffer(), true, (int) x, (int) y, (int) (width), (int) (height));
        }

        final BackgroundFillEffect backgroundShader = Shaders.getBackgroundFillEffect();
        backgroundShader.setColor(backgroundColor.getColor());
        backgroundShader.bindMask();
        RenderUtil.drawRoundedRect(x, y, width, height, 8, Color.BLACK);
        backgroundShader.renderScissoredScaled(mc.getFramebuffer(), false, (int) x, (int) y, (int) (width), (int) (height));
    }

    private record SidebarEntry(Text name, Text score, int scoreWidth) {
    }

}


