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

package de.nekosarekawaii.vandalism.integration.spotify;

import java.util.ArrayList;
import java.util.List;

public class SpotifyTrack {

    private boolean isAd;
    private String name;
    private final List<String> artists;
    private long time;
    private long progress;
    private long duration;
    private boolean paused;

    public SpotifyTrack() {
        this.isAd = false;
        this.name = "";
        this.artists = new ArrayList<>();
        this.time = 0L;
        this.duration = 0L;
        this.progress = 0L;
        this.paused = true;
    }

    public boolean isAd() {
        return this.isAd;
    }

    public void setAd(final boolean isAd) {
        this.isAd = isAd;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getArtists() {
        return this.artists;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(final long time) {
        this.time = time;
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