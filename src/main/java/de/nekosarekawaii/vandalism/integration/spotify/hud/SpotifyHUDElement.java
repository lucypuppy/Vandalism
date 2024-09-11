/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.spotify.hud;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.integration.spotify.gui.SpotifyData;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import de.nekosarekawaii.vandalism.util.render.util.GLStateTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpotifyHUDElement extends HUDElement {

    private final IntegerValue textWrapWidth = new IntegerValue(
            this,
            "Text Wrap Width",
            "The width in which the text should get wrapped.",
            188,
            188,
            600
    );

    private final SpotifyManager spotifyManager;

    public SpotifyHUDElement(final SpotifyManager spotifyManager) {
        super("Spotify", true, AlignmentX.RIGHT, AlignmentY.BOTTOM, false);
        this.spotifyManager = spotifyManager;
    }

    @Override
    public int getX() {
        final int scaledWidth = this.mc.getWindow().getScaledWidth();
        final int offset = 2;
        final int x;
        switch (this.alignmentX.getValue()) {
            case RIGHT -> x = scaledWidth - this.width - offset;
            case LEFT -> x = offset;
            default -> x = (scaledWidth - this.width) / 2;
        }
        return x + this.xOffset.getValue();
    }

    @Override
    protected void onRender(final DrawContext context, final float delta, final boolean inGame) {
        final SpotifyManager spotifyManager = this.spotifyManager;
        spotifyManager.update();
        final SpotifyData spotifyData = spotifyManager.getCurrentSpotifyData();
        final boolean paused = spotifyData.isPaused();
        final String type = spotifyData.getType();
        final boolean typeEmpty = type.isEmpty();
        final String name = spotifyData.getName();
        final boolean nameEmpty = name.isEmpty();
        final String artists = spotifyData.getArtists();
        final boolean artistsEmpty = artists.isEmpty();
        if (inGame && typeEmpty && nameEmpty && artistsEmpty) {
            return;
        }
        int width = 0, height = 0;
        final MatrixStack matrices = context.getMatrices();
        final float scale = 0.5f;
        final int fontHeight = this.mc.textRenderer.fontHeight;
        final int heightAddition = fontHeight * 2;
        final Map<String, String> infoMap = new LinkedHashMap<>();
        final String waitingForData = "Waiting for data...";
        infoMap.put("Type", !typeEmpty ? type : waitingForData);
        infoMap.put("Name", !nameEmpty ? name : waitingForData);
        infoMap.put("Artists", !artistsEmpty ? artists : waitingForData);
        String max = "00:00";
        if (spotifyData.getDuration() > 0) {
            final int maxSeconds = (int) Math.ceil(spotifyData.getDuration() / 1000d);
            final int maxMinutes = maxSeconds / 60;
            final int maxSecondsRest = maxSeconds % 60;
            max = (maxMinutes < 10 ? "0" : "") + maxMinutes + ":" + (maxSecondsRest < 10 ? "0" : "") + maxSecondsRest;
        }
        final long time = spotifyData.getTime();
        final long progress = spotifyData.getProgress();
        long currentProgress;
        final long currentTime = System.currentTimeMillis();
        if (paused) {
            currentProgress = spotifyData.getLastTime() - (time - progress);
        } else {
            currentProgress = currentTime - (time - progress);
            spotifyData.setLastTime(currentTime);
        }
        final int currentSeconds = (int) Math.ceil(Math.min(currentProgress, spotifyData.getDuration()) / 1000d);
        final int currentMinutes = currentSeconds / 60;
        final int currentSecondsRest = currentSeconds % 60;
        final String current = (currentMinutes < 10 ? "0" : "") + currentMinutes + ":" + (currentSecondsRest < 10 ? "0" : "") + currentSecondsRest;
        context.fill(
                this.getX(),
                this.getY(),
                this.getX() + this.width,
                this.getY() + (fontHeight * infoMap.size()) + heightAddition,
                Integer.MIN_VALUE
        );
        Identifier imageIdentifier = FabricBootstrap.MOD_ICON;
        AbstractTexture image = this.mc.getTextureManager().getTexture(imageIdentifier);
        if (spotifyData.getImage() != null) {
            imageIdentifier = SpotifyData.IMAGE_IDENTIFIER;
            image = spotifyData.getImage();
        }
        image.setFilter(
                true,
                true
        );
        GLStateTracker.BLEND.save(true);
        final int textureX = this.getX() + 2;
        final int textureY = this.getY() + 2;
        final int textureSize = 30;
        context.drawTexture(
                imageIdentifier,
                textureX,
                textureY,
                0,
                0,
                textureSize,
                textureSize,
                textureSize,
                textureSize
        );
        GLStateTracker.BLEND.revert();
        if (paused) {
            context.fill(
                    textureX,
                    textureY,
                    textureX + textureSize,
                    textureY + textureSize,
                    ColorUtils.toSRGB(0, 0, 0, 0.6f)
            );
            final float alpha = System.currentTimeMillis() % 1000 < 500 ? 0.7f : 0f;
            context.fill(
                    textureX + 10,
                    textureY + 8,
                    textureX + textureSize - 17,
                    textureY + textureSize - 8,
                    ColorUtils.toSRGB(1f, 1f, 1f, alpha)
            );
            context.fill(
                    textureX + 17,
                    textureY + 8,
                    textureX + textureSize - 10,
                    textureY + textureSize - 8,
                    ColorUtils.toSRGB(1f, 1f, 1f, alpha)
            );
        }
        final int progressBarY = this.getY() + (fontHeight * infoMap.size()) + heightAddition - 10;
        final int progressBarOffset = 8;
        final int progressBarStartX = this.getX() + progressBarOffset;
        final int endWidth = this.width - (progressBarOffset * 2);
        final int progressBarEndX = progressBarStartX + endWidth;
        int progressBarCurrentProgress = (int) (progressBarStartX + (endWidth * (Math.max(1, currentProgress) / (double) spotifyData.getDuration())));
        if (progressBarCurrentProgress > progressBarEndX) {
            progressBarCurrentProgress = progressBarEndX;
        }
        context.drawHorizontalLine(
                progressBarStartX,
                progressBarEndX - 1,
                progressBarY,
                Color.GRAY.getRGB()
        );
        context.drawHorizontalLine(
                progressBarStartX,
                progressBarCurrentProgress,
                progressBarY,
                Color.GREEN.getRGB()
        );
        matrices.push();
        matrices.scale(scale, scale, 1f);
        context.drawVerticalLine(
                (int) (progressBarCurrentProgress / scale) + 1,
                (int) (progressBarY / scale) - 4,
                (int) ((progressBarY + 4) / scale),
                Color.WHITE.getRGB()
        );
        context.drawVerticalLine(
                (int) (progressBarStartX / scale),
                (int) (progressBarY / scale) - 4,
                (int) ((progressBarY + 4) / scale),
                Color.GRAY.getRGB()
        );
        context.drawCenteredTextWithShadow(
                this.mc.textRenderer,
                current,
                (int) (progressBarStartX / scale),
                (int) ((progressBarY + 5) / scale),
                Color.WHITE.getRGB()
        );
        context.drawVerticalLine(
                (int) (progressBarEndX / scale),
                (int) (progressBarY / scale) - 4,
                (int) ((progressBarY + 4) / scale),
                Color.GRAY.getRGB()
        );
        context.drawCenteredTextWithShadow(
                this.mc.textRenderer,
                max,
                (int) (progressBarEndX / scale),
                (int) ((progressBarY + 5) / scale),
                Color.WHITE.getRGB()
        );
        final int wrapWidth = this.textWrapWidth.getValue();
        final int textOffset = 34;
        final int textX = (int) ((this.getX() + textOffset) / scale);
        final int textY = (int) ((this.getY() + 4) / scale) + 7;
        for (final Map.Entry<String, String> infoEntry : infoMap.entrySet()) {
            final List<OrderedText> wrappedTexts = this.mc.textRenderer.wrapLines(
                    Text.literal(infoEntry.getKey() + ": " + infoEntry.getValue()),
                    (int) (wrapWidth / scale)
            );
            for (final OrderedText text : wrappedTexts) {
                final int textWidth = (int) (this.mc.textRenderer.getWidth(text) * scale);
                context.drawTextWithShadow(this.mc.textRenderer, text, textX, textY + height, -1);
                height += (int) (fontHeight / scale) - 4;
                if (textWidth > width) {
                    width = textWidth;
                }
            }
        }
        matrices.pop();
        this.width = Math.max((int) (width + textOffset + 1 / scale), 160);
        this.height = (fontHeight * infoMap.size()) + heightAddition;
    }

}
