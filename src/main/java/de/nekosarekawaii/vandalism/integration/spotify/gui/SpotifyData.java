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

package de.nekosarekawaii.vandalism.integration.spotify.gui;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class SpotifyData {

    public static final Identifier IMAGE_IDENTIFIER = Identifier.of(FabricBootstrap.MOD_ID, "spotify_track.png");

    private String type;
    private String name;
    private String artists;
    private NativeImageBackedTexture image;
    private String imageUrl;
    private long time;
    private long lastTime;
    private long progress;
    private long duration;
    private boolean paused;

    public SpotifyData() {
        this.type = "";
        this.name = "";
        this.artists = "";
        this.image = null;
        this.imageUrl = "";
        this.time = 0L;
        this.lastTime = 0L;
        this.duration = 0L;
        this.progress = 0L;
        this.paused = true;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getArtists() {
        return this.artists;
    }

    public void setArtists(final String artists) {
        this.artists = artists;
    }

    public NativeImageBackedTexture getImage() {
        return this.image;
    }

    public void setImage(final NativeImageBackedTexture image) {
        this.image = image;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(final long time) {
        this.time = time;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(final long lastTime) {
        this.lastTime = lastTime;
    }

    public long getProgress() {
        return this.progress;
    }

    public void setProgress(final long progress) {
        this.progress = progress;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

}