/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import de.florianmichael.rclasses.common.ColorUtils;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyTrack;
import de.nekosarekawaii.vandalism.util.render.GLStateTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpotifyHUDElement extends HUDElement {

    private final IntegerValue updateInterval = new IntegerValue(
            this,
            "Update Interval",
            "The interval in milliseconds in which the Spotify data should get updated.",
            10000,
            5000,
            30000
    );

    private final IntegerValue textWrapWidth = new IntegerValue(
            this,
            "Text Wrap Width",
            "The width in which the text should get wrapped.",
            320,
            320,
            600
    );

    private final MSTimer updateTimer = new MSTimer();

    private final SpotifyManager spotifyManager;

    public SpotifyHUDElement(final SpotifyManager spotifyManager) {
        super("Spotify", 166, 4, false);
        this.spotifyManager = spotifyManager;
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        final MatrixStack matrices = context.getMatrices();
        final float scale = 0.5f;
        final int fontHeight = this.mc.textRenderer.fontHeight;
        final int heightAddition = this.spotifyManager.isLoggedIn() ? fontHeight * 2 : 0;
        final Map<String, String> infoMap = new LinkedHashMap<>();
        final SpotifyManager spotifyManager = this.spotifyManager;
        if (spotifyManager.isLoggedIn()) {
            final SpotifyTrack spotifyTrack = spotifyManager.getCurrentPlaying();
            if (this.updateTimer.hasReached(this.updateInterval.getValue(), true)) {
                spotifyManager.requestData();
            }
            final boolean paused = spotifyTrack.isPaused();
            final String type = spotifyTrack.getType();
            if (type.length() > 1) {
                infoMap.put("Type", type.substring(0, 1).toUpperCase() + type.substring(1));
            }
            infoMap.put("Name", spotifyTrack.getName());
            infoMap.put("Artists", String.join(", ", spotifyTrack.getArtists()));
            String max = "00:00";
            if (spotifyTrack.getDuration() > 0) {
                final int maxSeconds = (int) Math.ceil(spotifyTrack.getDuration() / 1000d);
                final int maxMinutes = maxSeconds / 60;
                final int maxSecondsRest = maxSeconds % 60;
                max = (maxMinutes < 10 ? "0" : "") + maxMinutes + ":" + (maxSecondsRest < 10 ? "0" : "") + maxSecondsRest;
            }
            final long time = spotifyTrack.getTime();
            final long progress = spotifyTrack.getProgress();
            long currentProgress;
            final long currentTime = System.currentTimeMillis();
            if (paused) {
                currentProgress = spotifyTrack.getLastTime() - (time - progress);
            } else {
                currentProgress = currentTime - (time - progress);
                spotifyTrack.setLastTime(currentTime);
            }
            final int currentSeconds = (int) Math.ceil(Math.min(currentProgress, spotifyTrack.getDuration()) / 1000d);
            final int currentMinutes = currentSeconds / 60;
            final int currentSecondsRest = currentSeconds % 60;
            final String current = (currentMinutes < 10 ? "0" : "") + currentMinutes + ":" + (currentSecondsRest < 10 ? "0" : "") + currentSecondsRest;
            context.fill(
                    this.x,
                    this.y,
                    this.x + this.width,
                    this.y + (fontHeight * infoMap.size()) + heightAddition,
                    Integer.MIN_VALUE
            );
            this.mc.getTextureManager().getTexture(FabricBootstrap.MOD_ICON).setFilter(
                    true,
                    true
            );
            GLStateTracker.BLEND.save(true);
            final int textureX = this.x + 2;
            final int textureY = this.y + 2;
            final int textureSize = 30;
            context.drawTexture(
                    FabricBootstrap.MOD_ICON,
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
                final int pauseRectX1 = textureX + 2;
                final int pauseRectX2 = textureX + textureSize - 2;
                final int pauseRectY1 = textureY + 2;
                final int pauseRectY2 = textureY + textureSize - 2;
                context.fill(
                        pauseRectX1,
                        pauseRectY1,
                        pauseRectX2,
                        pauseRectY2,
                        ColorUtils.toSRGB(0, 0, 0, 0.5f)
                );
                final float alpha = System.currentTimeMillis() % 1000 < 500 ? 0.7f : 0f;
                context.fill(
                        pauseRectX1 + 8,
                        pauseRectY1 + 6,
                        pauseRectX2 - 15,
                        pauseRectY2 - 6,
                        ColorUtils.toSRGB(0, 0, 0, alpha)
                );
                context.fill(
                        pauseRectX1 + 15,
                        pauseRectY1 + 6,
                        pauseRectX2 - 8,
                        pauseRectY2 - 6,
                        ColorUtils.toSRGB(0, 0, 0, alpha)
                );
            }
            final int progressBarY = this.y + (fontHeight * infoMap.size()) + heightAddition - 10;
            final int progressBarOffset = 8;
            final int progressBarStartX = this.x + progressBarOffset;
            final int endWidth = this.width - (progressBarOffset * 2);
            final int progressBarEndX = progressBarStartX + endWidth;
            int progressBarCurrentProgress = (int) (progressBarStartX + (endWidth * (currentProgress / (double) spotifyTrack.getDuration())));
            if (progressBarCurrentProgress > progressBarEndX) {
                progressBarCurrentProgress = progressBarEndX;
            }
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
            matrices.pop();
        } else {
            infoMap.put("Spotify", "Not logged in.");
        }
        final int wrapWidth = this.textWrapWidth.getValue();
        int width = 0, height = 0;
        final int textOffset = 34;
        final int textX = (int) ((this.x + textOffset) / scale);
        final int textY = (int) ((this.y + 4) / scale) + 7;
        matrices.push();
        matrices.scale(scale, scale, 1f);
        for (final Map.Entry<String, String> infoEntry : infoMap.entrySet()) {
            final List<OrderedText> wrappedTexts = this.mc.textRenderer.wrapLines(
                    Text.literal(infoEntry.getKey() + " » " + infoEntry.getValue()),
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
        this.width = (int) (width + textOffset + 1 / scale);
        this.height = (this.spotifyManager.isLoggedIn() ? this.y + (fontHeight * infoMap.size()) + heightAddition - 2 : 0);
    }

}
