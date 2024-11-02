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

package de.nekosarekawaii.vandalism.integration.cheatdetection;

import de.nekosarekawaii.vandalism.integration.cheatdetection.detectionplayer.DetectionPlayer;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public abstract class Detection implements IName, MinecraftWrapper {

    protected final DetectionPlayer player;
    private final String name;

    private boolean experimental = false;

    public Detection(final DetectionPlayer player, String name) {
        this.player = player;
        this.name = name;
    }

    protected void verbose(String... debugInfo) {
        final MutableText text = Text.literal(player.getPlayer().getGameProfile().getName() + "got detected by" + getName());

        if (experimental) {
            text.append("*");
        }

        if (debugInfo.length > 0) {
            text.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.of(String.join("\n", debugInfo)))));
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
