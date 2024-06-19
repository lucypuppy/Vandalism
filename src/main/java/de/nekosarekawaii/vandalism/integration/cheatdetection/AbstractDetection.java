/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.cheatdetection;

import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.DetectionPlayer;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public abstract class AbstractDetection implements IName, MinecraftWrapper {

    protected final DetectionPlayer player;
    private final String name;

    private boolean experimental = false;

    public AbstractDetection(final DetectionPlayer player, String name) {
        this.player = player;
        this.name = name;
    }

    protected void verbose(String... debugInfo) {
        final MutableText text = Text.literal(player.getPlayer().getGameProfile().getName() + "got detected by" + getName());

        if (experimental) {
            text.append("*");
        }

        if (debugInfo.length > 0) {
            text.append(" ").append(String.join(" ", debugInfo));
        }

        ChatUtil.chatMessage(text);
    }

    public abstract void onActivate();

    public abstract void onDeactivate();

    @Override
    public String getName() {
        return name;
    }

    public void setExperimental() {
        this.experimental = true;
    }

}
