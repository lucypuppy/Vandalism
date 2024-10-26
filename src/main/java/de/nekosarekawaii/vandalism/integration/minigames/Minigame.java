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

package de.nekosarekawaii.vandalism.integration.minigames;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Pair;


public abstract class Minigame implements IName, MinecraftWrapper {

    private final String name;
    private final String description;
    private final String author;

    private final MSTimer infoSoundResetTimer;
    private boolean isPlayingInfoSound;

    public Minigame(final String name, final String description, final String author) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.infoSoundResetTimer = new MSTimer();
        this.isPlayingInfoSound = false;
    }

    public void onStart() {
    }

    public void onClose() {
    }

    public abstract void onRender(final DrawContext context, final int mouseX, final int mouseY, final float startX, final float startY, final float endX, final float endY, final int width, final int height);

    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
    }

    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthor() {
        return this.author;
    }

    public Pair<Integer, Boolean> getTextureId() {
        return new Pair<>(-1, false);
    }

    public boolean isPlayingInfoSound() {
        if (this.infoSoundResetTimer.hasReached(this.getInfoSoundPlayingTime(), true)) {
            this.isPlayingInfoSound = false;
        }
        return this.isPlayingInfoSound;
    }

    public void startPlayingInfoSound() {
        this.isPlayingInfoSound = true;
        this.playInfoSound();
    }

    protected void playInfoSound() {
    }

    protected long getInfoSoundPlayingTime() {
        return 1000;
    }

    public abstract void save(final JsonObject configNode);

    public abstract void load(final JsonObject configNode);

}
