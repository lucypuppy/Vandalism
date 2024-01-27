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

import de.florianmichael.rclasses.math.geometry.Alignment;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ButtonValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyManager;
import de.nekosarekawaii.vandalism.integration.spotify.SpotifyTrack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

    private final BooleanValue shadow = new BooleanValue(
            this,
            "Shadow",
            "Whether or not the text should have a shadow.",
            true
    );

    private final ColorValue color = new ColorValue(
            this,
            "Color",
            "The color of the text.",
            Color.WHITE
    );

    private final IntegerValue textWrapWidth = new IntegerValue(
            this,
            "Text Wrap Width",
            "The width in which the text should get wrapped.",
            300,
            300,
            600
    );

    private final StringValue clientId = new StringValue(
            this,
            "Client ID",
            "The client id of your Spotify application.",
            ""
    ).onValueChange((oldValue, newValue) -> {
        if (!oldValue.equals(newValue)) {
            Vandalism.getInstance().getSpotifyManager().logout();
        }
    });

    private final StringValue clientSecret = new StringValue(
            this,
            "Client Secret",
            "The client secret of your Spotify application.",
            "",
            true
    ).onValueChange((oldValue, newValue) -> {
        if (!oldValue.equals(newValue)) {
            Vandalism.getInstance().getSpotifyManager().logout();
        }
    });

    private final ButtonValue spotifyLogin = new ButtonValue(
            this,
            "Spotify Login",
            "Logs you into Spotify.",
            buttonValue -> Vandalism.getInstance().getSpotifyManager().login()
    ).visibleCondition(() -> {
        final boolean hasClientId = !this.clientId.getValue().isEmpty();
        final boolean hasClientSecret = !this.clientSecret.getValue().isEmpty();
        final boolean isSpotifyLoggedIn = Vandalism.getInstance().getSpotifyManager().isLoggedIn();
        return hasClientId && hasClientSecret && !isSpotifyLoggedIn;
    });

    private final ButtonValue spotifyLogout = new ButtonValue(
            this,
            "Spotify Logout",
            "Logs you out of Spotify.",
            buttonValue -> Vandalism.getInstance().getSpotifyManager().logout()
    ).visibleCondition(() -> Vandalism.getInstance().getSpotifyManager().isLoggedIn());

    private final MSTimer updateTimer = new MSTimer();

    public SpotifyHUDElement() {
        super("Spotify", 180, 4, false);
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        Vandalism.getInstance().getSpotifyManager().setClientId(this.clientId.getValue());
        Vandalism.getInstance().getSpotifyManager().setClientSecret(this.clientSecret.getValue());
        final Map<String, String> infoMap = new LinkedHashMap<>();
        final SpotifyManager spotifyManager = Vandalism.getInstance().getSpotifyManager();
        if (spotifyManager.isLoggedIn()) {
            final SpotifyTrack spotifyTrack = spotifyManager.getCurrentPlaying();
            if (this.updateTimer.hasReached(this.updateInterval.getValue(), true)) {
                spotifyManager.requestData();
            }
            final String spotifyTrackType = spotifyTrack.isAd() ? "Ad" : "Track";
            final boolean paused = spotifyTrack.isPaused();
            infoMap.put("Spotify " + spotifyTrackType + " State", paused ? "Paused" : "Playing");
            infoMap.put("Spotify " + spotifyTrackType + " Name", spotifyTrack.getName());
            final StringBuilder artists = new StringBuilder();
            for (final String artist : spotifyTrack.getArtists()) {
                artists.append(artist).append(", ");
            }
            if (!artists.isEmpty()) {
                infoMap.put("Spotify " + spotifyTrackType + " Artists", artists.substring(0, artists.length() - 2));
            }
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
            if (paused) {
                currentProgress = progress;
            } else {
                currentProgress = System.currentTimeMillis() - (time - progress);
            }
            final int currentSeconds = (int) Math.ceil(Math.min(currentProgress, spotifyTrack.getDuration()) / 1000d);
            final int currentMinutes = currentSeconds / 60;
            final int currentSecondsRest = currentSeconds % 60;
            final String current = (currentMinutes < 10 ? "0" : "") + currentMinutes + ":" + (currentSecondsRest < 10 ? "0" : "") + currentSecondsRest;
            infoMap.put("Spotify " + spotifyTrackType + " Progress", current + " / " + max);
        } else {
            infoMap.put("Spotify", "Not logged in.");
        }
        int width = 0, height = 0;
        final int fontHeight = this.mc.textRenderer.fontHeight;
        final int wrapWidth = this.textWrapWidth.getValue();
        for (final Map.Entry<String, String> infoEntry : infoMap.entrySet()) {
            if (this.alignmentX == Alignment.MIDDLE) {
                final String[] infoParts = new String[]{infoEntry.getKey(), infoEntry.getValue()};
                for (int i = 0; i < infoParts.length; i++) {
                    final List<OrderedText> wrappedTexts = this.mc.textRenderer.wrapLines(
                            Text.literal((i == 0 ? Formatting.UNDERLINE : "") + infoParts[i]),
                            wrapWidth
                    );
                    for (final OrderedText text : wrappedTexts) {
                        final int textWidth = this.mc.textRenderer.getWidth(text);
                        this.drawText(
                                context,
                                text,
                                (this.x + this.width / 2) - textWidth / 2,
                                this.y + height
                        );
                        height += fontHeight + 3;
                        if (textWidth > width) {
                            width = textWidth;
                        }
                    }
                }
            } else {
                switch (this.alignmentX) {
                    case LEFT -> {
                        final List<OrderedText> wrappedTexts = this.mc.textRenderer.wrapLines(
                                Text.literal(infoEntry.getKey() + " » " + infoEntry.getValue()),
                                wrapWidth
                        );
                        for (final OrderedText text : wrappedTexts) {
                            final int textWidth = this.mc.textRenderer.getWidth(text);
                            this.drawText(context, text, this.x, this.y + height);
                            height += fontHeight;
                            if (textWidth > width) {
                                width = textWidth;
                            }
                        }
                    }
                    case RIGHT -> {
                        final List<OrderedText> wrappedTexts = this.mc.textRenderer.wrapLines(
                                Text.literal(infoEntry.getValue() + " « " + infoEntry.getKey()),
                                wrapWidth
                        );
                        for (final OrderedText text : wrappedTexts) {
                            final int textWidth = this.mc.textRenderer.getWidth(text);
                            this.drawText(context, text, (this.x + this.width) - textWidth, this.y + height);
                            height += fontHeight;
                            if (textWidth > width) {
                                width = textWidth;
                            }
                        }
                    }
                    default -> {
                    }
                }
            }
        }
        this.width = width;
        this.height = height;
    }

    private void drawText(final DrawContext context, final OrderedText text, final int x, final int y) {
        context.drawText(
                this.mc.textRenderer,
                text,
                x,
                y,
                this.color.getColor(-y * 20).getRGB(),
                this.shadow.getValue()
        );
    }

}
